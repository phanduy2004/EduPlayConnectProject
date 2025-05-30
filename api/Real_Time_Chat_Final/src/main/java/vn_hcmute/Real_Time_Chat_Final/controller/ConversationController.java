package vn_hcmute.Real_Time_Chat_Final.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn_hcmute.Real_Time_Chat_Final.entity.Conversation;
import vn_hcmute.Real_Time_Chat_Final.entity.ConversationMember;
import vn_hcmute.Real_Time_Chat_Final.model.ContactDTO;
import vn_hcmute.Real_Time_Chat_Final.service.IConversationService;
import vn_hcmute.Real_Time_Chat_Final.service.impl.ConversationService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;

    }
    @PostMapping("/uploadGroupAvatar")
    public ResponseEntity<String> uploadGroupAvatar(@RequestParam("avatar") MultipartFile file) {
        try {
            String uploadAvatarGroup = conversationService.uploadAvatarGroup(file);
            return ResponseEntity.ok(uploadAvatarGroup);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * API: Tạo cuộc trò chuyện mới (1-1)
     * URL: POST /api/conversations/{userId}/{friendId}
     */
    @PostMapping("/{userId}/{friendId}")
    public ResponseEntity<Conversation> createConversation(
            @PathVariable("userId") int userId,
            @PathVariable("friendId") int friendId) {
        System.out.println("Tạo cuộc trò chuyện: userId=" + userId + ", friendId=" + friendId);
        try {
            Conversation conversation = conversationService.createOrGetConversation(userId, friendId);
            System.out.println("Conversation response: id=" + conversation.getId());
            return ResponseEntity.ok(conversation);
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * API: Tạo cuộc trò chuyện nhóm
     * URL: POST /api/conversations
     */
    @PostMapping
    public ResponseEntity<Conversation> createGroupConversation(@RequestBody Conversation conversation) {
        try {
            System.out.println("Tạo cuộc trò chuyện: userId=" +  conversation.isGroup());

            Conversation createdConversation = conversationService.createConversation(
                    conversation.isGroup(),
                    conversation.getName(),
                    conversation.getAvatarUrl() // Truyền avatarUrl từ request
            );
            return ResponseEntity.ok(createdConversation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * API: Thêm thành viên vào cuộc trò chuyện nhóm
     * URL: POST /api/conversations/{conversationId}/members
     */
    @PostMapping("/{conversationId}/members")
    public ResponseEntity<Void> addMembersToConversation(
            @PathVariable("conversationId") Long conversationId,
            @RequestBody List<ConversationMember> members) {
        if (members == null || members.isEmpty()) {
            System.err.println("Lỗi thêm thành viên: Danh sách thành viên rỗng");
            return ResponseEntity.badRequest().build();
        }
        try {
            conversationService.addMembersToConversation(conversationId, members);
            System.out.println("Thêm thành viên thành công: conversationId=" + conversationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Lỗi thêm thành viên: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * API: Lấy thông tin cuộc trò chuyện theo ID
     * URL: GET /api/conversations/{conversationId}
     */
    @GetMapping("/{conversationId}")
    public ResponseEntity<?> getConversationById(@PathVariable Long conversationId) {
        Optional<Conversation> conversation = conversationService.getConversationById(conversationId);
        return conversation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * API: Lấy ID của cuộc trò chuyện, tạo mới nếu chưa tồn tại
     * URL: GET /api/conversations/getChatRoomId
     */
    @GetMapping("/getChatRoomId")
    public ResponseEntity<Long> getChatRoomId(
            @RequestParam Long conversationId,
            @RequestParam boolean createNewRoomIfNotExists) {
        Optional<Long> chatRoomId = conversationService.getChatRoomId(conversationId, createNewRoomIfNotExists);
        return chatRoomId.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * API: Lấy danh sách cuộc trò chuyện của người dùng
     * URL: GET /api/conversations/list/{userId}
     */
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<ConversationMember>> getListChat(@PathVariable(" phenotypesuserId") int userId) {
        try {
            List<ConversationMember> listChat = conversationService.findListChat(userId);
            return ResponseEntity.ok(listChat);
        } catch (Exception e) {
            System.err.println("Lỗi lấy danh sách cuộc trò chuyện: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/contacts")
    public ResponseEntity<List<ContactDTO>> getContacts(@RequestParam int user_id) {
        List<ContactDTO> contacts = conversationService.getContacts(user_id);
        return ResponseEntity.ok(contacts);
    }
}