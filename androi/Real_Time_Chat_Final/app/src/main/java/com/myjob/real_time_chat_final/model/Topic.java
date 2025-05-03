package com.myjob.real_time_chat_final.model;

import java.io.Serializable;
import java.util.List;

public class Topic  implements Serializable {

    private int id;

    private String name;
    private String description;

    public List<Video> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    List<Video> videoList;
    public Topic(List<Video> videoList, String description, String name, int id) {
        this.videoList = videoList;
        this.description = description;
        this.name = name;
        this.id = id;
    }


}

