package com.example.auth.dao;

import com.example.auth.util.DbUtil;

import java.sql.*;
import java.util.*;

public class WorkoutAnalyticsDao {

    public List<Map<String, Object>> dailyStats(String email) {
        List<Map<String, Object>> list = new ArrayList<>();

        String sql =
                "SELECT workout_date, COUNT(*) cnt, SUM(duration_minutes) mins " +
                "FROM workouts WHERE user_email=? " +
                "GROUP BY workout_date ORDER BY workout_date";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("date", rs.getDate("workout_date").toString());
                m.put("count", rs.getInt("cnt"));
                m.put("minutes", rs.getInt("mins"));
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
