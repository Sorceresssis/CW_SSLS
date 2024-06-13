package com.hjt.domain;

import java.math.BigDecimal;

public class Fine {
    private int id;
    private int borrowId;
    private String date;
    private BigDecimal amount;
    private short status;

    public Fine() {
    }

    public Fine(int id, int borrowId, String date, BigDecimal amount, short status) {
        this.id = id;
        this.borrowId = borrowId;
        this.date = date;
        this.amount = amount;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(int borrowId) {
        this.borrowId = borrowId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }
}
