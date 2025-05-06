package vn_hcmute.Real_Time_Chat_Final.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;                    // ID của bình luận
    private Long userId;// ID của người dùng tạo bình luận
    private Long postId;
    private String avatarUrl;
    private String username;            // Tên người dùng
    private String content;             // Nội dung bình luận
    private Date createdAt;             // Thời gian tạo bình luận
    private Long parentCommentId;       // ID của bình luận cha (nếu là trả lời)
    private List<CommentDTO> replies;
}
