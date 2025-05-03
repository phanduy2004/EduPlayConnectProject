package vn_hcmute.Real_Time_Chat_Final.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long userId; // ID của người nhận thông báo
    private Long actorId; // ID của người thực hiện hành động
    private Long postId; // ID của bài viết liên quan
    private String type; // LIKE, COMMENT, REPLY
    private String content;
    private String userAvatarUrl; // Avatar của actor
    private boolean isRead;
    private Timestamp createdAt;


}