package vn_hcmute.Real_Time_Chat_Final.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn_hcmute.Real_Time_Chat_Final.entity.Friendship;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import vn_hcmute.Real_Time_Chat_Final.repository.FriendshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendshipService {
    @Autowired
    private FriendshipRepository friendshipRepository;

    public void addFriend(Friendship friendship) {
        friendshipRepository.save(friendship);
    }

    public Optional<Friendship> findById(Long id) {
        return friendshipRepository.findById(id);
    }

    @Transactional
    public boolean deleteFriend(int userId, int friendId) {
        System.out.println("Kiểm tra quan hệ: userId=" + userId + ", friendId=" + friendId);
        boolean existsForward = friendshipRepository.existsBySenderIdIdAndReceiverIdIdAndStatus(userId, friendId, "Accepted");
        boolean existsBackward = friendshipRepository.existsBySenderIdIdAndReceiverIdIdAndStatus(friendId, userId, "Accepted");
        System.out.println("Forward exists: " + existsForward + ", Backward exists: " + existsBackward);

        if (!existsForward && !existsBackward) {
            return false;
        }

        if (existsForward) {
            friendshipRepository.deleteBySenderIdIdAndReceiverIdIdAndStatus(userId, friendId, "Accepted");
            System.out.println("Xóa forward: userId=" + userId + ", friendId=" + friendId);
        }
        if (existsBackward) {
            friendshipRepository.deleteBySenderIdIdAndReceiverIdIdAndStatus(friendId, userId, "Accepted");
            System.out.println("Xóa backward: userId=" + friendId + ", friendId=" + userId);
        }
        return true;
    }
    public Friendship updateFriendship(Friendship friendship) {
        return friendshipRepository.save(friendship);
    }

    public List<User> getFriendList(int userId) {
        // Tìm tất cả các mối quan hệ mà người dùng là người gửi yêu cầu và được chấp nhận
        List<Friendship> sentFriendships = friendshipRepository.findBySenderIdIdAndStatus(userId, "Accepted");

        // Tìm tất cả các mối quan hệ mà người dùng là người nhận yêu cầu và đã chấp nhận
        List<Friendship> receivedFriendships = friendshipRepository.findByReceiverIdIdAndStatus(userId, "Accepted");

        List<User> friends = new ArrayList<>();

        // Thêm người nhận từ danh sách yêu cầu đã gửi (và được chấp nhận)
        for (Friendship friendship : sentFriendships) {
            friends.add(friendship.getReceiverId());
        }

        // Thêm người gửi từ danh sách yêu cầu đã nhận (và đã chấp nhận)
        for (Friendship friendship : receivedFriendships) {
            friends.add(friendship.getSenderId());
        }

        return friends;
    }
}
