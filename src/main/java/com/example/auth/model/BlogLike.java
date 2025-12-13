
package com.example.auth.model;

import java.time.LocalDateTime;

public class BlogLike {
    private int id;
    private int blogId;
    private int userId;
    private LocalDateTime likedAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBlogId() { return blogId; }
    public void setBlogId(int blogId) { this.blogId = blogId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDateTime getLikedAt() { return likedAt; }
    public void setLikedAt(LocalDateTime likedAt) { this.likedAt = likedAt; }
}
