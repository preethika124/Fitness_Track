
package com.example.auth.dao;

import com.example.auth.util.DbUtil;

import java.sql.*;
import java.util.*;

public class NutritionAnalyticsDao {

    public List<Map<String, Object>> calories(String email) {
        List<Map<String, Object>> list = new ArrayList<>();

        String sql =
            "SELECT d.date, " +
            "COALESCE(m.cal,0) consumed, COALESCE(w.burn,0) burned " +
            "FROM ( " +
            "  SELECT meal_date date FROM meals WHERE user_email=? " +
            "  UNION SELECT workout_date FROM workouts WHERE user_email=? " +
            ") d " +
            "LEFT JOIN (SELECT meal_date, SUM(calorie_count) cal FROM meals GROUP BY meal_date) m " +
            "ON d.date=m.meal_date " +
            "LEFT JOIN (SELECT workout_date, SUM(calories_burned) burn FROM workouts GROUP BY workout_date) w " +
            "ON d.date=w.workout_date ORDER BY d.date";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("date", rs.getDate("date").toString());
                m.put("consumed", rs.getDouble("consumed"));
                m.put("burned", rs.getDouble("burned"));
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
