package com.myjob.real_time_chat_final.model;

import java.io.Serializable;

public class Video implements Serializable {

    private int id;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    private String title;

    private String background;
    public Video(String background, String title, String url, int id) {
        this.background = background;
        this.title = title;
        this.url = url;
        this.id = id;
    }




}
