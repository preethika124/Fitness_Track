package com.example.auth.servlet;



import com.example.auth.dao.UserDao;
import com.example.auth.model.User;
import com.example.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            resp.setStatus(401); resp.getWriter().write("{\"error\":\"missing token\"}"); return;
        }
        String token = auth.substring(7);
        try {
            Jws<Claims> claims = JwtUtil.parseToken(token);
            String email = claims.getBody().getSubject();
            User u = userDao.findByEmail(email);
            if (u == null) { resp.setStatus(404); resp.getWriter().write("{\"error\":\"user not found\"}"); return; }
        
            resp.setContentType("application/json");
            resp.getWriter().write(mapper.writeValueAsString(u));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"invalid token\"}");
        }
    }
}
