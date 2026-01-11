package com.example.auth.util;

import com.example.auth.util.DbUtil;
import io.jsonwebtoken.Claims;

import java.sql.*;

public class UserUtil {

    public static int getUserIdByEmail(String email) throws SQLException {
        String sql = "SELECT id FROM users WHERE email=?";
        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) throw new SQLException("user not found");
            return rs.getInt("id");
        }
    }

    public static int getTrainerIdFromToken(String authHeader) throws Exception {
        Claims claims = JwtUtil.parseToken(authHeader.substring(7)).getBody();
        String email = claims.getSubject();

        String sql =
            "SELECT t.id FROM trainers t " +
            "JOIN users u ON u.id=t.user_id " +
            "WHERE u.email=?";

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) throw new SQLException("trainer not found");
            return rs.getInt("id");
        }
    }
}
