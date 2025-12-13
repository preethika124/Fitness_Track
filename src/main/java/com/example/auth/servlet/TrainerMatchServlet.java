package com.example.auth.servlet;

import com.example.auth.dao.TrainerDao;
import com.example.auth.model.Trainer;
import com.example.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


@WebServlet("/trainers")
public class TrainerMatchServlet extends HttpServlet {
    public static class Req {
            public String specialization;
            public Integer experienceYears;
            public String availability;
          
        }

    private final TrainerDao trainerDao = new TrainerDao();
    private final ObjectMapper mapper = new ObjectMapper();

    // ============================================================
    // GET  → MATCH TRAINERS BASED ON GOAL
    // ============================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json; charset=UTF-8");

        String goalParam = req.getParameter("goal");
        if (goalParam == null || goalParam.trim().isEmpty()) {
            // accept 'q' as a shorthand from client-side (trainers.js uses q=...)
            goalParam = req.getParameter("q");
        }
        if (goalParam == null || goalParam.trim().isEmpty()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"goal query parameter required\"}");
            return;
        }
        final String goal = goalParam.trim();
        final String lowerGoal = goal.toLowerCase();

        try {
            List<Trainer> trainers = trainerDao.getAllTrainers();

            List<Map<String, Object>> matched = trainers.stream()
                    .map(t -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("id", t.getId());
                        m.put("name", t.getName() == null ? ("trainer-" + t.getId()) : t.getName());
                        m.put("specialization", t.getSpecialization());
                        m.put("experienceYears", t.getExperienceYears());
                        m.put("availability", t.getAvailability());
                        m.put("bio", t.getBio());
                        m.put("userEmail", t.getUserEmail());

                        return m;
                    })
                    .filter(m -> {
                        String spec = Optional.ofNullable((String) m.get("specialization")).orElse("").toLowerCase();

                        if (spec.contains(lowerGoal)) return true;
                        if (lowerGoal.contains("muscle") && spec.contains("muscle")) return true;
                        if (lowerGoal.contains("weight") && spec.contains("weight")) return true;
                        if (lowerGoal.contains("endurance") && spec.contains("endurance")) return true;
                        if (lowerGoal.contains("strength") && spec.contains("strength")) return true;

                        return false;
                    })
                    .map(m -> {
                        m.put("matchReason", "Specialization matches '" + goal + "'");
                        return m;
                    })
                    .collect(Collectors.toList());

        
            mapper.writeValue(resp.getWriter(), matched);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"server error\"}");
        }
    }

    // ============================================================
    // POST → TRAINER ENROLLMENT
    // ============================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json; charset=UTF-8");

        // -------------------------
        // 1. Validate JWT
        // -------------------------
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"missing token\"}");
            return;
        }

        String email;
        try {
            email = JwtUtil.parseToken(auth.substring(7)).getBody().getSubject();
        } catch (Exception ex) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"invalid token\"}");
            return;
        }

        // -------------------------
        // 2. Read JSON request
        // -------------------------
       

        Req r;
        String raw = new String(req.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
System.out.println("RAW BODY RECEIVED = " + raw);


try {
    r = mapper.readValue(raw, Req.class);
} catch (Exception ex) {
    ex.printStackTrace();
    resp.setStatus(400);
    resp.getWriter().write("{\"error\":\"invalid JSON\"}");
    return;
}


        // -------------------------
        // 3. Validate required fields
        // -------------------------
        if (r.specialization == null || r.specialization.trim().isEmpty()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"specialization required\"}");
            return;
        }

        try {
            // -------------------------
            // 4. Create trainer object
            // -------------------------
            Trainer t = new Trainer();
            t.setUserEmail(email);
            t.setSpecialization(r.specialization.trim());
            t.setExperienceYears(r.experienceYears);
            t.setAvailability(r.availability);
         

            Trainer created = trainerDao.createTrainer(t);

            resp.setStatus(201);
            mapper.writeValue(resp.getWriter(), created);
        } catch (IllegalStateException ise) {
            // user exists but is not a trainer
            ise.printStackTrace();
            resp.setStatus(403);
            resp.getWriter().write("{\"error\":\"you-must-be-trainer\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"server error\"}");
        }
    }
}
