package com.myjob.real_time_chat_final.model;

import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {
    private Long id;
    private String name;
    private String image;
    private List<Question> questions;
    public Category() {}
    public Category(String name, String image) {
        this.name = name;
        this.image = image;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}

