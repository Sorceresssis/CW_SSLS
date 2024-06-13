package com.hjt.dao.impl;

import com.hjt.dao.BookDao;
import com.hjt.domain.Book;
import com.hjt.dto.BookNameDTO;
import com.hjt.pojo.BookProfile;
import com.hjt.pojo.NameValue;
import com.hjt.utils.JDBCUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class BookDaoImpl implements BookDao {
    private final JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());

    @Override
    public int queryBookCount() {
        String sql = "SELECT COUNT(*) FROM book;";
        return template.queryForObject(sql, Integer.class);
    }

    @Override
    public int queryBookCountByCategoryId(int categoryId) {
        String sql = "SELECT COUNT(*) FROM book WHERE category_id = ?";
        return template.queryForObject(sql, Integer.class, categoryId);
    }

    @Override
    public int queryBookCountByKeyWord(String keyWord) {
        String sql = "SELECT COUNT(*) FROM book WHERE MATCH(name) AGAINST(?);";
        return template.queryForObject(sql, Integer.class, keyWord);
    }

    @Override
    public List<BookNameDTO> queryBookNamesByKeyWord(String keyWord, int offset, int count) {
        String sql = "SELECT id, name FROM book WHERE MATCH(name) AGAINST(?) ORDER BY MATCH(name) AGAINST(?) DESC LIMIT ?,?;";
        return template.query(sql,
                new BeanPropertyRowMapper<>(BookNameDTO.class),
                keyWord, keyWord, offset, count);
    }

    @Override
    public List<BookProfile> queryBookProfiles(int offset, int count) {
        String sql = "SELECT id, name, authors, imageUrl, status FROM book LIMIT ?,?;";
        return template.query(sql,
                new BeanPropertyRowMapper<>(BookProfile.class),
                offset, count);
    }

    @Override
    public List<BookProfile> queryBookProfilesByCategoryId(int categoryId, int offset, int count) {
        String sql = "SELECT id, name, authors, imageUrl, status FROM book WHERE category_id = ? LIMIT ?,?;";
        return template.query(sql,
                new BeanPropertyRowMapper<>(BookProfile.class),
                categoryId, offset, count);
    }

    @Override
    public List<BookProfile> queryBookProfilesByKeyWord(String keyWord, int offset, int count) {
        // 展示时，mysql的ngram分词器的最小分词树配置为了1, 只要有一个字匹配就会被查到。ngram_token_size = 1
        // 根据匹配度排序，匹配度越高，越靠前
        String sql = "SELECT id, name, authors, imageUrl, status FROM book WHERE MATCH(name) AGAINST(?) ORDER BY MATCH(name) AGAINST(?) DESC LIMIT ?,?;";
        return template.query(sql, new BeanPropertyRowMapper<>(BookProfile.class),
                keyWord, keyWord, offset, count);
    }

    @Override
    public Book queryBookByBookId(int bookId) {
        String sql = "SELECT b.id, b.name, b.authors, b.press, b.imageUrl, b.description, b.publishDate, b.status, c.id AS categoryId , c.name AS categoryName FROM book b JOIN category c ON b.category_id = c.id WHERE b.id = ?;";
        return template.queryForObject(sql, new BeanPropertyRowMapper<>(Book.class), bookId);
    }

    @Override
    public boolean addBook(Book book) {
        String sql = "INSERT INTO book(name, authors, press, imageUrl, description, publishDate, status, category_id) VALUES(?,?,?,?,?,?,?,?);";
        return template.update(sql,
                book.getName(),
                book.getAuthors(),
                emptyStringToNull(book.getPress()),
                emptyStringToNull(book.getImageUrl()),
                emptyStringToNull(book.getDescription()),
                emptyStringToNull(book.getPublishDate()),
                book.getStatus(),
                book.getCategoryId()
        ) == 1;
    }

    @Override
    public boolean editBook(int bookId, Book book) {
        String sql = "UPDATE book SET name = ?, authors = ?, press = ?, imageUrl = ?, description = ?, publishDate = ?, status = ?, category_id = ? WHERE id = ?;";
        return template.update(sql,
                book.getName(),
                book.getAuthors(),
                emptyStringToNull(book.getPress()),
                emptyStringToNull(book.getImageUrl()),
                emptyStringToNull(book.getDescription()),
                emptyStringToNull(book.getPublishDate()),
                book.getStatus(),
                book.getCategoryId(),
                bookId
        ) == 1;
    }

    // 把空字符串转换为null
    private String emptyStringToNull(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        return str;
    }

    @Override
    public List<NameValue> queryBookStatus() {
        String sql = "SELECT STATUS AS name, COUNT(*) AS value FROM book GROUP BY STATUS;";
        return template.query(sql, new BeanPropertyRowMapper<>(NameValue.class));
    }

    @Override
    public List<NameValue> queryBookCountOfCategorys() {
        String sql = "SELECT c.name, COUNT(*) AS value FROM book bk JOIN category c ON bk.category_id = c.id GROUP BY c.id ORDER BY value DESC;";
        return template.query(sql, new BeanPropertyRowMapper<>(NameValue.class));
    }
}
