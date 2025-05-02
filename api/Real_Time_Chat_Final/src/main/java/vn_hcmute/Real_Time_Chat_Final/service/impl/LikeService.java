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
    public String likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        boolean isLiked = likeRepository.existsByPostIdAndUserId(postId, userId);
        LikeNotificationDTO notification = new LikeNotificationDTO();
        notification.setPostId(postId);
        notification.setUserId(userId);
        notification.setUsername(user.getUsername());

        if (isLiked) {
            likeRepository.deleteByPostIdAndUserId(postId, userId);
            notification.setLikeCount((int) likeRepository.countByPostId(postId));
            notification.setAction("UNLIKED");
            messagingTemplate.convertAndSend("/topic/likes", notification);
            return "UNLIKED";
        } else {
            Like like = Like.builder()
                    .post(post)
                    .user(user)
                    .build();
            likeRepository.save(like);
            notification.setLikeCount((int) likeRepository.countByPostId(postId));
            notification.setAction("LIKED");
            notification.setCreatedAt(like.getCreatedAt());
            messagingTemplate.convertAndSend("/topic/likes", notification);
            return "LIKED";
        }
    }
}