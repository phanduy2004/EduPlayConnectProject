package vn_hcmute.Real_Time_Chat_Final.model;

import lombok.Getter;
import vn_hcmute.Real_Time_Chat_Final.entity.Conversation;

@Getter
public class ConversationMemberDTO {
    // Getters & Setters
    private long id;
    private ConversationDTO conversationId;
    private UserDTO user;

    public ConversationDTO getConversationId() {
        return conversationId;
    }

    public void setConversationId(ConversationDTO conversationId) {
        this.conversationId = conversationId;
    }

    public long getId() {
        return id;
    }

    public UserDTO getUser() {
        return user;
    }

    // Constructors
    public ConversationMemberDTO() {}


    public void setId(long id) { this.id = id; }


    public void setUser(UserDTO user) { this.user = user; }

    @Override
    public String toString() {
        return "ConversationMember{" +
                "id=" + id +
                ", conversationId=" + conversationId +
                ", user=" + user +
                '}';
    }
}
