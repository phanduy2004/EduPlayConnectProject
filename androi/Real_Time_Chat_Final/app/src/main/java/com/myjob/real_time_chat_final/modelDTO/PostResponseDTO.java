package com.myjob.real_time_chat_final.modelDTO;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class PostResponseDTO {
    private Long id;                    // ID của bài đăng
    private Long userId;                // ID của người dùng tạo bài đăng
    private String username;            // Tên người dùng
    private String avatarUrl;           // URL ảnh đại diện của người dùng
    private String content;
    @SerializedName("imageUrl")// Nội dung bài đăng
    private List<String> imageUrl;            // URL hình ảnh của bài đăng (nếu có)
    private String privacy;             // Quyền riêng tư (PUBLIC, FRIENDS, PRIVATE)
    private Date createdAt;             // Thời gian tạo bài đăng
    private int likeCount;              // Số lượt thích
    private List<CommentDTO> comments;  // Danh sách bình luận
    private double score;               // Điểm số (dùng để sắp xếp bài đăng)

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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}