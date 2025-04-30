package vn_hcmute.Real_Time_Chat_Final.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn_hcmute.Real_Time_Chat_Final.entity.Post.Privacy;
import vn_hcmute.Real_Time_Chat_Final.entity.PostMedia;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDTO {
    private Long id;                    // ID của bài đăng
    private Long userId;                // ID của người dùng tạo bài đăng
    private String username;            // Tên người dùng
    private String avatarUrl;           // URL ảnh đại diện của người dùng
    private String content;             // Nội dung bài đăng
    private List<String> imageUrl;      // Danh sách URL hình ảnh của bài đăng
    private String privacy;             // Quyền riêng tư (PUBLIC, FRIENDS, PRIVATE)
    private Date createdAt;             // Thời gian tạo bài đăng
    private int likeCount;              // Số lượt thích
    private List<CommentDTO> comments;  // Danh sách bình luận
    private double score;


}
