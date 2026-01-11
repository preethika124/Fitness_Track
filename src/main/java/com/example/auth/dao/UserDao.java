package com.example.auth.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.auth.model.User;
import com.example.auth.util.DbUtil;

public class UserDao {

    /* ================= CREATE USER ================= */
    public boolean createUser(User user) throws SQLException {

        String sql =
            "INSERT INTO users (email, password_hash, first_name, last_name, role) " +
            "VALUES (?, ?, ?, ?, ?)";

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

    /* ================= UPDATE PROFILE ================= */
    public void updateProfile(
            String email,
            Integer age,
            Double weight,
            Integer weeklyExerciseGoal,
            Double dailyWaterGoal,
            Double dailySleepGoal
    ) throws SQLException {

        String sql =
            "UPDATE users SET " +
            "age = ?, " +
            "weight = ?, " +
            "weekly_exercise_goal = ?, " +
            "daily_water_goal = ?, " +
            "daily_sleep_goal = ? " +
            "WHERE email = ?";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, age);
            ps.setObject(2, weight);
            ps.setObject(3, weeklyExerciseGoal);
            ps.setObject(4, dailyWaterGoal);
            ps.setObject(5, dailySleepGoal);
            ps.setString(6, email);

            ps.executeUpdate();
        }
    }

    /* ================= FIND USER BY EMAIL ================= */
    public User findByEmail(String email) throws SQLException {

        

        String sql =
            "SELECT id, email, password_hash, first_name, last_name, role, " +
            "age, weight, weekly_exercise_goal, daily_water_goal, daily_sleep_goal " +
            "FROM users WHERE email = ?";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            User u = new User();
u.setId(rs.getInt("id"));
u.setEmail(rs.getString("email"));
u.setPasswordHash(rs.getString("password_hash"));
u.setFirstName(rs.getString("first_name"));
u.setLastName(rs.getString("last_name"));
u.setRole(rs.getString("role"));

// Correct numeric handling
int age = rs.getInt("age");
u.setAge(rs.wasNull() ? null : age);

double weight = rs.getDouble("weight");
u.setWeight(rs.wasNull() ? null : weight);

int weekly = rs.getInt("weekly_exercise_goal");
u.setWeeklyExerciseGoal(rs.wasNull() ? null : weekly);

double water = rs.getDouble("daily_water_goal");
u.setDailyWaterGoal(rs.wasNull() ? null : water);

double sleep = rs.getDouble("daily_sleep_goal");
u.setDailySleepGoal(rs.wasNull() ? null : sleep);


            return u;
        }
    }
}
