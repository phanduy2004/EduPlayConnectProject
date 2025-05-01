    package vn_hcmute.Real_Time_Chat_Final.model;

    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.sql.Timestamp;

    @Getter
    @Setter
    @NoArgsConstructor
    public class CommentNotificationDTO {
        private Long id;                    // ID của bình luận
        private Long postId;                // ID của bài đăng
        private Long userId;                // ID của người dùng
        private String username;            // Tên người dùng
        private String content;             // Nội dung bình luận
        private String avatarUrl;           // URL avatar của người dùng
        private Timestamp createdAt;        // Thời gian tạo
        private Long parentCommentId;       // ID của bình luận cha (nếu là trả lời)
    }