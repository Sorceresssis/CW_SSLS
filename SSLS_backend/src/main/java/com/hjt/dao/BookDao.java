package com.hjt.dao;

import com.hjt.domain.Book;
import com.hjt.dto.BookNameDTO;
import com.hjt.pojo.BookProfile;
import com.hjt.pojo.NameValue;

import java.util.List;

public interface BookDao {

    int queryBookCount();

    int queryBookCountByCategoryId(int categoryId);

    int queryBookCountByKeyWord(String keyWord);

    /**
     * 根据关键字查询书籍的名称并去根据匹配度排序
     *
     * @param keyWord 用户的搜索词
     * @return 书籍名称列表
     */
    List<BookNameDTO> queryBookNamesByKeyWord(String keyWord, int offset, int count);

    List<BookProfile> queryBookProfiles(int offset, int count);

    List<BookProfile> queryBookProfilesByCategoryId(int categoryId, int offset, int count);

    List<BookProfile> queryBookProfilesByKeyWord(String keyWord, int offset, int count);

    Book queryBookByBookId(int bookId);

    boolean addBook(Book book);

    boolean editBook(int bookId, Book book);

    List<NameValue> queryBookStatus();

    // 查询各个分类下的书籍数量
    List<NameValue> queryBookCountOfCategorys();
}
