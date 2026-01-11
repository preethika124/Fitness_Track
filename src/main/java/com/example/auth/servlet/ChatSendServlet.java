package com.example.auth.servlet;

import com.example.auth.dao.ChatDao;
import com.example.auth.util.JwtUtil;
import com.example.auth.util.UserUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/chat/send")
public class ChatSendServlet extends HttpServlet {

    private final ChatDao chatDao = new ChatDao();
    private final ObjectMapper mapper = new ObjectMapper();

    static class Req {
        public Integer trainerId;
        public Integer userId;
        public String message;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            resp.setStatus(401); return;
        }

        try {
            String email = JwtUtil.parseToken(auth.substring(7)).getBody().getSubject();
            Req r = mapper.readValue(req.getInputStream(), Req.class);
            
            if (r.trainerId != null) {
                // Sent by User to Trainer
                int userId = UserUtil.getUserIdByEmail(email);
                chatDao.saveMessage(userId, r.trainerId, "USER", r.message);
            } else if (r.userId != null) {
                // Sent by Trainer to User
                int trainerId = UserUtil.getTrainerIdFromToken(auth);
                chatDao.saveMessage(r.userId, trainerId, "TRAINER", r.message);
            }

            resp.getWriter().write("{\"status\":\"sent\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
        }
    }
}