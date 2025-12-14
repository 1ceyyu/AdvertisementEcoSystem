package com.example.videowebplatform.dao;
import com.example.videowebplatform.model.Video;
import com.example.videowebplatform.util.DBUtil;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
public class VideoDAOImpl implements VideoDAO {

    // SQL 语句常量
    private static final String SELECT_ALL = "SELECT id, title, file_name, cover_image, duration_seconds, file_length_bytes FROM video";
    private static final String SELECT_BY_ID = "SELECT id, title, file_name, cover_image, duration_seconds, file_length_bytes FROM video WHERE id = ?";

    @Override
    public List<Video> getAllVideos() {
        List<Video> videos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(SELECT_ALL);
            rs = ps.executeQuery(); // 执行查询

            while (rs.next()) {
                // 封装结果集到 Video 对象
                Video video = new Video(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("file_name"),
                        rs.getString("cover_image"),
                        rs.getLong("duration_seconds"),
                        rs.getLong("file_length_bytes")
                );
                videos.add(video);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 确保资源被关闭
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBUtil.close(conn); // 使用 DBUtil 关闭连接
        }
        return videos;
    }

    @Override
    public Video getVideoById(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Video video = null;

        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(SELECT_BY_ID);
            ps.setInt(1, id); // 设置参数
            rs = ps.executeQuery();

            if (rs.next()) {
                video = new Video(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("file_name"),
                        rs.getString("cover_image"),
                        rs.getLong("duration_seconds"),
                        rs.getLong("file_length_bytes")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBUtil.close(conn);
        }
        return video;
    }

    @Override
    public void saveVideo(Video video) {
        // 这里编写 INSERT 语句
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
