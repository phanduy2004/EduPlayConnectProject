package vn_hcmute.Real_Time_Chat_Final.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
@Getter
@Setter
@NoArgsConstructor
public class CommentNotificationDTO {
    private Long postId;
    private Long userId;
    private String username;
    private String content;
    private String avatarUrl; // Thêm trường này
    private Timestamp createdAt;
}
