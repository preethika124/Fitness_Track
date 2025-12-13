package com.example.auth.servlet;

import com.example.auth.dao.BmiDao;
import com.example.auth.model.BMI;
import com.example.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/bmi/save")
public class BmiServlet extends HttpServlet {

    private final BmiDao bmiDao = new BmiDao();
    private final ObjectMapper mapper = new ObjectMapper();

    static class BmiReq {
        public double bmiValue;
        public String status;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 1. Validate token
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"Missing or invalid token\"}");
            return;
        }

        String email;
        try {
            email = JwtUtil.parseToken(auth.substring(7)).getBody().getSubject();
        } catch (Exception e) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"Invalid token\"}");
            return;
        }

        // 2. Parse JSON body
        BmiReq data = mapper.readValue(req.getInputStream(), BmiReq.class);

        try {
            BMI bmi = new BMI();
            bmi.setEmail(email);
            bmi.setBmiValue(data.bmiValue);
            bmi.setStatus(data.status);

            bmiDao.saveBMI(bmi);

            resp.getWriter().write("{\"status\":\"saved\"}");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"Server error\"}");
        }
    }
}
