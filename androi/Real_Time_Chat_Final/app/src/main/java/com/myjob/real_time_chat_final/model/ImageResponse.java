package com.myjob.real_time_chat_final.model;

public class ImageResponse {
    private String imageUrl;
    private String error; // Ensure this field exists if the API returns it

    // Getters and setters
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
