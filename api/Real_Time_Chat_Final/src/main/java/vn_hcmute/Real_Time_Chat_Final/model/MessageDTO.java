package vn_hcmute.Real_Time_Chat_Final.model;



import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
public class MessageDTO {
    private int id;
    private ConversationDTO conversation;
    private UserDTO sender;
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy h:mm:ss a", locale = "vn")
    private Timestamp timestamp;

    // Constructors
    public MessageDTO() {}



    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }



    public UserDTO getSender() { return sender; }
    public void setSender(UserDTO sender) { this.sender = sender; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }



    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public MessageDTO(ConversationDTO conversation, UserDTO sender, String message, Timestamp timestamp) {
        this.timestamp = timestamp;
        this.message = message;
        this.sender = sender;
        this.conversation = conversation;
    }

    public ConversationDTO getConversation() {
        return conversation;
    }

    public void setConversation(ConversationDTO conversation) {
        this.conversation = conversation;
    }


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
