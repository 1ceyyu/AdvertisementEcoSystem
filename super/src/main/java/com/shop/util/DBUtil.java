package com.shop.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

public class DBUtil {
    private static DataSource dataSource;
    
    static {
        try {
            // 加载配置文件
            InputStream inputStream = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            
            // 创建数据源
            dataSource = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("数据库连接池初始化失败");
        }
    }
    
    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    // 关闭数据库连接
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}