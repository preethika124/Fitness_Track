package com.example.auth.dao;

import com.example.auth.model.Trainer;
import com.example.auth.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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

    public Trainer createOrUpdateTrainer(Trainer t) throws SQLException {

    String findUser =
        "SELECT id, role, first_name FROM users WHERE email = ?";

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

            if (!"trainer".equalsIgnoreCase(role)) {
                throw new IllegalStateException("user-not-trainer");
            }

          
            String findTrainer =
                "SELECT id, specialization, experience_years, availability, bio " +
                "FROM trainers WHERE user_id = ?";

            try (PreparedStatement fps = c.prepareStatement(findTrainer)) {
                fps.setInt(1, userId);

                try (ResultSet trs = fps.executeQuery()) {

             
                    if (trs.next()) {

                        int trainerId = trs.getInt("id");
                        String spec = trs.getString("specialization");
                        Integer exp = trs.getInt("experience_years");
                        String avail = trs.getString("availability");
                        String bio = trs.getString("bio");

                        boolean same =
                            Objects.equals(spec, t.getSpecialization()) &&
                            Objects.equals(exp, t.getExperienceYears()) &&
                            Objects.equals(avail, t.getAvailability()) &&
                            Objects.equals(bio, t.getBio());

                        if (same) {
                            throw new IllegalStateException(
                                "trainer-already-enrolled"
                            );
                        }

                   
                        String update =
                            "UPDATE trainers SET " +
                            "specialization=?, experience_years=?, availability=?, bio=? " +
                            "WHERE id=?";

                        try (PreparedStatement ups =
                                 c.prepareStatement(update)) {

                            ups.setString(1, t.getSpecialization());
                            ups.setObject(2, t.getExperienceYears());
                            ups.setString(3, t.getAvailability());
                            ups.setString(4, t.getBio());
                            ups.setInt(5, trainerId);
                            ups.executeUpdate();
                        }

                        t.setId(trainerId);
                        t.setUserId(userId);
                        t.setName(name);
                        return t;
                    }
                }
            }

            // =================================================
            // CASE 2: Trainer does NOT exist → INSERT
            // =================================================
            String insert =
                "INSERT INTO trainers(user_id, specialization, experience_years, availability, bio) " +
                "VALUES (?,?,?,?,?)";

            try (PreparedStatement ips =
                     c.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {

                ips.setInt(1, userId);
                ips.setString(2, t.getSpecialization());
                ips.setObject(3, t.getExperienceYears());
                ips.setString(4, t.getAvailability());
                ips.setString(5, t.getBio());

                ips.executeUpdate();

                try (ResultSet gk = ips.getGeneratedKeys()) {
                    if (gk.next()) {
                        t.setId(gk.getInt(1));
                        t.setUserId(userId);
                        t.setName(name);
                        return t;
                    }
                }
            }
        }
    }

    throw new SQLException("trainer save failed");
}



}
