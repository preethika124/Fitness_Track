package com.example.auth.dao;

import com.example.auth.model.LogEntry;
import com.example.auth.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogDao {
    public List<LogEntry> getLogsForToday(String email) throws SQLException {

        String sql = "SELECT * FROM logs WHERE user_email = ? AND log_date = CURRENT_DATE ORDER BY id DESC";

        List<LogEntry> logs = new ArrayList<>();

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                LogEntry log = new LogEntry();
                log.setId(rs.getInt("id"));
                log.setEmail(email);
                log.setWaterIntake(rs.getDouble("water_intake_liters"));
                log.setSleepHours(rs.getDouble("sleep_hours"));
                log.setNotes(rs.getString("notes"));
                log.setLogDate(rs.getString("log_date"));
                logs.add(log);
            }
        }
        return logs;
    }

    public void addLog(LogEntry log) throws SQLException {

        String sql = "INSERT INTO logs (user_email, water_intake_liters, sleep_hours, notes, log_date) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, log.getEmail());
            ps.setObject(2, log.getWaterIntake());
            ps.setObject(3, log.getSleepHours());
            ps.setString(4, log.getNotes());
            ps.setDate(5, java.sql.Date.valueOf(log.getLogDate()));  // ✅ FIXED
          

            ps.executeUpdate();
        }
    }

    public LogEntry getTodayLog(String email) throws SQLException {

        String sql = "SELECT * FROM logs WHERE user_email = ? AND log_date = CURRENT_DATE";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                LogEntry log = new LogEntry();
                log.setId(rs.getInt("id"));
                log.setEmail(email);
                log.setWaterIntake(rs.getDouble("water_intake_liters"));
                log.setSleepHours(rs.getDouble("sleep_hours"));
                log.setNotes(rs.getString("notes"));
                log.setLogDate(rs.getString("log_date"));
                return log;
            }
        }
        return null;
    }
}
