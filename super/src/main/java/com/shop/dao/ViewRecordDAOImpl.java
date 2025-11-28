package com.shop.dao;

import com.shop.entity.ViewRecord;
import com.shop.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ViewRecordDAOImpl implements ViewRecordDAO {
    @Override
    public void addOrUpdateRecord(int userId, int categoryId) {
        String sql = "INSERT INTO view_records (user_id, category_id, view_count) VALUES (?, ?, 1) ON DUPLICATE KEY UPDATE view_count = view_count + 1, last_viewed_at = CURRENT_TIMESTAMP";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public List<ViewRecord> findByUserId(int userId) {
        String sql = "SELECT vr.*, u.username, c.name as category_name FROM view_records vr JOIN users u ON vr.user_id = u.id JOIN categories c ON vr.category_id = c.id WHERE vr.user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<ViewRecord> records = new ArrayList<>();
        
        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                ViewRecord record = new ViewRecord();
                record.setId(rs.getInt("id"));
                record.setUserId(rs.getInt("user_id"));
                record.setCategoryId(rs.getInt("category_id"));
                record.setViewCount(rs.getInt("view_count"));
                record.setLastViewedAt(rs.getTimestamp("last_viewed_at"));
                
                // 设置用户信息
                com.shop.entity.User user = new com.shop.entity.User();
                user.setId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                record.setUser(user);
                
                // 设置分类信息
                com.shop.entity.Category category = new com.shop.entity.Category();
                category.setId(rs.getInt("category_id"));
                category.setName(rs.getString("category_name"));
                record.setCategory(category);
                
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return records;
    }
    
    @Override
    public List<ViewRecord> findAll() {
        String sql = "SELECT vr.*, u.username, c.name as category_name FROM view_records vr JOIN users u ON vr.user_id = u.id JOIN categories c ON vr.category_id = c.id";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<ViewRecord> records = new ArrayList<>();
        
        try {
            conn = DBUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                ViewRecord record = new ViewRecord();
                record.setId(rs.getInt("id"));
                record.setUserId(rs.getInt("user_id"));
                record.setCategoryId(rs.getInt("category_id"));
                record.setViewCount(rs.getInt("view_count"));
                record.setLastViewedAt(rs.getTimestamp("last_viewed_at"));
                
                // 设置用户信息
                com.shop.entity.User user = new com.shop.entity.User();
                user.setId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                record.setUser(user);
                
                // 设置分类信息
                com.shop.entity.Category category = new com.shop.entity.Category();
                category.setId(rs.getInt("category_id"));
                category.setName(rs.getString("category_name"));
                record.setCategory(category);
                
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return records;
    }
}