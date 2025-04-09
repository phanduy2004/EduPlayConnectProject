package vn_hcmute.Real_Time_Chat_Final.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
