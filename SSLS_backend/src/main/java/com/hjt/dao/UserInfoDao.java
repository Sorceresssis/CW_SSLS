package com.hjt.dao;

import com.hjt.domain.UserInfo;

import java.math.BigDecimal;

public interface UserInfoDao {
    UserInfo queryUserInfoById(int userId);

    boolean increaseBalance(int userId, BigDecimal amount);
}