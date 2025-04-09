package vn_hcmute.Real_Time_Chat_Final.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Setter;

import java.sql.Timestamp;

public class MessageModel {
    private long id;
    private long sender_id;
    private long conversation_id;// ✅ Đổi từ int sender_id thành User sender
    @Setter
    private String message;
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy h:mm:ss a", locale = "en")
    private Timestamp timestamp;

    public MessageModel( int sender_id, int conversation_id, String message, Timestamp timestamp) {
        this.sender_id = sender_id;
        this.conversation_id = conversation_id;
        this.message = message;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public long getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(int conversation_id) {
        this.conversation_id = conversation_id;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

}
