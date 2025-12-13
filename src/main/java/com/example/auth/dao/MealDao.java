package com.example.auth.dao;

import com.example.auth.model.Meal;
import com.example.auth.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MealDao {

   public void addMeal(Meal m) throws SQLException {

    String sql = "INSERT INTO meals (user_email, meal_type, calorie_count, protein, carbs, fats, meal_date) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection c = DbUtil.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, m.getEmail());
        ps.setString(2, m.getMealType());
        ps.setObject(3, m.getCalories());
        ps.setObject(4, m.getProtein());
        ps.setObject(5, m.getCarbs());
        ps.setObject(6, m.getFats());
        ps.setDate(7, java.sql.Date.valueOf(m.getMealDate()));  // ✅ FIXED

        ps.executeUpdate();
    }
}


    public List<Meal> getMealsForToday(String email) throws SQLException {

        String sql = "SELECT * FROM meals WHERE user_email = ? AND meal_date = CURRENT_DATE";

        List<Meal> list = new ArrayList<>();

        try (Connection c = DbUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Meal m = new Meal();
                m.setId(rs.getInt("id"));
                m.setEmail(email);
                m.setMealType(rs.getString("meal_type"));
                m.setCalories(rs.getDouble("calorie_count"));
                m.setProtein(rs.getDouble("protein"));
                m.setCarbs(rs.getDouble("carbs"));
                m.setFats(rs.getDouble("fats"));
                m.setMealDate(rs.getString("meal_date"));
                list.add(m);
            }
        }
        return list;
    }
}
