package com.hjt.dao;

import com.hjt.pojo.BookProfile;

import java.util.List;

public interface BookBagDao {
    List<BookProfile> getBookBagByUserId(int userId);

    int getBookBagCountByUserId(int userId);

    int addBookToBookBag(int userId, int bookId);

    int removeBookFromBookBag(int userId, int bookId);

    boolean isInBookBag(int userId, int bookId);
}