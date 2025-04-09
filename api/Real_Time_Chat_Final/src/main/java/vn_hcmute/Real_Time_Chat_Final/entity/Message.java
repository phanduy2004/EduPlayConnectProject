package vn_hcmute.Real_Time_Chat_Final.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    @JsonIgnore
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy h:mm:ss a", locale = "vn")
    private Timestamp timestamp;

    @Override
    public String toString() {
        return "Message{" +
                "conversation=" + conversation +
                ", id=" + id +
                ", sender=" + sender +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
