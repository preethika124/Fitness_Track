package com.example.auth.dao;

import com.example.auth.util.DbUtil;

import java.sql.*;
import java.util.*;

public class ChatDao {

    /* ================= SAVE MESSAGE ================= */
    public void saveMessage(
            int userId,
            int trainerId,
            String senderRole,
            String message
    ) throws SQLException {

        String sql =
            "INSERT INTO chat_messages(user_id, trainer_id, sender_role, message) " +
            "VALUES (?,?,?,?)";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, trainerId);
            ps.setString(3, senderRole);
            ps.setString(4, message);
            ps.executeUpdate();
        }
    }

    /* ================= GET CHAT ================= */
    public List<Map<String, Object>> getMessages(
            int userId,
            int trainerId
    ) throws SQLException {

        String sql =
            "SELECT sender_role, message, created_at " +
            "FROM chat_messages " +
            "WHERE user_id=? AND trainer_id=? " +
            "ORDER BY created_at";

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, trainerId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("senderRole", rs.getString("sender_role"));
                m.put("message", rs.getString("message"));
                m.put("time", rs.getTimestamp("created_at").toString());
                list.add(m);
            }
        }
        return list;
    }

    /* ================= TRAINER'S USER LIST ================= */
    public List<Map<String, Object>> getTrainerUsers(int trainerId)
            throws SQLException {

        String sql =
            "SELECT DISTINCT u.id, u.first_name " +
            "FROM chat_messages c " +
            "JOIN users u ON u.id = c.user_id " +
               "WHERE c.trainer_id = ? AND u.role = 'USER'"
           ;

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, trainerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("userId", rs.getInt("id"));
                m.put("userName", rs.getString("first_name"));
                list.add(m);
            }
        }
        return list;
    }

    /* ================= USER'S TRAINER LIST ================= */
    public List<Map<String, Object>> getUserTrainers(int userId)
            throws SQLException {

        String sql =
            "SELECT DISTINCT t.id, u.first_name as name " +
            "FROM chat_messages c " +
            "JOIN trainers t ON t.id = c.trainer_id " +
            "JOIN users u ON u.id = t.user_id " +
            "WHERE c.user_id = ?";

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", rs.getInt("id"));
                m.put("name", rs.getString("name"));
                list.add(m);
            }
        }
        return list;
    }
}