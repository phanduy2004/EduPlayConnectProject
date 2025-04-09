package vn_hcmute.Real_Time_Chat_Final.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn_hcmute.Real_Time_Chat_Final.entity.User;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class FriendshipOTD {
    private int id;  // ID của quan hệ bạn bè
    private User senderId;  // Người dùng 1
    private String receiverName;  // Người dùng 2
    private String status;  // Trạng thái kết bạn ("Pending", "Accepted", "Rejected")

    public void setSenderId(User senderId) {
        this.senderId = senderId;
    }
}
