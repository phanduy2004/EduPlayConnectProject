package vn_hcmute.Real_Time_Chat_Final.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn_hcmute.Real_Time_Chat_Final.entity.Friendship;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import vn_hcmute.Real_Time_Chat_Final.model.FriendshipOTD;
import vn_hcmute.Real_Time_Chat_Final.repository.FriendshipRepository;
import vn_hcmute.Real_Time_Chat_Final.repository.UserRepository;
import vn_hcmute.Real_Time_Chat_Final.service.impl.FriendshipService;
import vn_hcmute.Real_Time_Chat_Final.service.impl.UserServiceImpl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@Configuration
public class FriendshipController {

    @Autowired
    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FriendshipService friendshipService;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private UserServiceImpl userServiceImpl;
    @GetMapping("/api/friends/{userId}")
    public ResponseEntity<List<User>> getFriendList(@PathVariable("userId") int userId) {
        List<User> friends = friendshipService.getFriendList(userId);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("api/friendRequests/received/{userId}")
    public ResponseEntity<List<Friendship>> getFriendRequestsReceived(@PathVariable("userId") int userId) {
        List<Friendship> friendRequests = friendshipRepository.findByReceiverIdIdAndStatus(userId, "Pending");
        return ResponseEntity.ok(friendRequests);
    }
    @DeleteMapping("api/friendship/{userId}/{friendId}")
    public ResponseEntity<Void> deleteFriendship(@PathVariable("userId") int userId, @PathVariable("friendId") int friendId) {
        System.out.println("Gọi xóa bạn: userId=" + userId + ", friendId=" + friendId);
        boolean deleted = friendshipService.deleteFriend(userId, friendId);
        if (deleted) {
            System.out.println("Xóa thành công: userId=" + userId + ", friendId=" + friendId);
            return ResponseEntity.ok().build();
        } else {
            System.out.println("Không tìm thấy quan hệ: userId=" + userId + ", friendId=" + friendId);
            return ResponseEntity.notFound().build();
        }
    }

    @MessageMapping("/sendFriendRequest")
    @SendTo("/topic/friendRequests")
    public void sendFriendRequest(FriendshipOTD request) {
        // Chuyển đổi từ FriendshipOTD sang Friendship entity
        Optional<User> optionalReceiver = userServiceImpl.findByUsername(request.getReceiverName());
        Optional<User> optionalSender = userServiceImpl.findById(request.getSenderId().getId());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Friendship friendship = new Friendship();
        friendship.setSenderId(optionalSender.get()); // Gán người gửi yêu cầu
        friendship.setReceiverId(optionalReceiver.get()); // Gán người nhận yêu cầu
        friendship.setStatus(request.getStatus()); // Trạng thái ban đầu là "Pending"
        friendship.setCreatedAt(timestamp);
        // Lưu quan hệ bạn bè vào cơ sở dữ liệu
        friendshipService.addFriend(friendship);

        // Gửi thông báo yêu cầu kết bạn
        messagingTemplate.convertAndSend("/topic/friendRequests", friendship);
    }
    @MessageMapping("/acceptFriendRequest")
    public void acceptFriendRequest(Friendship friendship) {
        // Tìm kiếm yêu cầu kết bạn trong cơ sở dữ liệu
        Optional<Friendship> optionalFriendship = friendshipService.findById((long) friendship.getId());

        if (optionalFriendship.isPresent()) {
            Friendship existingFriendship = optionalFriendship.get();
            existingFriendship.setStatus("Accepted");
            friendshipService.updateFriendship(existingFriendship);
            FriendshipOTD response = convertToFriendshipOTD(existingFriendship);
            messagingTemplate.convertAndSend("/topic/friendRequests/" + existingFriendship.getSenderId().getId(), response);
            messagingTemplate.convertAndSend("/topic/friendRequests/" + existingFriendship.getReceiverId().getId(), response);
        }
    }

    @MessageMapping("/rejectFriendRequest")
    public void rejectFriendRequest(Friendship friendship) {
        // Tìm kiếm yêu cầu kết bạn trong cơ sở dữ liệu
        Optional<Friendship> optionalFriendship = friendshipService.findById(friendship.getId());
        if (optionalFriendship.isPresent()) {
            Friendship existingFriendship = optionalFriendship.get();
            existingFriendship.setStatus("Rejected");
            friendshipService.updateFriendship(existingFriendship);
            FriendshipOTD response = convertToFriendshipOTD(existingFriendship);
            messagingTemplate.convertAndSend("/topic/friendRequests/" + existingFriendship.getSenderId().getId(), response);
            messagingTemplate.convertAndSend("/topic/friendRequests/" + existingFriendship.getReceiverId().getId(), response);
        }
    }

    private FriendshipOTD convertToFriendshipOTD(Friendship friendship) {
        FriendshipOTD otd = new FriendshipOTD();
        otd.setId(friendship.getId());
        otd.setSenderId(friendship.getSenderId());
        otd.setReceiverId(friendship.getReceiverId());
        otd.setStatus(friendship.getStatus());
        otd.setCreatedAt(friendship.getCreatedAt());
        otd.setReceiverName(friendship.getReceiverId().getUsername());

        return otd;
    }
}
