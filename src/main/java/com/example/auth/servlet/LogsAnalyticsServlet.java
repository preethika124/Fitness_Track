
package com.example.auth.servlet;

import com.example.auth.dao.LogsAnalyticsDao;
import com.example.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/analytics/logs")
public class LogsAnalyticsServlet extends HttpServlet {

    private LogsAnalyticsDao dao = new LogsAnalyticsDao();
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
         if (email == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("[]");
            return;
        }
        resp.setContentType("application/json");

        mapper.writeValue(resp.getWriter(), dao.daily(email));
    }
}
