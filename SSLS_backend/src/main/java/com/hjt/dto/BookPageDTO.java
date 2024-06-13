package com.hjt.dto;

import com.hjt.pojo.BookProfile;

import java.util.List;

// 书籍分页信息
public class BookPageDTO {
    private int total;
    private List<BookProfile> bookProfiles;

    public BookPageDTO() {
    }

    public BookPageDTO(int total, List<BookProfile> bookProfiles) {
        this.total = total;
        this.bookProfiles = bookProfiles;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<BookProfile> getBookProfiles() {
        return bookProfiles;
    }

    public void setBookProfiles(List<BookProfile> bookProfiles) {
        this.bookProfiles = bookProfiles;
    }
}
