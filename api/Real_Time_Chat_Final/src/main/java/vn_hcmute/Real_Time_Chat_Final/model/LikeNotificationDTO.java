package vn_hcmute.Real_Time_Chat_Final.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
@Getter
@Setter

public class LikeNotificationDTO {
    private Long postId;
    private Long userId;
    private String username;
    private Timestamp createdAt;

    public LikeNotificationDTO() {
    }
}
