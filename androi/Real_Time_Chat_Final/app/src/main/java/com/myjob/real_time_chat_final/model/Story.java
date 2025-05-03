package com.myjob.real_time_chat_final.model;


public class Story {
    private boolean isCreateStory;
    private String username;
    private Integer avatarResId;
    private Integer imageResId;
    private boolean isUnseen;

    public Story(boolean isCreateStory, String username, Integer avatarResId, Integer imageResId, boolean isUnseen) {
        this.isCreateStory = isCreateStory;
        this.username = username;
        this.avatarResId = avatarResId;
        this.imageResId = imageResId;
        this.isUnseen = isUnseen;
    }

    public boolean isCreateStory() {
        return isCreateStory;
    }

    public String getUsername() {
        return username;
    }

    public Integer getAvatarResId() {
        return avatarResId;
    }

    public Integer getImageResId() {
        return imageResId;
    }

    public boolean isUnseen() {
        return isUnseen;
    }
}