package com.example.auth.servlet;

import com.example.auth.dao.UserDao;
import com.example.auth.model.User;
import com.example.auth.util.PasswordUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    private final UserDao userDao = new UserDao();
    private final ObjectMapper mapper = new ObjectMapper();

    static class RegisterRequest {
        public String email;
        public String password;
        public String firstName;
        public String lastName;
        public String role;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        RegisterRequest rr = mapper.readValue(req.getInputStream(), RegisterRequest.class);

        if (rr.email == null || rr.password == null || rr.firstName == null || rr.lastName == null) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"email, password, first & last name required\"}");
            return;
        }

        try {
            if (userDao.findByEmail(rr.email) != null) {
                resp.setStatus(409);
                resp.getWriter().write("{\"error\":\"user exists\"}");
                return;
            }

            User u = new User();
            u.setEmail(rr.email);
            u.setPasswordHash(PasswordUtil.hash(rr.password));
            u.setFirstName(rr.firstName);
            u.setLastName(rr.lastName);
            u.setRole(rr.role != null ? rr.role : "USER");

            boolean ok = userDao.createUser(u);

            if (ok) {
                resp.setStatus(201);
                resp.getWriter().write("{\"message\":\"registered\"}");
            } else {
                resp.setStatus(500);
                resp.getWriter().write("{\"error\":\"could not create user\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"server error\"}");
        }
    }
}

