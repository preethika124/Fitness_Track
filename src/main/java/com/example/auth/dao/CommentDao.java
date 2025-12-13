package com.example.auth.dao;

import com.example.auth.model.Comment;
import com.example.auth.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDao {

    public void createTable() {
       String sql = "CREATE TABLE IF NOT EXISTS comments (" +
            "id SERIAL PRIMARY KEY," +
            "blog_id INT NOT NULL," +
            "user_id INT NOT NULL," +
            "comment TEXT NOT NULL," +
            "commented_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        try (Connection c = DbUtil.getConnection(); Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insert(Comment comment) {
        String sql = "INSERT INTO comments (blog_id, user_id, comment) VALUES (?, ?, ?) RETURNING id";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, comment.getBlogId());
            ps.setInt(2, comment.getUserId());
            ps.setString(3, comment.getComment());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean delete(int id, int userId, String userRole) {
        String sql;
        if ("admin".equalsIgnoreCase(userRole) || "trainer".equalsIgnoreCase(userRole)) {
            sql = "DELETE FROM comments WHERE id=?";
        } else {
            sql = "DELETE FROM comments WHERE id=? AND user_id=?";
        }
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (!("admin".equalsIgnoreCase(userRole) || "trainer".equalsIgnoreCase(userRole))) {
                ps.setInt(2, userId);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Comment> getByBlogId(int blogId) {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT c.*, u.first_name, u.last_name " +
            "FROM comments c " +
            "JOIN users u ON c.user_id = u.id " +
            "WHERE c.blog_id = ? " +
            "ORDER BY c.commented_at DESC";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getCountByBlogId(int blogId) {
        String sql = "SELECT COUNT(*) FROM comments WHERE blog_id = ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Comment mapRow(ResultSet rs) throws SQLException {
        Comment cm = new Comment();
        cm.setId(rs.getInt("id"));
        cm.setBlogId(rs.getInt("blog_id"));
        cm.setUserId(rs.getInt("user_id"));
        cm.setComment(rs.getString("comment"));
        cm.setCommentedAt(rs.getTimestamp("commented_at").toLocalDateTime());
        cm.setUserName(rs.getString("first_name") + " " + rs.getString("last_name"));
        return cm;
    }
}
