package com.hjt.dao.impl;

import com.hjt.domain.UserLogin;
import com.hjt.utils.JDBCUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class UserLoginDaoImpl implements com.hjt.dao.UserLoginDao {
    private final JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());

    private final TransactionTemplate txTemplate = JDBCUtils.getTransactionTemplate();

    @Override
    public UserLogin getUserLoginByUsername(String username) {
        try {
            String sql = "SELECT * FROM userlogin WHERE username = ? OR phone = ? OR email = ?";
            return template.queryForObject(sql, new BeanPropertyRowMapper<UserLogin>(UserLogin.class), username, username, username);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean addUser(String username, String psd, String salt, String phone, String email) {
        try {
            // 插入用户登陆凭证信息
            String sql = "INSERT INTO userlogin(username, psd, salt, phone, email) VALUES(?, ?, ?, ?, ?);";
            return template.update(sql, username, psd, salt, phone, email) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean editPsd(int userId, String psd, String salt) {
        String sql = "UPDATE userlogin SET psd = ?, salt = ? WHERE id = ?;";
        return template.update(sql, psd, salt, userId) > 0;
    }
}
