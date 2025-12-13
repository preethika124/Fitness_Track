package com.example.auth.util;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET =
            "pA8NkiJeLqz9vEKeD8zHgxb1w9gNh71pA8NkiJeLqz9vEKeD8zHg==";

    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final long EXP_MS = 1000L * 60 * 60 * 24; // 24 hours

    public static String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP_MS))
                .signWith(KEY)
                .compact();
    }

    public static String generateToken(int userId, String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP_MS))
                .signWith(KEY)
                .compact();
    }

    public static Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
    }

    public static int getUserId(String token) {
        Claims claims = parseToken(token).getBody();
        Object userId = claims.get("userId");
        if (userId instanceof Integer) {
            return (Integer) userId;
        } else if (userId instanceof Number) {
            return ((Number) userId).intValue();
        }
        return -1;
    }

    public static String getRole(String token) {
        Claims claims = parseToken(token).getBody();
        return (String) claims.get("role");
    }

    public static String getEmail(String token) {
        Claims claims = parseToken(token).getBody();
        return claims.getSubject();
    }
    public static String email(String authHeader) {
    try {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        // Remove "Bearer " and trim spaces
        String token = authHeader.substring(7).trim();

        Claims claims = parseToken(token).getBody();
        return claims.getSubject();
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
}