package vn_hcmute.Real_Time_Chat_Final.service.impl;

import org.hibernate.Hibernate;
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

    @Transactional(readOnly = true)
    public Optional<Friendship> findById(Long id) {
        return friendshipRepository.findById(id)
                .map(friendship -> {
                    // Tải trước senderId và receiverId để tránh LazyInitializationException
                    Hibernate.initialize(friendship.getSenderId());
                    Hibernate.initialize(friendship.getReceiverId());
                    return friendship;
                });
    }

    @Transactional
    public boolean deleteFriend(long userId, long friendId) {
        System.out.println("Kiểm tra quan hệ: userId=" + userId + ", friendId=" + friendId);
        boolean existsForward = friendshipRepository.existsBySenderIdIdAndReceiverIdIdAndStatus((int) userId, (int) friendId, "Accepted");
        boolean existsBackward = friendshipRepository.existsBySenderIdIdAndReceiverIdIdAndStatus((int) friendId, (int) userId, "Accepted");
        System.out.println("Forward exists: " + existsForward + ", Backward exists: " + existsBackward);

        if (!existsForward && !existsBackward) {
            return false;
        }

        if (existsForward) {
            friendshipRepository.deleteBySenderIdIdAndReceiverIdIdAndStatus((int) userId, (int) friendId, "Accepted");
            System.out.println("Xóa forward: userId=" + userId + ", friendId=" + friendId);
        }
        if (existsBackward) {
            friendshipRepository.deleteBySenderIdIdAndReceiverIdIdAndStatus((int) friendId, (int) userId, "Accepted");
            System.out.println("Xóa backward: userId=" + friendId + ", friendId=" + userId);
        }
        return true;
    }

    public Friendship updateFriendship(Friendship friendship) {
        return friendshipRepository.save(friendship);
    }

    @Transactional(readOnly = true)
    public List<User> getFriendList(long userId) {
        List<Friendship> sentFriendships = friendshipRepository.findBySenderIdIdAndStatus((int) userId, "Accepted");
        List<Friendship> receivedFriendships = friendshipRepository.findByReceiverIdIdAndStatus((int) userId, "Accepted");
        List<User> friends = new ArrayList<>();

        for (Friendship friendship : sentFriendships) {
            Hibernate.initialize(friendship.getReceiverId());
            friends.add(friendship.getReceiverId());
        }
        for (Friendship friendship : receivedFriendships) {
            Hibernate.initialize(friendship.getSenderId());
            friends.add(friendship.getSenderId());
        }
        return friends;
    }

}