package com.myjob.real_time_chat_final.modelDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentDTO {
    private Long id;                    // ID của bình luận
    private Long userId;
    private String avatarUrl;
    private Long postId; // Thêm trường postId// ID của người dùng tạo bình luận
    private String username;            // Tên người dùng
    private String content;             // Nội dung bình luận
    private Date createdAt;             // Thời gian tạo bình luận
    private Long parentCommentId;       // ID của bình luận cha (nếu là trả lời)
    private List<CommentDTO> replies;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    // Getter và Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<CommentDTO> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentDTO> replies) {
        this.replies = replies;
    }
}