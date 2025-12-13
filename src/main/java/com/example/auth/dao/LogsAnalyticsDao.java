
package com.example.auth.dao;

import com.example.auth.util.DbUtil;

import java.sql.*;
import java.util.*;

public class LogsAnalyticsDao {

    public List<Map<String, Object>> daily(String email) {
        List<Map<String, Object>> list = new ArrayList<>();

        String sql =
                "SELECT log_date, water_intake_liters, sleep_hours " +
                "FROM logs WHERE user_email=? ORDER BY log_date";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("date", rs.getDate("log_date").toString());
                m.put("water", rs.getDouble("water_intake_liters"));
                m.put("sleep", rs.getDouble("sleep_hours"));
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
