package com.example.auth.dao;

import com.example.auth.model.Trainer;
import com.example.auth.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainerDao {

    /**
     * Return all trainers. Keep it simple for now.
     */
    public List<Trainer> getAllTrainers() throws SQLException {
        String sql = "SELECT t.id, t.user_id, u.first_name as user_name,u.email as user_email, t.specialization, t.experience_years, t.availability, t.bio " +
                     "FROM trainers t LEFT JOIN users u ON u.id = t.user_id ORDER BY t.id";
        List<Trainer> out = new ArrayList<>();
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Trainer t = new Trainer();
                t.setId(rs.getInt("id"));
                t.setUserId(rs.getInt("user_id"));
                t.setName(rs.getString("user_name"));
                t.setSpecialization(rs.getString("specialization") != null ? rs.getString("specialization") : "");
                t.setExperienceYears(rs.getInt("experience_years"));
                t.setAvailability(rs.getString("availability"));
                t.setUserEmail(rs.getString("user_email"));
                t.setBio(rs.getString("bio"));
                out.add(t);
            }
        }
        return out;
    }

    public Trainer createTrainer(Trainer t) throws SQLException {

    String findUser = "SELECT id, role, first_name FROM users WHERE email = ?";
    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(findUser)) {

        ps.setString(1, t.getUserEmail());
        try (ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) {
                throw new SQLException("user-not-found");
            }

            int userId = rs.getInt("id");
            String role = rs.getString("role");
            String name = rs.getString("first_name");

            if (role == null || !"trainer".equalsIgnoreCase(role)) {
                throw new IllegalStateException("user-not-trainer");
            }

            // ============================================
            // 1. CHECK IF USER ALREADY ENROLLED AS TRAINER
            // ============================================
            String checkExisting =
    "SELECT id, specialization, experience_years, availability, bio " +
    "FROM trainers " +
    "WHERE user_id = ? AND specialization = ? " +
    "AND experience_years = ? AND availability = ?";

            try (PreparedStatement checkPs = c.prepareStatement(checkExisting)) {
                checkPs.setInt(1, userId);
                checkPs.setString(2, t.getSpecialization());
                checkPs.setInt(3, t.getExperienceYears());
                checkPs.setString(4, t.getAvailability());

                try (ResultSet existRs = checkPs.executeQuery()) {
                    if (existRs.next()) {
                        // RETURN EXISTING TRAINER – DO NOT INSERT AGAIN
                        Trainer existing = new Trainer();
                        existing.setId(existRs.getInt("id"));
                        existing.setUserId(userId);
                        existing.setName(name);
                        existing.setSpecialization(existRs.getString("specialization"));
                        existing.setExperienceYears(existRs.getInt("experience_years"));
                        existing.setAvailability(existRs.getString("availability"));
                        existing.setBio(existRs.getString("bio"));
                        existing.setUserEmail(t.getUserEmail());

                        return existing;
                    }
                }
            }

            // ============================================
            // 2. IF NOT EXISTS → CREATE NEW TRAINER
            // ============================================
            String insert = "INSERT INTO trainers(user_id, specialization, experience_years, availability, bio) VALUES(?,?,?,?,?)";
            try (PreparedStatement ips = c.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {

                ips.setInt(1, userId);
                ips.setString(2, t.getSpecialization());
                ips.setInt(3, t.getExperienceYears());
                ips.setString(4, t.getAvailability());
                ips.setString(5, t.getBio());

                int affected = ips.executeUpdate();
                if (affected == 0) {
                    throw new SQLException("creating trainer failed");
                }

                try (ResultSet gk = ips.getGeneratedKeys()) {
                    if (gk.next()) {
                        t.setId(gk.getInt(1));
                        t.setUserId(userId);
                        if (t.getName() == null) t.setName(name);
                        return t;
                    } else {
                        throw new SQLException("creating trainer failed, no id obtained");
                    }
                }
            }
        }
    }
}

}
