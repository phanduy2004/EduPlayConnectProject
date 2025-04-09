package vn_hcmute.Real_Time_Chat_Final.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vn_hcmute.Real_Time_Chat_Final.entity.Friendship;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findBySenderIdIdAndStatus(int senderId, String status);

    // Tìm tất cả friendships mà user là người nhận và có trạng thái cụ thể
    List<Friendship> findByReceiverIdIdAndStatus(int receiverId, String status);

    // Kiểm tra tồn tại friendship giữa hai người dùng (user1 gửi cho user2)
    boolean existsBySenderIdIdAndReceiverIdIdAndStatus(int senderId, int receiverId, String status);

    // Tìm friendship giữa hai người dùng (cả hai chiều)

}
