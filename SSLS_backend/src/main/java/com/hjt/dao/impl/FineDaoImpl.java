package com.hjt.dao.impl;

import com.hjt.dao.FineDao;
import com.hjt.domain.Fine;
import com.hjt.dto.Result;
import com.hjt.pojo.FineInfo;
import com.hjt.pojo.NameValue;
import com.hjt.utils.JDBCUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;

public class FineDaoImpl implements FineDao {
    private final JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());
    private final TransactionTemplate txTemplate = JDBCUtils.getTransactionTemplate();

    @Override
    public int queryFineCountByUserId(int userId) {
        String sql = "SELECT COUNT(f.status) FROM fine f JOIN borrow bw ON f.borrow_id = bw.id WHERE bw.user_id = ? AND f.status = 0;";
        return template.queryForObject(sql, Integer.class, userId);
    }

    public Fine queryFineByBorrowId(int borrowId) {
        String sql = "SELECT * FROM fine WHERE borrow_id = ?;";
        try {
            return template.queryForObject(sql, new BeanPropertyRowMapper<>(Fine.class), borrowId);
        } catch (Exception e) {
            // 查询结果为空报错
            return null;
        }
    }

    @Override
    public List<FineInfo> queryFineByUserId(int userId) {
        // 一天1440分钟。以分钟为最小精度，不满一天但超过一分钟算一天
        String sql = "SELECT f.id, f.amount, f.date, f.status, bw.borrowDate, bw.dueDate, bw.returnDate, " +
                "CEIL(TIMESTAMPDIFF(MINUTE, bw.dueDate, bw.returnDate) / 1440) AS overdue, " +
                "bk.name AS bookName, bk.authors, bk.imageUrl " +
                "FROM fine f JOIN borrow bw ON f.borrow_id = bw.id JOIN book bk ON bw.book_id = bk.id " +
                "WHERE bw.user_id = ? ORDER BY f.status, f.date DESC, f.id DESC;";
        return template.query(sql, new BeanPropertyRowMapper<>(FineInfo.class), userId);
    }

    @Override
    public Result payFineByFineId(int fineId) {
        try {
            // 查询fine(status)是否为0未缴纳
            String fineStatusSql = "SELECT status FROM fine WHERE id = ?;";
            if (template.queryForObject(fineStatusSql, Integer.class, fineId) != 0)
                return Result.error("罚款已经被缴纳，不要重复缴纳");
            /*
             * 开启事务，还要给select userInfo(balance)的操作加排他锁，mysql的InnoDB默认的事务隔离级别是Repeatable read (可重复读)，
             * innoDB隔离实现MVCC，开启事务时生成一个快照readview，记录的是一个旧值。
             * 即使其他的事务已经commit提交确认，在当前事务下依然是旧值。即无法保证select出来的userInfo(balance)是最新的数据。
             * 我需要先select查userInfo(balance)是否足够再update改余额，这就要求select查出的要是最新的数据，事务期间不允许其他事务修改。
             * 所以隔离级别要提高到Serializable (可串行化)。 悲观锁
             * 用select...for update实现独占，阻塞其他事务的的select...{for update | for share}和update...[for update | for share]
             * 普通的select不受影响。
             */
            return txTemplate.execute(status -> {
                try {
                    // 查询罚款状态
                    // 查询需要缴纳的金额；
                    String amountSql = "SELECT amount FROM fine WHERE id = ?";
                    BigDecimal amount = template.queryForObject(amountSql, BigDecimal.class, fineId);
                    // 悲观锁，独占userInfo(balance)字段。防止在当前事务下其他事务修改了余额（充值，还其他的款）。
                    String balanceSql = "SELECT ui.balance FROM fine f JOIN borrow bw ON f.borrow_id = bw.id JOIN userinfo ui ON bw.user_id = ui.id WHERE f.id = ? FOR UPDATE;";
                    BigDecimal balance = template.queryForObject(balanceSql, BigDecimal.class, fineId);
                    // 判断余额是否足够
                    if (amount.compareTo(balance) <= 0) {
                        // 余额足够开始缴纳罚金
                        /*
                         * 改fine(status),在innodb默认隔离级别下，改的操作会被阻塞，根据最新的数据来更改。所以affectedRow就更具最新的数据来更改的。
                         * 事务在执行update后会更新自己的readview;
                         */
                        String updateFineStatusSql = "UPDATE fine SET status = 1, date = NOW() WHERE id = ?;";
                        int affectedRow = template.update(updateFineStatusSql, fineId);
                        if (affectedRow > 0) {
                            // 修改余额；update join
                            String updateBalanceSql = "UPDATE fine f JOIN borrow bw ON f.borrow_id = bw.id JOIN userinfo ui ON bw.user_id = ui.id \n" +
                                    "SET ui.balance = ui.balance - f.amount\n" +
                                    "WHERE f.id = ?;";
                            // 更新余额
                            template.update(updateBalanceSql, fineId);
                            // 返回余额；
                            return Result.success(template.queryForObject(balanceSql, BigDecimal.class, fineId));
                        } else {
                            // 被其他事务提前修改了。
                            return Result.error("罚款已经被缴纳，不要重复缴纳");
                        }
                    } else {
                        // 余额不足
                        return Result.error("余额不足");
                    }
                } catch (Exception e) {
                    status.setRollbackOnly();
                    return Result.error("缴纳罚款失败");
                }
            });
        } catch (Exception e) {
            // 传入错误的
            return Result.error("未找到罚款信息");
        }
    }

    @Override
    public List<NameValue> queryFineTotalByMonth() {
        String sql = "SELECT DATE_FORMAT(bw.returnDate, '%Y-%M') AS name, SUM(f.amount) AS value " +
                "FROM fine f JOIN borrow bw ON f.borrow_id = bw.id " +
                "WHERE bw.returnDate >=  DATE_SUB(Now(),INTERVAL 1 YEAR) " +
                "GROUP BY name;";
        return template.query(sql, new BeanPropertyRowMapper<>(NameValue.class));
    }
}