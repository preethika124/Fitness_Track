package com.example.auth.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.auth.model.User;
import com.example.auth.util.DbUtil;

public class UserDao {

    public boolean createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (email, password_hash, first_name, last_name, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection c = DbUtil.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getLastName());
            ps.setString(5, user.getRole());

            return ps.executeUpdate() == 1;
        }
    }

    public void updateProfile(String email, Integer age, Double weight, String goals) throws Exception {
        String sql = "UPDATE users SET age=?, weight=?, goals=? WHERE email=?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, age);
            ps.setObject(2, weight);
            ps.setString(3, goals);
            ps.setString(4, email);
            ps.executeUpdate();
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setFirstName(rs.getString("first_name"));
                u.setLastName(rs.getString("last_name"));
                u.setRole(rs.getString("role"));

                // SAFE conversions for nullable numeric values
                u.setAge(rs.getObject("age") != null ? rs.getInt("age") : null);
                u.setWeight(rs.getObject("weight") != null ? rs.getDouble("weight") : null);

                u.setGoals(rs.getString("goals"));
                return u;
            }
        }
        return null;
    }
}
