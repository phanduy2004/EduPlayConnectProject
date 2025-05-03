package vn_hcmute.Real_Time_Chat_Final.repository;

import vn_hcmute.Real_Time_Chat_Final.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}