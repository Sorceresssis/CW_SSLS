package com.hjt.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态sql构造工具类
 */
public class DynamicSqlBuilderUtils {
    private StringBuilder sqlBuilder;
     
    private List<Object> params;

    public DynamicSqlBuilderUtils() {
        this.sqlBuilder = new StringBuilder();
        this.params = new ArrayList<>();

    }

    public DynamicSqlBuilderUtils append(String sql) {
        sqlBuilder.append(sql);
        return this;
    }

    public DynamicSqlBuilderUtils appendIf(boolean condition, String sql) {
        if (condition) {
            sqlBuilder.append(sql);
        }
        return this;
    }

    public DynamicSqlBuilderUtils appendIfNotNull(Object value, String sql) {
        if (value != null) {
            sqlBuilder.append(sql);
            params.add(value);
        }
        return this;
    }

    public DynamicSqlBuilderUtils appendIfNotEmpty(String value, String sql) {
        if (value != null && !value.isEmpty()) {
            sqlBuilder.append(sql);
            params.add(value);
        }
        return this;
    }

    public DynamicSqlBuilderUtils appendIfNotEmpty(List<?> list, String sql) {
        if (list != null && !list.isEmpty()) {
            sqlBuilder.append(sql);
            params.addAll(list);
        }
        return this;
    }

    public String getSql() {
        return sqlBuilder.toString();
    }

    public Object[] getParameters() {
        return params.toArray();
    }
}
