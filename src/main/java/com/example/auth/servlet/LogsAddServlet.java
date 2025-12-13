package com.example.auth.servlet;

import com.example.auth.dao.LogDao;
import com.example.auth.model.LogEntry;
import com.example.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/logs/add")
public class LogsAddServlet extends HttpServlet {

    private final LogDao LogDao = new LogDao();
    private final ObjectMapper mapper = new ObjectMapper();

    static class LogReq {
        public Double waterIntake;
        public Double sleepHours;
        public String notes;
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

        LogReq data = mapper.readValue(req.getInputStream(), LogReq.class);

        try {
            LogEntry log = new LogEntry();
            log.setEmail(email);
            log.setWaterIntake(data.waterIntake);
            log.setSleepHours(data.sleepHours);
            log.setNotes(data.notes);
            log.setLogDate(data.date);

            LogDao.addLog(log);

            resp.getWriter().write("{\"status\":\"saved\"}");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"Server error\"}");
        }
    }
}
