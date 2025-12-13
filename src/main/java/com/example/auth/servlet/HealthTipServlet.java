package com.example.auth.servlet;

import com.example.auth.dao.HealthTipDao;
import com.example.auth.model.HealthTip;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/health/tip")
public class HealthTipServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();
    private final HealthTipDao dao = new HealthTipDao();

    private static final Map<String, List<String>> TIPS = new HashMap<>();
    private static final Map<String, String> IMAGE_MAP = new HashMap<>();

    static {
        TIPS.put("hydration", Arrays.asList(
                "Drink at least 8 cups of water daily.",
                "Begin your day with a glass of water.",
                "Proper hydration improves focus and energy."
        ));
        TIPS.put("nutrition", Arrays.asList(
                "Eat at least one serving of leafy greens today.",
                "Choose whole fruits instead of sugary drinks.",
                "Add protein to every meal to stay full longer."
        ));
        TIPS.put("fitness", Arrays.asList(
                "Walk at least 30 minutes today.",
                "Stretch your body for 5 minutes in the morning.",
                "Light exercise improves mood instantly."
        ));
        TIPS.put("sleep", Arrays.asList(
                "Aim for at least 7 to 8 hours of sleep tonight.",
                "Avoid screens for 45 minutes before bedtime.",
                "Good sleep boosts immunity and memory."
        ));
        TIPS.put("mental", Arrays.asList(
            "Take a 2 minute deep breathing break.",
            "Practice gratitude—it reduces stress instantly.",
            "Short breaks improve mental clarity."
        ));

        IMAGE_MAP.put("hydration", "https://images.unsplash.com/photo-1526403220919-1f3d71330a52?w=1200");
        IMAGE_MAP.put("nutrition", "https://images.unsplash.com/photo-1526403220919-1f3d71330a52?w=1200");
        IMAGE_MAP.put("fitness", "https://images.unsplash.com/photo-1526403220919-1f3d71330a52?w=1200");
        IMAGE_MAP.put("sleep", "https://images.unsplash.com/photo-1526403220919-1f3d71330a52?w=1200");
        IMAGE_MAP.put("mental", "https://images.unsplash.com/photo-1526403220919-1f3d71330a52?w=1200");
        IMAGE_MAP.put("default", "https://images.unsplash.com/photo-1526403220919-1f3d71330a52?w=1200");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String today = LocalDate.now().toString();

        try {
            // If today's tip exists, return it
            HealthTip saved = dao.findTipByDate(today);
            if (saved != null) {
                writeResponse(resp, saved);
                return;
            }

            // Pick category randomly
            List<String> keys = new ArrayList<>(TIPS.keySet());
            String category = keys.get(new Random().nextInt(keys.size()));

            // Pick a tip randomly
            List<String> tipList = TIPS.get(category);
            String text = tipList.get(new Random().nextInt(tipList.size()));

            // Pick image
            String image = IMAGE_MAP.getOrDefault(category, IMAGE_MAP.get("default"));

            // Save tip
            HealthTip tip = new HealthTip();
            tip.setText(text);
            tip.setCategory(category);
            tip.setDate(today);
            tip.setSource("SmartHealth Local Tips");
            tip.setImageUrl(image);

            dao.saveTip(tip);

            writeResponse(resp, tip);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"Server error\"}");
        }
    }

    private void writeResponse(HttpServletResponse resp, HealthTip tip) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("text", tip.getText());
        map.put("category", tip.getCategory());
        map.put("image", tip.getImageUrl());
        map.put("date", tip.getDate());
        map.put("source", tip.getSource());

        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(), map);
    }
}
