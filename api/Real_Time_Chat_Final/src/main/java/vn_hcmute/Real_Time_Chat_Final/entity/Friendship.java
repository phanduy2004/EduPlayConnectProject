package vn_hcmute.Real_Time_Chat_Final.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "friendships")  // Ensure this matches your table name
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;  // ID của quan hệ bạn bè

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", foreignKey = @ForeignKey(name = "FK_sender_id"))
    private User senderId;  //

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", foreignKey = @ForeignKey(name = "FK_receiver_id"))
    private User receiverId;  //

    @Column(name = "status")
    private String status;  // Trạng thái kết bạn ("Pending", "Accepted", "Rejected")

    // Default constructor

}
