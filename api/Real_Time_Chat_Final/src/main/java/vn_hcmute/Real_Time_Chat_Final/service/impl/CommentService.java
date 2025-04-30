package vn_hcmute.Real_Time_Chat_Final.service.impl;

import vn_hcmute.Real_Time_Chat_Final.entity.Comment;
import vn_hcmute.Real_Time_Chat_Final.entity.Post;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import vn_hcmute.Real_Time_Chat_Final.model.CommentDTO;
import vn_hcmute.Real_Time_Chat_Final.model.CommentNotificationDTO;
import vn_hcmute.Real_Time_Chat_Final.model.CommentRequestDTO;
import vn_hcmute.Real_Time_Chat_Final.repository.CommentRepository;
import vn_hcmute.Real_Time_Chat_Final.repository.PostRepository;
import vn_hcmute.Real_Time_Chat_Final.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    public CommentDTO createComment(CommentRequestDTO request) {
        // Tìm bài đăng
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Tìm người dùng
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tìm bình luận cha (nếu có)
        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        }

        // Tạo và lưu bình luận mới
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .parentComment(parentComment)
                .content(request.getContent())
                .build();
        comment = commentRepository.save(comment);

        // Gửi thông báo qua WebSocket
        CommentNotificationDTO notification = new CommentNotificationDTO();
        notification.setPostId(request.getPostId());
        notification.setUserId(request.getUserId());
        notification.setUsername(user.getUsername());
        notification.setContent(request.getContent());
        notification.setCreatedAt(comment.getCreatedAt());
        messagingTemplate.convertAndSend("/topic/comments", notification);

        // Ánh xạ Comment sang CommentDTO và trả về
        return mapToDTO(comment);
    }

    // Phương thức ánh xạ từ Comment sang CommentDTO
    private CommentDTO mapToDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setUserId(comment.getUser().getId());
        commentDTO.setPostId(comment.getPost().getId());
        commentDTO.setAvatarUrl(comment.getUser().getAvatarUrl()); // Lấy avatarUrl từ User
        commentDTO.setUsername(comment.getUser().getUsername());   // Lấy username từ User
        commentDTO.setContent(comment.getContent());
        commentDTO.setCreatedAt(comment.getCreatedAt());
        commentDTO.setParentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null);
        commentDTO.setReplies(new ArrayList<>()); // Khởi tạo danh sách replies rỗng

        return commentDTO;
    }
}