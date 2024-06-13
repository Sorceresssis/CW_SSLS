package com.hjt.dto;

import com.hjt.pojo.BorrowHistoryInfo;

import java.util.List;

public class BorrowHistoryPageDTO {
    private int total;
    private List<BorrowHistoryInfo> borrowHistoryInfos;

    public BorrowHistoryPageDTO() {
    }

    public BorrowHistoryPageDTO(int total, List<BorrowHistoryInfo> borrowHistoryInfos) {
        this.total = total;
        this.borrowHistoryInfos = borrowHistoryInfos;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<BorrowHistoryInfo> getBorrowHistoryInfos() {
        return borrowHistoryInfos;
    }

    public void setBorrowHistoryInfos(List<BorrowHistoryInfo> borrowHistoryInfos) {
        this.borrowHistoryInfos = borrowHistoryInfos;
    }
}
