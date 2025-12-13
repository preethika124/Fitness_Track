package com.example.auth.dao;

import com.example.auth.util.DbUtil;

import java.sql.*;

public class BlogLikeDao {

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS blog_likes (" +
            "id SERIAL PRIMARY KEY," +
            "blog_id INT NOT NULL," +
            "user_id INT NOT NULL," +
            "liked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "UNIQUE(blog_id, user_id)" +
            ")";
        try (Connection c = DbUtil.getConnection(); Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasLiked(int blogId, int userId) {
        String sql = "SELECT 1 FROM blog_likes WHERE blog_id = ? AND user_id = ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addLike(int blogId, int userId) {
        String sql = "INSERT INTO blog_likes (blog_id, user_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeLike(int blogId, int userId) {
        String sql = "DELETE FROM blog_likes WHERE blog_id = ? AND user_id = ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
