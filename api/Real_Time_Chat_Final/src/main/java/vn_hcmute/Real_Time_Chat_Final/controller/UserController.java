package vn_hcmute.Real_Time_Chat_Final.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import vn_hcmute.Real_Time_Chat_Final.entity.Conversation;
import vn_hcmute.Real_Time_Chat_Final.entity.ConversationMember;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import vn_hcmute.Real_Time_Chat_Final.service.IConversationService;
import vn_hcmute.Real_Time_Chat_Final.service.IUserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IConversationService conversationService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        Optional<User> user = userService.findByUserId(userId);
        return ResponseEntity.ok(user);
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (username == null || password == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Vui lòng nhập đầy đủ thông tin");
            return ResponseEntity.badRequest().body(response);
        }

        // Tìm user theo username
        User user = userService.findByUsername(username).orElse(null);

        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Không tìm thấy tài khoản");
            return ResponseEntity.status(404).body(response);
        }

        // Kiểm tra mật khẩu (trong thực tế nên mã hóa mật khẩu)
        if (!password.equals(user.getPassword()) ) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Mật khẩu không đúng");
            return ResponseEntity.status(401).body(response);
        }
        if(!user.isActive()){
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Tài khoản chưa được kích hoạt");
            return ResponseEntity.status(401).body(response);
        }
        // Đăng nhập thành công, trả về userId
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Đăng nhập thành công");
        response.put("userId", user.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ API: Lấy thông tin danh sách các user mà người dùng nhắn tin
     */
    @GetMapping("/contacts")
    public ResponseEntity<List<ConversationMember>> getContacts(@RequestParam int user_id) {
        List<ConversationMember> contacts = conversationService.findListChat(user_id); // Lấy danh sách user
        return ResponseEntity.ok(contacts); // Trả về mảng JSON []
    }
    @MessageMapping("/user.addUser")
    @SendTo("/user/topic")
    public User addUser(@Payload User user) {
        userService.saveUser(user);
        return user;
    }
    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/public")
    public User disconnectUser(
            @Payload User user
    ) {
        userService.disconnect(user);
        return user;
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> findConnectedUsers() {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }
}

