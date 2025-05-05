package vn_hcmute.Real_Time_Chat_Final.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import vn_hcmute.Real_Time_Chat_Final.entity.Conversation;
import vn_hcmute.Real_Time_Chat_Final.entity.Message;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import vn_hcmute.Real_Time_Chat_Final.model.MessageDTO;
import vn_hcmute.Real_Time_Chat_Final.repository.ConversationRepository;
import vn_hcmute.Real_Time_Chat_Final.repository.UserRepository;
import vn_hcmute.Real_Time_Chat_Final.service.impl.ChatMessageService;
import vn_hcmute.Real_Time_Chat_Final.config.ChatNotification;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/message")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    private final ChatMessageService chatMessageService;
    @Autowired
    private final ConversationRepository conversationRepository;
    @Autowired
    UserRepository userRepository;
    /**
     * ✅ Xử lý tin nhắn gửi qua WebSocket
     */
    @MessageMapping("/chat.sendMessage")
    public void processMessage(@Payload MessageDTO message) {
        System.out.println("📥 Received message: " + message.getSender().getId());
// ✅ Lấy Conversation từ ID
        if (message.getConversation() == null || message.getConversation().getId() == 0) {
            throw new RuntimeException("Conversation is missing in the message payload.");
        }

        // Truy vấn conversation từ database
        Conversation conversation = conversationRepository.findById(message.getConversation().getId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        // Truy vấn sender từ database
        User sender = userRepository.findById((long) message.getSender().getId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        // Tạo message mới
        Message newMessage = new Message();
        newMessage.setConversation(conversation);
        newMessage.setSender(sender);
        newMessage.setMessage(message.getMessage());
        newMessage.setTimestamp(message.getTimestamp());


        // Lưu vào database
        chatMessageService.save(newMessage);

        // 4️⃣ Gửi tin nhắn đến topic
        Long conversationId = newMessage.getConversation().getId();
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId,
                newMessage // Gửi cả object `Message`
        );

    }






    /**
     * ✅ API lấy danh sách tin nhắn của 1 người dùng trong đoạn hội thoại
     *
     */
    @GetMapping("/{conversationId}/{senderId}")
    public ResponseEntity<List<Message>> findChatMessages(@PathVariable int senderId,
                                                          @PathVariable int conversationId) {
        return ResponseEntity.ok(chatMessageService.findChatMessagesBySender(conversationId,senderId));
    }

    /**
     * ✅ API: Lấy thông tin cuộc trò chuyện với conversationId
     */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Page<Message>> getMessagesByConversationId(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            int convId = Integer.parseInt(conversationId.trim());
            Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
            Page<Message> messages = chatMessageService.findChatMessagesByConversationId(convId, pageable);
            return ResponseEntity.ok(messages);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
