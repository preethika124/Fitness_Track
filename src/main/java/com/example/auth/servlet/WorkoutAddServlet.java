package com.example.auth.servlet;

import com.example.auth.dao.WorkoutDao;
import com.example.auth.model.Workout;
import com.example.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/workout/add")
public class WorkoutAddServlet extends HttpServlet {

    private final WorkoutDao workoutDao = new WorkoutDao();
    private final ObjectMapper mapper = new ObjectMapper();

    static class WorkoutReq {
        public String workoutType;
        public Integer durationMinutes;
        public Double caloriesBurned;
        public String date;  // yyyy-MM-dd
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // Token Validation
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"Missing token\"}");
            return;
        }

        String email;
        try {
            email = JwtUtil.parseToken(auth.substring(7)).getBody().getSubject();
        } catch (Exception e) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"Invalid token\"}");
            return;
        }

        // Parse JSON
        WorkoutReq data = mapper.readValue(req.getInputStream(), WorkoutReq.class);

        try {
            Workout w = new Workout();
            w.setEmail(email);
            w.setWorkoutType(data.workoutType);
            w.setDurationMinutes(data.durationMinutes);
            w.setCaloriesBurned(data.caloriesBurned);
            w.setWorkoutDate(data.date);

            workoutDao.addWorkout(w);

            resp.setContentType("application/json");
            resp.getWriter().write("{\"status\":\"saved\"}");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"Server error\"}");
        }
    }
}
