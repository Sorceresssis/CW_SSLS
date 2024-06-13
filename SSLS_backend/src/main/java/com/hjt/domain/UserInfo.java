package com.hjt.domain;

import java.math.BigDecimal;

public class UserInfo {
    private String nickName;
    private String gender;
    private String avatar;
    private String birthday;
    private BigDecimal balance;
    private String rule;

    public UserInfo() {
    }

    public UserInfo(String nickName, String gender, String avatar, String birthday, BigDecimal balance, String rule) {
        this.nickName = nickName;
        this.gender = gender;
        this.avatar = avatar;
        this.birthday = birthday;
        this.balance = balance;
        this.rule = rule;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}