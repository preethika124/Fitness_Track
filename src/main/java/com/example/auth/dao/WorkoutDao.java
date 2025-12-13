package com.example.auth.dao;

import com.example.auth.model.Workout;
import com.example.auth.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkoutDao {

    public void addWorkout(Workout w) throws SQLException {

        String sql = "INSERT INTO workouts (exercise_type, duration_minutes, calories_burned, workout_date, user_email) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, w.getWorkoutType());
            ps.setObject(2, w.getDurationMinutes());
            ps.setObject(3, w.getCaloriesBurned());

            // Convert String -> java.sql.Date
            ps.setDate(4, Date.valueOf(w.getWorkoutDate()));

            ps.setString(5, w.getEmail());

            ps.executeUpdate();
        }
    }

    public List<Workout> getWorkoutsForToday(String email) throws SQLException {

        String sql = "SELECT * FROM workouts WHERE user_email = ? AND workout_date = CURRENT_DATE";
        List<Workout> list = new ArrayList<>();

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Workout w = new Workout();
                w.setId(rs.getInt("id"));
                w.setEmail(email);
                w.setWorkoutType(rs.getString("exercise_type"));
                w.setDurationMinutes(rs.getInt("duration_minutes"));
                w.setCaloriesBurned(rs.getDouble("calories_burned"));

                // Convert java.sql.Date -> String
                w.setWorkoutDate(rs.getDate("workout_date").toString());

                list.add(w);
            }
        }

        return list;
    }
}
