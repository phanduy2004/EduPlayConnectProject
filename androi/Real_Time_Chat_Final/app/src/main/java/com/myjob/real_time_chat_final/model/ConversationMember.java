package com.myjob.real_time_chat_final.model;

import java.io.Serializable;

public class ConversationMember {
    private long id;
    private Conversation conversation;
    private User user;

    // Constructors
    public ConversationMember() {}

    public ConversationMember(long id, int conversationId, User user) {
        this.id = id;
        this.user = user;
    }

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Override
    public String toString() {
        return "ConversationMember{" +
                "id=" + id +
                ", conversationId=" + conversation +
                ", user=" + user +
                '}';
    }


}
