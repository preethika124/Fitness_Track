package com.example.auth.servlet;

import com.example.auth.dao.HealthTipDao;
import com.example.auth.model.HealthTip;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/health/tip")
public class HealthTipServlet extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HealthTipDao dao = new HealthTipDao();
    private static final Random RANDOM = new Random();

    private static final Map<String, List<String>> TIPS = new HashMap<>();
    private static final Map<String, String> IMAGE_MAP = new HashMap<>();

    // ---------------- STATIC DATA ----------------
    static {

        // Hydration
        TIPS.put("hydration", Arrays.asList(
                "Drink at least 8 cups of water daily.",
                "Begin your day with a glass of water.",
                "Proper hydration improves focus and energy."
        ));

        // Nutrition
        TIPS.put("nutrition", Arrays.asList(
                "Eat at least one serving of leafy greens today.",
                "Choose whole fruits instead of sugary drinks.",
                "Add protein to every meal to stay full longer."
        ));

        // Fitness
        TIPS.put("fitness", Arrays.asList(
                "Walk at least 30 minutes today.",
                "Stretch your body for 5 minutes in the morning.",
                "Light exercise improves mood instantly."
        ));

        // Sleep
        TIPS.put("sleep", Arrays.asList(
                "Aim for at least 7 to 8 hours of sleep tonight.",
                "Avoid screens for 45 minutes before bedtime.",
                "Good sleep boosts immunity and memory."
        ));

        // Mental health
        TIPS.put("mental", Arrays.asList(
                "Take a 2 minute deep breathing break.",
                "Practice gratitude—it reduces stress instantly.",
                "Short breaks improve mental clarity."
        ));

        // -------- IMAGE PATHS (RELATIVE, CONTEXT-SAFE) --------
        IMAGE_MAP.put("hydration", "/images/hydration.jpg");
        IMAGE_MAP.put("nutrition", "/images/nutrition.jpg");
        IMAGE_MAP.put("fitness", "/images/fitness.jpg");
        IMAGE_MAP.put("sleep", "/images/health.jpg");
        IMAGE_MAP.put("mental", "/images/mental.jpg");
        IMAGE_MAP.put("default", "/images/default.jpg");
    }

    // ---------------- GET DAILY HEALTH TIP ----------------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String today = LocalDate.now().toString();

        try {
            // 1️⃣ Return today's saved tip if exists
            HealthTip savedTip = dao.findTipByDate(today);
            if (savedTip != null) {
                writeResponse(resp, savedTip, req);
                return;
            }

            // 2️⃣ Pick random category
            List<String> categories = new ArrayList<>(TIPS.keySet());
            String category = categories.get(RANDOM.nextInt(categories.size()));

            // 3️⃣ Pick random tip
            List<String> tipList = TIPS.get(category);
            String tipText = tipList.get(RANDOM.nextInt(tipList.size()));

            // 4️⃣ Pick image
            String imagePath = IMAGE_MAP.getOrDefault(category, IMAGE_MAP.get("default"));

            // 5️⃣ Create and save tip
            HealthTip tip = new HealthTip();
            tip.setText(tipText);
            tip.setCategory(category);
            tip.setDate(today);
            tip.setSource("SmartHealth Local Tips");
            tip.setImageUrl(imagePath);

            dao.saveTip(tip);

            // 6️⃣ Respond
            writeResponse(resp, tip, req);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Internal server error\"}");
        }
    }

    // ---------------- RESPONSE BUILDER ----------------
    private void writeResponse(HttpServletResponse resp, HealthTip tip, HttpServletRequest req)
            throws IOException {

        Map<String, Object> response = new HashMap<>();
        response.put("text", tip.getText());
        response.put("category", tip.getCategory());
        response.put("date", tip.getDate());
        response.put("source", tip.getSource());

        // Build FULL image URL safely
        String fullImageUrl = req.getContextPath() + tip.getImageUrl();
        response.put("image", fullImageUrl);

        mapper.writeValue(resp.getWriter(), response);
    }
}
