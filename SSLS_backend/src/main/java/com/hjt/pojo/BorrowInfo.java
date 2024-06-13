package com.hjt.pojo;

public class BorrowInfo {
    private int id;
    private int bookId;
    private String bookName;
    private String authors;
    private String imageUrl;
    private String borrowDate;
    private String dueDate;
    private String renew;

    public BorrowInfo() {
    }

    public BorrowInfo(int id, int bookId, String bookName, String authors, String imageUrl, String borrowDate, String dueDate, String renew) {
        this.id = id;
        this.bookId = bookId;
        this.bookName = bookName;
        this.authors = authors;
        this.imageUrl = imageUrl;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.renew = renew;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getRenew() {
        return renew;
    }

    public void setRenew(String renew) {
        this.renew = renew;
    }
}
