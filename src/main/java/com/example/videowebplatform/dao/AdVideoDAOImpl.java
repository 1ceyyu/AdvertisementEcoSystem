// File: com/example/videowebplatform/dao/AdVideoDAOImpl.java
package com.example.videowebplatform.dao;

import com.example.videowebplatform.model.AdVideo;
import com.example.videowebplatform.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdVideoDAOImpl implements AdVideoDAO {

    // 假设广告表名为 ad_video
    private static final String SELECT_ALL_ADS =
            "SELECT id, title, file_name, duration_seconds, file_length_bytes FROM ad_video";

    @Override
    public List<AdVideo> getAllAds() {
        List<AdVideo> ads = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();// 获取数据库连接
            ps = conn.prepareStatement(SELECT_ALL_ADS);
            rs = ps.executeQuery();

            while (rs.next()) {
                AdVideo ad = new AdVideo(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("file_name"),
                        rs.getLong("duration_seconds"),
                        rs.getLong("file_length_bytes")
                );
                ads.add(ad);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 打印错误
        } finally {
            // 确保资源被关闭
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBUtil.close(conn);
        }
        return ads;
    }
}