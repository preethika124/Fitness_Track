package com.example.auth.dao;

import com.example.auth.util.DbUtil;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DashboardDao {

    public Map<String, Object> getSummary(String email) {
        Map<String, Object> map = new HashMap<>();

        String todayCalories =
                "SELECT COALESCE(SUM(calorie_count),0) FROM meals " +
                "WHERE user_email=? AND meal_date=CURRENT_DATE";

        String weeklyBurned =
                "SELECT COALESCE(SUM(calories_burned),0) FROM workouts " +
                "WHERE user_email=? AND workout_date >= CURRENT_DATE - INTERVAL '7 days'";

        try (Connection con = DbUtil.getConnection()) {

            PreparedStatement ps1 = con.prepareStatement(todayCalories);
            ps1.setString(1, email);
            ResultSet rs1 = ps1.executeQuery();
            rs1.next();
            map.put("todayCalories", rs1.getDouble(1));

            PreparedStatement ps2 = con.prepareStatement(weeklyBurned);
            ps2.setString(1, email);
            ResultSet rs2 = ps2.executeQuery();
            rs2.next();
            map.put("weeklyBurned", rs2.getDouble(1));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}

