package com.hjt.pojo;

public class BorrowResult {
    // 是否成功
    private int code;
    private int bookId;
    private String bookName;
    private String authors;
    private String borrowDate;
    private String dueDate;

    public BorrowResult() {
    }

    public BorrowResult(int code, int bookId, String bookName, String authors, String borrowDate, String dueDate) {
        this.code = code;
        this.bookId = bookId;
        this.bookName = bookName;
        this.authors = authors;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
