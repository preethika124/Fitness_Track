package com.example.auth.servlet;

import com.example.auth.dao.UserDao;
import com.example.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/update-profile")
public class UpdateProfileServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private final ObjectMapper mapper = new ObjectMapper();

    // DTO matching profile.js payload
    static class UpdateReq {
        public Integer age;
        public Double weight;

        // 🎯 GOALS
        public Integer weeklyExerciseGoal;
        public Double dailyWaterGoal;
        public Double dailySleepGoal;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        /* ================= AUTH ================= */
        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"Missing token\"}");
            return;
        }

        String token = authHeader.substring(7);
        String email;

        try {
            email = JwtUtil.parseToken(token).getBody().getSubject();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"Invalid token\"}");
            return;
        }

        /* ================= READ BODY ================= */
        UpdateReq data = mapper.readValue(req.getInputStream(), UpdateReq.class);

        /* ================= UPDATE PROFILE ================= */
        try {
            userDao.updateProfile(
                email,
                data.age,
                data.weight,
                data.weeklyExerciseGoal,
                data.dailyWaterGoal,
                data.dailySleepGoal
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"status\":\"success\"}");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"Server error\"}");
        }
    }
}
