package com.example.auth.servlet;

import com.example.auth.dao.BlogDao;
import com.example.auth.dao.BlogLikeDao;
import com.example.auth.dao.CommentDao;
import com.example.auth.model.Blog;
import com.example.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/blogs/*")
public class BlogServlet extends HttpServlet {
    private final BlogDao blogDao = new BlogDao();
    private final BlogLikeDao likeDao = new BlogLikeDao();
    private final CommentDao commentDao = new CommentDao();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public void init() {
        blogDao.createTable();
        likeDao.createTable();
        commentDao.createTable();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setHeader("Cache-Control", "no-cache");
        String path = req.getPathInfo();

        try {
            if (path == null || path.equals("/")) {
                String category = req.getParameter("category");
                String featured = req.getParameter("featured");
                List<Blog> blogs;
                if ("true".equals(featured)) {
                    blogs = blogDao.getFeatured();
                } else if (category != null && !category.isEmpty()) {
                    blogs = blogDao.getByCategory(category);
                } else {
                    blogs = blogDao.getAll();
                }
                mapper.writeValue(resp.getWriter(), blogs);
            } else {
                int id = Integer.parseInt(path.substring(1));
                Blog blog = blogDao.getById(id);
                if (blog == null) {
                    resp.setStatus(404);
                    resp.getWriter().write("{\"error\":\"Blog not found\"}");
                    return;
                }
                Map<String, Object> result = new HashMap<>();
                result.put("blog", blog);
                result.put("commentCount", commentDao.getCountByBlogId(id));
                
                String token = req.getHeader("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    int userId = JwtUtil.getUserId(token.substring(7));
                    result.put("liked", likeDao.hasLiked(id, userId));
                }
                mapper.writeValue(resp.getWriter(), result);
            }
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
            BlogRequest blogReq = mapper.readValue(req.getReader(), BlogRequest.class);
            
            Blog blog = new Blog();
            blog.setAuthorId(userId);
            blog.setTitle(blogReq.title);
            blog.setContent(blogReq.content);
            blog.setCategory(blogReq.category);
            blog.setImageUrl(null);
            
            int id = blogDao.insert(blog);
            if (id > 0) {
                resp.getWriter().write("{\"success\":true,\"id\":" + id + "}");
            } else {
                resp.setStatus(500);
                resp.getWriter().write("{\"error\":\"Failed to create blog\"}");
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
            BlogRequest blogReq = mapper.readValue(req.getReader(), BlogRequest.class);
            
            Blog blog = new Blog();
            blog.setId(blogId);
            blog.setAuthorId(userId);
            blog.setTitle(blogReq.title);
            blog.setContent(blogReq.content);
            blog.setCategory(blogReq.category);
            blog.setImageUrl(blogReq.imageUrl);
            
            if (blogDao.update(blog)) {
                resp.getWriter().write("{\"success\":true}");
            } else {
                resp.setStatus(403);
                resp.getWriter().write("{\"error\":\"Cannot update blog\"}");
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
            String role = JwtUtil.getRole(token.substring(7));
            
            if (blogDao.delete(blogId, userId, role)) {
                resp.getWriter().write("{\"success\":true}");
            } else {
                resp.setStatus(403);
                resp.getWriter().write("{\"error\":\"Cannot delete blog\"}");
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    static class BlogRequest {
        public String title;
        public String content;
        public String category;
        public String imageUrl;
    }
}
