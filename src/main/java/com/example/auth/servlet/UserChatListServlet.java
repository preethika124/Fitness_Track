package com.example.auth.servlet;

import com.example.auth.dao.ChatDao;
import com.example.auth.util.JwtUtil;
import com.example.auth.util.UserUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/user/chats")
public class UserChatListServlet extends HttpServlet {

    private final ChatDao chatDao = new ChatDao();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            String email = JwtUtil.parseToken(auth.substring(7))
                                   .getBody()
                                   .getSubject();

            int userId = UserUtil.getUserIdByEmail(email);

            mapper.writeValue(
                resp.getWriter(),
                chatDao.getUserTrainers(userId)
            );

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
