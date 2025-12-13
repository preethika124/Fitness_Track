package com.example.auth.servlet;

import com.example.auth.dao.BlogDao;
import com.example.auth.dao.BlogLikeDao;
import com.example.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/api/likes/*")
public class LikeServlet extends HttpServlet {
    private final BlogLikeDao likeDao = new BlogLikeDao();
    private final BlogDao blogDao = new BlogDao();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String token = req.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }

        try {
            int userId = JwtUtil.getUserId(token.substring(7));
            LikeRequest likeReq = mapper.readValue(req.getReader(), LikeRequest.class);
            
            if (likeDao.hasLiked(likeReq.blogId, userId)) {
                resp.getWriter().write("{\"success\":false,\"message\":\"Already liked\"}");
                return;
            }
            
            if (likeDao.addLike(likeReq.blogId, userId)) {
                blogDao.incrementLikes(likeReq.blogId);
                resp.getWriter().write("{\"success\":true,\"liked\":true}");
            } else {
                resp.setStatus(500);
                resp.getWriter().write("{\"error\":\"Failed to like\"}");
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String token = req.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }

        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"Blog ID required\"}");
            return;
        }

        try {
            int blogId = Integer.parseInt(path.substring(1));
            int userId = JwtUtil.getUserId(token.substring(7));
            
            if (likeDao.removeLike(blogId, userId)) {
                blogDao.decrementLikes(blogId);
                resp.getWriter().write("{\"success\":true,\"liked\":false}");
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"Not liked\"}");
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    static class LikeRequest {
        public int blogId;
    }
}
