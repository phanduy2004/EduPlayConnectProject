package vn_hcmute.Real_Time_Chat_Final.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn_hcmute.Real_Time_Chat_Final.entity.Like;
import vn_hcmute.Real_Time_Chat_Final.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserId(Long userId, Pageable pageable);

}
