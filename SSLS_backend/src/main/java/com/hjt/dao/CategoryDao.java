package com.hjt.dao;

import com.hjt.domain.Category;

import java.util.List;

public interface CategoryDao {
    List<Category> queryCategorys();

    boolean addCategory(String category);

    boolean deleteCategory(int id);

    boolean updateCategory(int id, Category category);
}
