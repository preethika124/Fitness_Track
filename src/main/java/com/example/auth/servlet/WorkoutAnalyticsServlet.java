package com.example.auth.servlet;

import com.example.auth.dao.WorkoutAnalyticsDao;
import com.example.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/analytics/workouts")
public class WorkoutAnalyticsServlet extends HttpServlet {

    private WorkoutAnalyticsDao dao = new WorkoutAnalyticsDao();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String token = req.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }
        String email=JwtUtil.email(token);
        resp.setContentType("application/json");

        mapper.writeValue(resp.getWriter(), dao.dailyStats(email));
    }
}
