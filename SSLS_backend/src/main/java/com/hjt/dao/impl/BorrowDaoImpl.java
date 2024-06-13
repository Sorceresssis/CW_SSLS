package com.hjt.dao.impl;

import com.hjt.dao.BorrowDao;
import com.hjt.pojo.BorrowHistoryInfo;
import com.hjt.pojo.BorrowInfo;
import com.hjt.pojo.BorrowResult;
import com.hjt.pojo.NameValue;
import com.hjt.utils.JDBCUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

public class BorrowDaoImpl implements BorrowDao {
    private final JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());
    private final TransactionTemplate txTemplate = JDBCUtils.getTransactionTemplate();
    // 借阅时长和续借时长都是30天
    private final int BORROW_DURATION = 30;
    private final int RENEW_DURATION = 30;

    @Override
    public List<BorrowResult> toBorrowBooks(int userId, int[] bookIds) {
        // 记录这每本书的借书结果
        List<BorrowResult> results = new ArrayList<>();
        // 每借一本书都开启一个事务: 保证每本书的借阅结果都是独立的
        for (int bookId : bookIds) {
            // 返回 0 表示借书失败， 1 表示成功
            Integer affectedRows = txTemplate.execute(status -> {
                try {
                    // MVCC默认隔离级别下 当前事务完成update的操作后且没有提交，其他事务的update会等待当前事务完成，获才能得排他锁。
                    // 所以update返回的affectedRow是根据最新的已提交的数据来判断的，执行update操作也会更新当前事务的视图ReaderView。
                    String sqlBook = "UPDATE book SET status = '外借' WHERE status = '在库' AND id = ?";
                    // 修改结果为1,说明借书成功，把书加入用户的借阅表
                    if (template.update(sqlBook, bookId) == 1) {
                        String sqlBorrow = "INSERT INTO borrow(user_id, book_id, dueDate) VALUES(?,?,DATE_ADD(NOW(),INTERVAL ? DAY))";
                        if (template.update(sqlBorrow, userId, bookId, BORROW_DURATION) == 1) {
                            return 1;
                        }
                    }
                    // 把书给用户失败，回滚 书的状态；
                    status.setRollbackOnly();
                    return 0;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    return 0;
                }
            });
            if (affectedRows == 1) {
                // 借书成功，返回借书结果
                String sqlResult = "SELECT bk.id AS bookId, bk.name AS bookName, bk.authors, bw.borrowDate, bw.dueDate FROM borrow bw JOIN book bk ON bw.book_id = bk.id WHERE bw.book_id = ? AND bw.status = 0;";
                BorrowResult borrowResultDTO = template.queryForObject(sqlResult,
                        new BeanPropertyRowMapper<>(BorrowResult.class), bookId);
                // 写入状态码
                borrowResultDTO.setCode(1);
                results.add(borrowResultDTO);
            } else {
                String sqlResult = "SELECT id AS bookId, name AS bookName, authors FROM book WHERE id = ?;";
                BorrowResult borrowResultDTO = template.queryForObject(sqlResult,
                        new BeanPropertyRowMapper<>(BorrowResult.class), bookId);
                borrowResultDTO.setCode(0);
                results.add(borrowResultDTO);
            }
        }
        return results;
    }

    @Override
    public List<BorrowInfo> getBorrowedBooksByUserId(int userId) {
        // 超期 || 已经续借过了(borrowDate和dueDate之差超过了BORROW_DURATION) 不能再续借
        String sql = "SELECT bw.id, bk.id AS bookId, bk.name AS bookName, bk.authors, bk.imageUrl, bw.borrowDate, bw.dueDate, \n" +
                "IF(dueDate < NOW() , '逾期', IF(borrowDate < DATE_SUB(dueDate,INTERVAL ? DAY), '已续借', '续借')) AS renew \n" +
                "FROM borrow bw JOIN book bk ON bw.book_id = bk.id WHERE bw.user_id = ? AND bw.status = 0;";
        return template.query(sql, new BeanPropertyRowMapper<>(BorrowInfo.class),
                BORROW_DURATION, userId);
    }

    @Override
    public List<BorrowHistoryInfo> getReturnedBooksByUserId(int userId, int offset, int count) {
        try {
            String sql = "SELECT bw.id, bw.borrowDate, bw.returnDate, bk.id AS bookId, bk.name AS bookName, bk.authors AS authors, bk.imageUrl " +
                    "FROM borrow bw JOIN book bk ON bw.book_id = bk.id WHERE bw.user_id = ? AND bw.status = 1 ORDER BY bw.returnDate DESC LIMIT ?,?;";
            return template.query(sql,
                    new BeanPropertyRowMapper<>(BorrowHistoryInfo.class),
                    userId, offset, count);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getReturnedBooksCountByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM borrow WHERE user_id = ? AND status = 1;";
        return template.queryForObject(sql, Integer.class, userId);
    }

    @Override
    public boolean renewBook(int borrowId) {
        // 在borrowDate的基础上加而不是在dueDate上，因为 就算弄错了续借次数也不会影响到最后的还书日期
        // where筛选条件加入borrowDate = DATE_SUB(dueDate,INTERVAL BORROW_DURATION DAY) 保证续借次数不会超过一次
        String sql = "UPDATE borrow SET dueDate = DATE_ADD(borrowDate,INTERVAL ?+? DAY) \n" +
                "WHERE id = ? AND status = 0 AND borrowDate = DATE_SUB(dueDate,INTERVAL ? DAY)";
        return template.update(sql, BORROW_DURATION, RENEW_DURATION, borrowId, BORROW_DURATION) == 1;
    }

    @Override
    public boolean returnBook(int borrowId) {
        // 还书完成后 有触发器会自动判读是否逾期，如果逾期会自动在fine表插入记录， 事务可以回滚触发器的操作
        return Boolean.TRUE.equals(txTemplate.execute(status -> {
            // 还书完成后 有触发器会自动修改书的状态和判断是否逾期，如果逾期会自动在fine表插入记录， 事务可以回滚触发器的操作
            try {
                // 修改借阅表的状态为已还
                String sqlBorrow = "UPDATE borrow SET status = 1, returnDate = NOW() WHERE id = ? AND status = 0;";
                if (template.update(sqlBorrow, borrowId) == 1) {
                    return true;
                } else {
                    status.setRollbackOnly();
                    return false;
                }
            } catch (Exception e) {
                status.setRollbackOnly();
                return false;
            }
        }));
    }

    @Override
    public List<NameValue> borrowingRanking() {
        try {
            String sql = "SELECT bk.name, COUNT(*) AS value FROM borrow bw JOIN book bk ON bw.book_id = bk.id GROUP BY book_id ORDER BY COUNT(*) DESC LIMIT 10;";
            return template.query(sql, new BeanPropertyRowMapper<>(NameValue.class));
        } catch (Exception e) {
            return null;
        }
    }
}
