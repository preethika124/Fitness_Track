package com.example.auth.servlet;

import com.example.auth.dao.MealDao;
import com.example.auth.model.Meal;
import com.example.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/meals/add")
public class MealsAddServlet extends HttpServlet {

    private final MealDao mealsDao = new MealDao();
    private final ObjectMapper mapper = new ObjectMapper();

    static class MealReq {
        public String mealType;
        public Double calories;
        public Double protein;
        public Double carbs;
        public Double fats;
        public String date;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"Missing token\"}");
            return;
        }

        String token = auth.substring(7);
        String email;

        try {
            email = JwtUtil.parseToken(token).getBody().getSubject();
        } catch (Exception e) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"Invalid token\"}");
            return;
        }

        MealReq data = mapper.readValue(req.getInputStream(), MealReq.class);

        try {
            Meal m = new Meal();
            m.setEmail(email);
            m.setMealType(data.mealType);
            m.setCalories(data.calories);
            m.setProtein(data.protein);
            m.setCarbs(data.carbs);
            m.setFats(data.fats);
            m.setMealDate(data.date);

            mealsDao.addMeal(m);

            resp.getWriter().write("{\"status\":\"saved\"}");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"Server error\"}");
        }
    }
}
