package com.example.auth.dao;

import com.example.auth.model.BMI;
import com.example.auth.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BmiDao {

    public void saveBMI(BMI bmi) throws SQLException {

        String sql = "INSERT INTO bmi (user_email, bmi_value, status) VALUES (?, ?, ?)";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, bmi.getEmail());
            ps.setDouble(2, bmi.getBmiValue());
            ps.setString(3, bmi.getStatus());

            ps.executeUpdate();
        }
    }
}
