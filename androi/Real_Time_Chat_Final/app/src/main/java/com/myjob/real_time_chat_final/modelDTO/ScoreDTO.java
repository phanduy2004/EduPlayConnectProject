package com.myjob.real_time_chat_final.modelDTO;

public class ScoreDTO {
    public Long id;
    public String username;
    public int score;
    public String completedAt;
    public String avatarUrl; // Thêm trường avatarUrl

    public ScoreDTO() {}

    public ScoreDTO(Long id, String username, int score, String completedAt, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.score = score;
        this.completedAt = completedAt;
        this.avatarUrl = avatarUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}