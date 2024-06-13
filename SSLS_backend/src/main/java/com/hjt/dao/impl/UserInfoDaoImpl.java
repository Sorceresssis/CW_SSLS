package com.hjt.dao.impl;

import com.hjt.dao.UserInfoDao;
import com.hjt.domain.UserInfo;
import com.hjt.utils.JDBCUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

public class UserInfoDaoImpl implements UserInfoDao {
    private final JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());


    @Override
    public UserInfo queryUserInfoById(int userId) {
        String sql = "SELECT ui.nickName, ui.gender, ui.avatar, ui.birthday, ui.balance, ul.rule " +
                "FROM userinfo ui JOIN userlogin ul ON ui.id = ul.id WHERE ui.id = ?;";
        return template.queryForObject(sql, new BeanPropertyRowMapper<>(UserInfo.class), userId);
    }

    @Override
    public boolean increaseBalance(int userId, BigDecimal amount) {
        String sql = "UPDATE userinfo SET balance = balance + ? WHERE id = ?;";
        return template.update(sql, amount, userId) > 0;
    }
}