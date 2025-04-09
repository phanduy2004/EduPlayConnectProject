package com.myjob.real_time_chat_final.model;

import androidx.annotation.NonNull;

import java.sql.Timestamp;
public class Message {
    private int id;
    private Conversation conversation;
    private User sender;
    private String message;
    private Timestamp timestamp;

    // Constructors
    public Message() {}



    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }



    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }



    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Message(Conversation conversation,User sender,String message,Timestamp timestamp) {
        this.timestamp = timestamp;
        this.message = message;
        this.sender = sender;
        this.conversation = conversation;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    @NonNull
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", conversationId=" + conversation +
                ", sender=" + sender +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
