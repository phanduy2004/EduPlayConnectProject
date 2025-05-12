package com.myjob.real_time_chat_final.model;

import java.sql.Timestamp;

public class Friendship {
    private int id;  // ID của quan hệ bạn bè
    private User senderId;  // Người dùng 1
    private String receiverName;  // Người dùng 2
    private String status;  // Trạng thái kết bạn ("Pending", "Accepted", "Rejected")

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    private Timestamp createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getSenderId() {
        return senderId;
    }

    public void setSenderId(User senderId) {
        this.senderId = senderId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Friendship(User senderId, String receiverName, String status,Timestamp timestamp) {
        this.senderId = senderId;
        this.receiverName = receiverName;
        this.status = status;
        this.createdAt = timestamp;
    }
}
