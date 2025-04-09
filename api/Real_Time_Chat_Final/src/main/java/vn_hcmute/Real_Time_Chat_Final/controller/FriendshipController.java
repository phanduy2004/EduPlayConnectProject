package vn_hcmute.Real_Time_Chat_Final.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn_hcmute.Real_Time_Chat_Final.entity.Friendship;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import vn_hcmute.Real_Time_Chat_Final.model.FriendshipOTD;
import vn_hcmute.Real_Time_Chat_Final.repository.FriendshipRepository;
import vn_hcmute.Real_Time_Chat_Final.service.impl.FriendshipService;
import vn_hcmute.Real_Time_Chat_Final.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@Configuration
public class FriendshipController {
    private final SimpMessagingTemplate messagingTemplate;
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

    @MessageMapping("/sendFriendRequest")
    @SendTo("/topic/friendRequests")
    public void sendFriendRequest(FriendshipOTD request) {
        // Chuyển đổi từ FriendshipOTD sang Friendship entity
        Optional<User> optionalReceiver = userServiceImpl.findByUsername(request.getReceiverName());
        Optional<User> optionalSender = userServiceImpl.findById(request.getSenderId().getId());

        Friendship friendship = new Friendship();
        friendship.setSenderId(optionalSender.get()); // Gán người gửi yêu cầu
        friendship.setReceiverId(optionalReceiver.get()); // Gán người nhận yêu cầu
        friendship.setStatus(request.getStatus()); // Trạng thái ban đầu là "Pending"

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
            existingFriendship.setStatus("Accepted"); // Cập nhật trạng thái

            // Lưu cập nhật vào cơ sở dữ liệu
            friendshipService.updateFriendship(existingFriendship);

            // Gửi thông báo cập nhật cho cả hai người dùng
            messagingTemplate.convertAndSend("/topic/friendRequests/" + existingFriendship.getSenderId().getId(), existingFriendship);
            messagingTemplate.convertAndSend("/topic/friendRequests/" + existingFriendship.getReceiverId().getId(), existingFriendship);
        }
    }

    @MessageMapping("/rejectFriendRequest")
    public void rejectFriendRequest(Friendship friendship) {
        // Tìm kiếm yêu cầu kết bạn trong cơ sở dữ liệu
        Optional<Friendship> optionalFriendship = friendshipService.findById((long) friendship.getId());

        if (optionalFriendship.isPresent()) {
            Friendship existingFriendship = optionalFriendship.get();
            existingFriendship.setStatus("Rejected"); // Cập nhật trạng thái

            // Lưu cập nhật vào cơ sở dữ liệu
            friendshipService.updateFriendship(existingFriendship);

            // Gửi thông báo cập nhật cho cả hai người dùng
            messagingTemplate.convertAndSend("/topic/friendRequests/" + existingFriendship.getSenderId().getId(), existingFriendship);
            messagingTemplate.convertAndSend("/topic/friendRequests/" + existingFriendship.getReceiverId().getId(), existingFriendship);
        }
    }
}
