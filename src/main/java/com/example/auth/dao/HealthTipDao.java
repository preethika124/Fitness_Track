package com.example.auth.dao;

import com.example.auth.model.HealthTip;
import com.example.auth.util.DbUtil;

import java.sql.*;

public class HealthTipDao {

    // Save today's tip
    public void saveTip(HealthTip tip) throws SQLException {

        String sql = "INSERT INTO health_tips (tip_text, category, date, source, image_url) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tip.getText());
            ps.setString(2, tip.getCategory());

            // Convert String "2025-12-08" → java.sql.Date
            ps.setDate(3, java.sql.Date.valueOf(tip.getDate()));

            ps.setString(4, tip.getSource());
            ps.setString(5, tip.getImageUrl());

            ps.executeUpdate();
        }
    }

    // Retrieve tip for a given date
    public HealthTip findTipByDate(String date) throws SQLException {

        String sql = "SELECT * FROM health_tips WHERE date = ?";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Convert string to DATE for comparison
            ps.setDate(1, java.sql.Date.valueOf(date));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                HealthTip tip = new HealthTip();
                tip.setId(rs.getInt("id"));
                tip.setText(rs.getString("tip_text"));
                tip.setCategory(rs.getString("category"));
                tip.setDate(rs.getDate("date").toString());
                tip.setSource(rs.getString("source"));
                tip.setImageUrl(rs.getString("image_url"));
                return tip;
            }
        }
        return null;
    }
}
