package com.hjt.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    public static Properties loadProperties(String fileName) {
        Properties properties = new Properties();
        try (InputStream inputStream = PropertiesReader.class.getClassLoader().getResourceAsStream(fileName)) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("Failed to load properties file: " + e.getMessage());
        }
        return properties;
    }

    public static void main(String[] args) {
//        Properties properties = loadProperties("druid.properties");
//        // 读取属性值
//        String username = properties.getProperty("username");
//        // 打印属性值
//        System.out.println("Username: " + username);
    }
}
