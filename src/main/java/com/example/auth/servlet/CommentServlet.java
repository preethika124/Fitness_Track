package com.example.auth.servlet;

import com.example.auth.dao.CommentDao;
import com.example.auth.model.Comment;
import com.example.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/comments/*")
public class CommentServlet extends HttpServlet {
    private final CommentDao commentDao = new CommentDao();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setHeader("Cache-Control", "no-cache");
        
        String blogIdParam = req.getParameter("blogId");
        if (blogIdParam == null) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"blogId required\"}");
            return;
        }

        try {
            int blogId = Integer.parseInt(blogIdParam);
            List<Comment> comments = commentDao.getByBlogId(blogId);
            mapper.writeValue(resp.getWriter(), comments);
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

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
            CommentRequest cmReq = mapper.readValue(req.getReader(), CommentRequest.class);
            
            Comment comment = new Comment();
            comment.setBlogId(cmReq.blogId);
            comment.setUserId(userId);
            comment.setComment(cmReq.comment);
            
            int id = commentDao.insert(comment);
            if (id > 0) {
                resp.getWriter().write("{\"success\":true,\"id\":" + id + "}");
            } else {
                resp.setStatus(500);
                resp.getWriter().write("{\"error\":\"Failed to add comment\"}");
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
            resp.getWriter().write("{\"error\":\"Comment ID required\"}");
            return;
        }

        try {
            int commentId = Integer.parseInt(path.substring(1));
            int userId = JwtUtil.getUserId(token.substring(7));
            String role = JwtUtil.getRole(token.substring(7));
            
            if (commentDao.delete(commentId, userId, role)) {
                resp.getWriter().write("{\"success\":true}");
            } else {
                resp.setStatus(403);
                resp.getWriter().write("{\"error\":\"Cannot delete comment\"}");
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    static class CommentRequest {
        public int blogId;
        public String comment;
    }
}
