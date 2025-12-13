package com.example.auth.servlet;

import com.example.auth.dao.UserDao;
import com.example.auth.model.User;
import com.example.auth.util.JwtUtil;
import com.example.auth.util.PasswordUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;


@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();
    private final ObjectMapper mapper = new ObjectMapper();

    static class LoginRequest { public String email; public String password; }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LoginRequest lr = mapper.readValue(req.getInputStream(), LoginRequest.class);
        if (lr.email == null || lr.password == null) {
            resp.setStatus(400); resp.getWriter().write("{\"error\":\"email & password required\"}"); return;
        }
        try {
            User u = userDao.findByEmail(lr.email);
            if (u == null || !PasswordUtil.verify(lr.password, u.getPasswordHash())) {
                resp.setStatus(401); resp.getWriter().write("{\"error\":\"invalid credentials\"}"); return;
            }
            String token = JwtUtil.generateToken(u.getId(), u.getEmail(), u.getRole());
            resp.setContentType("application/json");
            resp.getWriter().write("{\"token\":\"" + token + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500); resp.getWriter().write("{\"error\":\"server error\"}");
        }
    }
}