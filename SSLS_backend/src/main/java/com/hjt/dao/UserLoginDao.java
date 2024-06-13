package com.hjt.dao;

import com.hjt.domain.UserLogin;

public interface UserLoginDao {

    UserLogin getUserLoginByUsername(String username);

    boolean addUser(String username, String psd, String salt, String phone, String email);

    boolean editPsd(int userId, String psd, String salt);
}
