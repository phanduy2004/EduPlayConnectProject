package com.myjob.real_time_chat_final.modelDTO;


import java.util.List;

public class PostRequestDTO {
    private Long userId;
    private String content;
    private String privacy;
    private List<String> imageUrl; // Ảnh được gửi dưới dạng Base64 (nếu có)

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }
}