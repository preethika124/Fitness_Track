package com.example.auth.servlet;

import com.example.auth.dao.ChatDao;
import com.example.auth.util.JwtUtil;
import com.example.auth.util.UserUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/chat/messages")
public class ChatMessagesServlet extends HttpServlet {

    private final ChatDao chatDao = new ChatDao();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            resp.setStatus(401); return;
        }

        try {
            String email = JwtUtil.parseToken(auth.substring(7)).getBody().getSubject();
            
            String trainerIdParam = req.getParameter("trainerId");
            String userIdParam = req.getParameter("userId");

            if (trainerIdParam != null) {
                // User is requesting messages with a specific trainer
                int userId = UserUtil.getUserIdByEmail(email);
                int trainerId = Integer.parseInt(trainerIdParam);
                mapper.writeValue(resp.getWriter(), chatDao.getMessages(userId, trainerId));
            } else if (userIdParam != null) {
                // Trainer is requesting messages with a specific user
                int trainerId = UserUtil.getTrainerIdFromToken(auth);
                int userId = Integer.parseInt(userIdParam);
                mapper.writeValue(resp.getWriter(), chatDao.getMessages(userId, trainerId));
            } else {
                resp.setStatus(400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
        }
    }
}