package vn_hcmute.Real_Time_Chat_Final.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn_hcmute.Real_Time_Chat_Final.entity.Conversation;
import vn_hcmute.Real_Time_Chat_Final.service.IConversationService;

import java.util.Optional;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final IConversationService conversationService;

    @Autowired
    public ConversationController(IConversationService conversationService) {
        this.conversationService = conversationService;
    }

    /**
     * ✅ API: Tạo cuộc trò chuyện mới
     * URL: POST /api/conversations
     */
    @PostMapping
    public ResponseEntity<Conversation> createConversation(
            @RequestParam boolean isGroup,
            @RequestParam String name) {
        Conversation conversation = conversationService.createConversation(isGroup, name);
        return ResponseEntity.ok(conversation);
    }

    /**
     * ✅ API: Lấy thông tin cuộc trò chuyện theo ID
     * URL: GET /api/conversations/{conversationId}
     */
    @GetMapping("/{conversationId}")
    public ResponseEntity<?> getConversationById(@PathVariable Long conversationId) {
        Optional<Conversation> conversation = conversationService.getConversationById(conversationId);
        return conversation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * ✅ API: Lấy ID của cuộc trò chuyện, tạo mới nếu chưa tồn tại
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
}
