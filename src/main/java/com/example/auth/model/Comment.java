package com.example.auth.model;

import java.time.LocalDateTime;

public class Comment {
    private int id;
    private int blogId;
    private int userId;
    private String comment;
    private LocalDateTime commentedAt;
    
    private String userName;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBlogId() { return blogId; }
    public void setBlogId(int blogId) { this.blogId = blogId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCommentedAt() { return commentedAt; }
    public void setCommentedAt(LocalDateTime commentedAt) { this.commentedAt = commentedAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}

