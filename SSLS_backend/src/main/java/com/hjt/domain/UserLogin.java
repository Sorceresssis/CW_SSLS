package com.hjt.domain;

import java.time.LocalDateTime;

public class UserLogin {
    private int id;
    private String rule;
    private String username;
    private String phone;
    private String email;
    private String psd;
    private String salt;
    private LocalDateTime regDate;

    public UserLogin() {
    }

    public UserLogin(int id, String rule, String username, String phone, String email, String psd, String salt, LocalDateTime regDate) {
        this.id = id;
        this.rule = rule;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.psd = psd;
        this.salt = salt;
        this.regDate = regDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPsd() {
        return psd;
    }

    public void setPsd(String psd) {
        this.psd = psd;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public LocalDateTime getRegDate() {
        return regDate;
    }

    public void setRegDate(LocalDateTime regDate) {
        this.regDate = regDate;
    }
}
