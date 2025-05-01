package com.myjob.real_time_chat_final.modelDTO;

import java.util.Date;

public class CommentNotificationDTO {
    private Long id;                    // ID của bình luận
    private Long postId;                // ID của bài đăng
    private Long userId;                // ID của người dùng
    private String username;            // Tên người dùng
    private String content;             // Nội dung bình luận
    private String avatarUrl;           // URL avatar của người dùng
    private Date createdAt;             // Thời gian tạo
    private Long parentCommentId;       // ID của bình luận cha (nếu là trả lời)

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
}