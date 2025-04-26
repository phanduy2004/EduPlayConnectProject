package com.myjob.real_time_chat_final.modelDTO;




public class PostRequestDTO {
    private Long userId;
    private String content;
    private String privacy;
    private String imageUrl; // Ảnh được gửi dưới dạng Base64 (nếu có)

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}