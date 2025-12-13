package com.example.auth.servlet;

import com.example.auth.dao.UserDao;
import com.example.auth.model.User;
import com.example.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;

@WebServlet("/update-profile")
public class UpdateProfileServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private final ObjectMapper mapper = new ObjectMapper();

    static class UpdateReq {
        public Integer age;
        public Double weight;
        public String goals;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

      
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

        UpdateReq data = mapper.readValue(req.getInputStream(), UpdateReq.class);

        try {
         
            userDao.updateProfile(email, data.age, data.weight, data.goals);

            resp.setContentType("application/json");
            resp.getWriter().write("{\"status\":\"success\"}");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"Server error\"}");
        }
    }
}

