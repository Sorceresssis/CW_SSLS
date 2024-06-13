package com.hjt.dao.impl;

import com.hjt.dao.BookBagDao;
import com.hjt.pojo.BookProfile;
import com.hjt.utils.JDBCUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class BookBagDaoImpl implements BookBagDao {
    private final JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());

    @Override
    public List<BookProfile> getBookBagByUserId(int userId) {
        try {
            String sql = "SELECT b.id, b.name, b.authors, b.imageUrl, b.status FROM book b JOIN bookbag bb ON b.id = bb.book_id WHERE bb.user_id = ?;";
            return template.query(sql, new BeanPropertyRowMapper<>(BookProfile.class), userId);
        } catch (Exception e) {
            // 结果为空
            return null;
        }
    }

    @Override
    public int getBookBagCountByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM bookbag WHERE user_id = ?;";
        return template.queryForObject(sql, Integer.class, userId);
    }

    @Override
    public int addBookToBookBag(int userId, int bookId) {
        // 为了防止重复添加，先检查是否已经在书包里了
        if (isInBookBag(userId, bookId)) {
            return 0;
        }
        String sql = "INSERT INTO bookbag (user_id, book_id) VALUES (?, ?)";
        return template.update(sql, userId, bookId);
    }

    @Override
    public int removeBookFromBookBag(int userId, int bookId) {
        String sql = "DELETE FROM bookbag WHERE user_id = ? AND book_id = ?";
        return template.update(sql, userId, bookId);
    }

    @Override
    public boolean isInBookBag(int userId, int bookId) {
        String sql = "SELECT COUNT(*) FROM bookbag WHERE user_id = ? AND book_id = ?;";
        return template.queryForObject(sql, Integer.class, userId, bookId) != 0;
    }
}
