package com.hjt.domain;

public class Book {
    private int id;
    private String name;
    private String authors;
    private String press;
    private String imageUrl;
    private String description;
    private String publishDate;
    private String status;
    private int categoryId;
    private String categoryName;

    public Book() {
    }

    public Book(int id, String name, String authors, String press, String imageUrl, String description, String publishDate, String status, int categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.authors = authors;
        this.press = press;
        this.imageUrl = imageUrl;
        this.description = description;
        this.publishDate = publishDate;
        this.status = status;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Book(String name, String authors, String press, String imageUrl, String description, String publishDate, String status, int categoryId) {
        this.name = name;
        this.authors = authors;
        this.press = press;
        this.imageUrl = imageUrl;
        this.description = description;
        this.publishDate = publishDate;
        this.status = status;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}

