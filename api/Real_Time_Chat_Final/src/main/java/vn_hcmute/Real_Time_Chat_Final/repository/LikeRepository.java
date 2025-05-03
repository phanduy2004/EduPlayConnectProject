package vn_hcmute.Real_Time_Chat_Final.repository;

import vn_hcmute.Real_Time_Chat_Final.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    void deleteByPostIdAndUserId(Long postId, Long userId);
    Like findByPostIdAndUserId(Long postId, Long userId);
    long countByPostId(Long postId);
}