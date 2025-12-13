package com.example.auth.servlet;

import com.example.auth.dao.WorkoutDao;
import com.example.auth.dao.MealDao;
import com.example.auth.dao.LogDao;
import com.example.auth.dao.UserDao;

import com.example.auth.model.Workout;
import com.example.auth.model.Meal;
import com.example.auth.model.LogEntry;
import com.example.auth.model.User;

import com.example.auth.util.JwtUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final WorkoutDao workoutDao = new WorkoutDao();
    private final MealDao mealDao = new MealDao();
    private final LogDao logDao = new LogDao();
    private final UserDao userDao = new UserDao();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // Validate token
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

        try {
            // Fetch full details — NOT counts
            List<Workout> workouts = workoutDao.getWorkoutsForToday(email);
            List<Meal> meals = mealDao.getMealsForToday(email);
            LogEntry log = logDao.getTodayLog(email);

            User user = userDao.findByEmail(email);

            // Build JSON response
            Map<String, Object> result = new HashMap<>();
            result.put("workouts", workouts);
            result.put("meals", meals);
            result.put("log", log);
            result.put("firstName", user.getFirstName());
            result.put("role", user.getRole());

            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), result);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"Server error\"}");
        }
    }
}
