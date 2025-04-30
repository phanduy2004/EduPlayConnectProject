package vn_hcmute.Real_Time_Chat_Final.service.impl;


import vn_hcmute.Real_Time_Chat_Final.entity.Like;
import vn_hcmute.Real_Time_Chat_Final.entity.Post;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import vn_hcmute.Real_Time_Chat_Final.model.LikeNotificationDTO;
import vn_hcmute.Real_Time_Chat_Final.repository.LikeRepository;
import vn_hcmute.Real_Time_Chat_Final.repository.PostRepository;
import vn_hcmute.Real_Time_Chat_Final.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra xem người dùng đã like bài đăng chưa
        if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
            // Nếu đã like, thì bỏ like
            likeRepository.deleteByPostIdAndUserId(postId, userId);
        } else {
            // Nếu chưa like, thì thêm like
            Like like = Like.builder()
                    .post(post)
                    .user(user)
                    .build();
            likeRepository.save(like);

            // Gửi thông báo qua WebSocket
            LikeNotificationDTO notification = new LikeNotificationDTO();
            notification.setPostId(postId);
            notification.setUserId(userId);
            notification.setUsername(user.getUsername());
            notification.setCreatedAt(like.getCreatedAt());
            messagingTemplate.convertAndSend("/topic/likes", notification);
        }
    }
}