package vn_hcmute.Real_Time_Chat_Final.model;

import java.util.List;

public class ConversationDTO {
    private long id;
    private boolean group;
    private String name;
    private String createdAt;
    private List<ConversationMemberDTO> members;
    private List<MessageDTO> messages;

    // Constructors
    public ConversationDTO() {}

    public ConversationDTO(long id, boolean isGroup, String name, String createdAt,
                           List<ConversationMemberDTO> members, List<MessageDTO> messages) {
        this.id = id;
        this.group = isGroup;
        this.name = name;
        this.createdAt = createdAt;
        this.members = members;
        this.messages = messages;
    }

    // Getters & Setters
    public long getId() { return (int) id; }
    public void setId(long id) { this.id = id; }

    public boolean isGroup() { return group; }
    public void setGroup(boolean group) { group = group; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<ConversationMemberDTO> getMembers() { return members; }
    public void setMembers(List<ConversationMemberDTO> members) { this.members = members; }

    public List<MessageDTO> getMessages() { return messages; }
    public void setMessages(List<MessageDTO> messages) { this.messages = messages; }

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
