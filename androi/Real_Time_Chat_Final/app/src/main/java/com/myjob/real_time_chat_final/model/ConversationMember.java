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

    public ConversationMember(Conversation conversationId, User user) {
        this.conversation = conversationId;
        this.user = user;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversationId(Conversation conversationId) {
        this.conversation = conversationId;
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
