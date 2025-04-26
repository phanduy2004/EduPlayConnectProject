package com.myjob.real_time_chat_final.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Post implements Serializable {
    private int id;
    private User user;
    private String content;
    private String privacy; // PUBLIC, FRIENDS, PRIVATE
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int likeCount; // Số lượng like
    private List<Comment> comments; // Danh sách comment cấp 1
    private String imageUri; // URI của hình ảnh (sẽ được thay bằng URL khi tích hợp API)

    public Post(int id, User user, String content, String privacy, Timestamp createdAt, Timestamp updatedAt, int likeCount, List<Comment> comments, String imageUri) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.privacy = privacy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likeCount = likeCount;
        this.comments = comments != null ? comments : new ArrayList<>();
        this.imageUri = imageUri;
    }

    public Post() {
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public String getPrivacy() {
        return privacy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}