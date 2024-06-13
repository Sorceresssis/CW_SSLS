package com.hjt.pojo;

// 图书的简略信息
public class BookProfile {
    private int id;
    private String name;
    private String authors;
    private String imageUrl;
    private String status;

    public BookProfile() {
    }

    public BookProfile(int id, String name, String authors, String imageUrl, String status) {
        this.id = id;
        this.name = name;
        this.authors = authors;
        this.imageUrl = imageUrl;
        this.status = status;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
