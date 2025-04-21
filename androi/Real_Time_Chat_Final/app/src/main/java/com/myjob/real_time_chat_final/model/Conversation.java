package com.myjob.real_time_chat_final.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Conversation {
    private long id;
    @SerializedName("group")
    private boolean group;
    private String name;
    private String createdAt;
    private List<ConversationMember> members;
    private List<Message> messages;

    // Constructors
    public Conversation() {}

    public Conversation(long id, boolean isGroup, String name, String createdAt,
                        List<ConversationMember> members, List<Message> messages) {
        this.id = id;
        this.group = isGroup;
        this.name = name;
        this.createdAt = createdAt;
        this.members = members;
        this.messages = messages;
    }

    // Getters & Setters
    public int getId() { return (int) id; }
    public void setId(long id) { this.id = id; }

    public boolean isGroup() { return group; }
    public void setGroup(boolean group) { group = group; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<ConversationMember> getMembers() { return members; }
    public void setMembers(List<ConversationMember> members) { this.members = members; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }

    @Override
    public String toString() {
        return "Conversation{" +
                "id=" + id +
                ", isGroup=" + group +
                ", name='" + name + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", members=" + members +
                ", messages=" + messages +
                '}';
    }
}
