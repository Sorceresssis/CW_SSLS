package com.hjt.dao;

import com.hjt.pojo.BorrowHistoryInfo;
import com.hjt.pojo.BorrowInfo;
import com.hjt.pojo.BorrowResult;
import com.hjt.pojo.NameValue;

import java.util.List;

public interface BorrowDao {
    /**
     * 借书
     *
     * @param userId  用户id
     * @param bookIds 书籍id数组
     * @return 每本书的借书结果
     */
    List<BorrowResult> toBorrowBooks(int userId, int[] bookIds);

    List<BorrowInfo> getBorrowedBooksByUserId(int userId);

    List<BorrowHistoryInfo> getReturnedBooksByUserId(int userId, int offset, int count);

    int getReturnedBooksCountByUserId(int userId);

    // 续借图书
    boolean renewBook(int borrowId);

    // 归还图书
    boolean returnBook(int borrowId);

    // 借阅排名
    List<NameValue> borrowingRanking();
}
