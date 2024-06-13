package com.hjt.dao.impl;

import com.hjt.domain.Category;
import com.hjt.utils.JDBCUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class CategoryDaoImpl implements com.hjt.dao.CategoryDao {
    private JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());

    @Override
    public List<Category> queryCategorys() {
        String sql = "SELECT * FROM category;";
        return template.query(sql, new BeanPropertyRowMapper<>(Category.class));
    }

    @Override
    public boolean addCategory(String category) {
        return false;
    }

    @Override
    public boolean deleteCategory(int id) {
        return false;
    }

    @Override
    public boolean updateCategory(int id, Category category) {
        return false;
    }

}
