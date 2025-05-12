package vn_hcmute.Real_Time_Chat_Final.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import vn_hcmute.Real_Time_Chat_Final.entity.User;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FriendshipOTD {
    private Long id;
    private User senderId;
    private User receiverId;
    private String status;
    private String receiverName;

    @JsonFormat(pattern = "MMM dd, yyyy hh:mm:ss a", timezone = "Asia/Ho_Chi_Minh")
    private Timestamp createdAt;

    public FriendshipOTD() {

    }
}
