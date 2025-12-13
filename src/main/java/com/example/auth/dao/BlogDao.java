package com.example.auth.dao;

import com.example.auth.model.Blog;
import com.example.auth.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlogDao {

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS blogs (" +
            "id SERIAL PRIMARY KEY," +
            "author_id INT NOT NULL," +
            "title VARCHAR(255) NOT NULL," +
            "content TEXT NOT NULL," +
            "category VARCHAR(100)," +
            "image_url TEXT," +
            "likes INT DEFAULT 0," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        try (Connection c = DbUtil.getConnection(); Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insert(Blog blog) {
        String sql = "INSERT INTO blogs (author_id, title, content, category, image_url) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, blog.getAuthorId());
            ps.setString(2, blog.getTitle());
            ps.setString(3, blog.getContent());
            ps.setString(4, blog.getCategory());
            ps.setString(5, blog.getImageUrl());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean update(Blog blog) {
        String sql = "UPDATE blogs SET title=?, content=?, category=?, image_url=?, updated_at=CURRENT_TIMESTAMP WHERE id=? AND author_id=?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, blog.getTitle());
            ps.setString(2, blog.getContent());
            ps.setString(3, blog.getCategory());
            ps.setString(4, blog.getImageUrl());
            ps.setInt(5, blog.getId());
            ps.setInt(6, blog.getAuthorId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id, int authorId, String userRole) {
        String sql;
        if ("admin".equalsIgnoreCase(userRole) || "trainer".equalsIgnoreCase(userRole)) {
            sql = "DELETE FROM blogs WHERE id=?";
        } else {
            sql = "DELETE FROM blogs WHERE id=? AND author_id=?";
        }
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (!("admin".equalsIgnoreCase(userRole) || "trainer".equalsIgnoreCase(userRole))) {
                ps.setInt(2, authorId);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Blog getById(int id) {
        String sql = "SELECT b.*, u.first_name, u.last_name, u.role " +
            "FROM blogs b " +
            "JOIN users u ON b.author_id = u.id " +
            "WHERE b.id = ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Blog> getAll() {
        List<Blog> list = new ArrayList<>();
        String sql = "SELECT b.*, u.first_name, u.last_name, u.role " +
            "FROM blogs b " +
            "JOIN users u ON b.author_id = u.id " +
            "ORDER BY b.created_at DESC";
        try (Connection c = DbUtil.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Blog> getByCategory(String category) {
        List<Blog> list = new ArrayList<>();
        String sql = "SELECT b.*, u.first_name, u.last_name, u.role " +
            "FROM blogs b " +
            "JOIN users u ON b.author_id = u.id " +
            "WHERE b.category = ? " +
            "ORDER BY b.created_at DESC";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Blog> getFeatured() {
        List<Blog> list = new ArrayList<>();
        String sql = "SELECT b.*, u.first_name, u.last_name, u.role " +
            "FROM blogs b " +
            "JOIN users u ON b.author_id = u.id " +
            "WHERE u.role IN ('admin', 'trainer') " +
            "ORDER BY b.likes DESC, b.created_at DESC " +
            "LIMIT 6";
        try (Connection c = DbUtil.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void incrementLikes(int blogId) {
        String sql = "UPDATE blogs SET likes = likes + 1 WHERE id = ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void decrementLikes(int blogId) {
        String sql = "UPDATE blogs SET likes = GREATEST(likes - 1, 0) WHERE id = ?";
        try (Connection c = DbUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, blogId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Blog mapRow(ResultSet rs) throws SQLException {
        Blog b = new Blog();
        b.setId(rs.getInt("id"));
        b.setAuthorId(rs.getInt("author_id"));
        b.setTitle(rs.getString("title"));
        b.setContent(rs.getString("content"));
        b.setCategory(rs.getString("category"));
        b.setImageUrl(rs.getString("image_url"));
        b.setLikes(rs.getInt("likes"));
        b.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        b.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        b.setAuthorName(rs.getString("first_name") + " " + rs.getString("last_name"));
        b.setAuthorRole(rs.getString("role"));
        return b;
    }
}