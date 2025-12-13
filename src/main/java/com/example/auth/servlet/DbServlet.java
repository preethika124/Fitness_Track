package com.example.auth.servlet;

import com.example.auth.dao.DashboardDao;
import com.example.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;

@WebServlet("/dashboard/summary")
public class DbServlet extends HttpServlet {

    private DashboardDao dao = new DashboardDao();
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

        Map<String, Object> data = dao.getSummary(email);
        mapper.writeValue(resp.getWriter(), data);
    }
}
