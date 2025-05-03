package vn_hcmute.Real_Time_Chat_Final.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn_hcmute.Real_Time_Chat_Final.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
