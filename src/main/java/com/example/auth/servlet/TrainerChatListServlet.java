package com.example.auth.servlet;

import com.example.auth.dao.ChatDao;
import com.example.auth.util.JwtUtil;
import com.example.auth.util.UserUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;


@WebServlet("/trainer/chats")
public class TrainerChatListServlet extends HttpServlet {

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
            int trainerId = UserUtil.getTrainerIdFromToken(auth);
            mapper.writeValue(
                resp.getWriter(),
                chatDao.getTrainerUsers(trainerId)
            );
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
        }
    }
}
