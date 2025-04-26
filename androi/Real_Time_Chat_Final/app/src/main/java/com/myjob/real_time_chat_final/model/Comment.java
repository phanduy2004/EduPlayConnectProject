package com.myjob.real_time_chat_final.model;


import java.io.Serializable;
import java.sql.Timestamp;

public class Comment implements Serializable {
    private Long id;
    private User user;
    private String content;
    private Timestamp createdAt;

    public Comment(Long id, User user, String content, Timestamp createdAt) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
