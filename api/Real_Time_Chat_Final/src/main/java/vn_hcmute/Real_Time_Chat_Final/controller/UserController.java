package vn_hcmute.Real_Time_Chat_Final.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn_hcmute.Real_Time_Chat_Final.entity.Conversation;
import vn_hcmute.Real_Time_Chat_Final.entity.ConversationMember;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import vn_hcmute.Real_Time_Chat_Final.service.IConversationService;
import vn_hcmute.Real_Time_Chat_Final.service.IUserService;
import vn_hcmute.Real_Time_Chat_Final.service.impl.UserServiceImpl;

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
    private UserServiceImpl userServiceImpl;

    @Autowired
    private IConversationService conversationService;
    @PostMapping("/{userId}/avatar")
    public ResponseEntity<User> uploadAvatar(@PathVariable Long userId, @RequestParam("avatar") MultipartFile file) {
        try {
            User updatedUser = userServiceImpl.updateAvatar(userId, file);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") int id, @RequestBody User user) {
        try {
            User updatedUser = userServiceImpl.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
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
    @PostMapping("/{userId}/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        // Lấy các tham số từ request body
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");

        // Kiểm tra các trường bắt buộc
        if (oldPassword == null || newPassword == null || confirmPassword == null) {
            response.put("status", "error");
            response.put("message", "Vui lòng nhập đầy đủ thông tin");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu có khớp không
        if (!newPassword.equals(confirmPassword)) {
            response.put("status", "error");
            response.put("message", "Mật khẩu mới và xác nhận mật khẩu không khớp");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Tìm user theo userId
            Optional<User> optionalUser = userService.findByUserId(userId);
            if (!optionalUser.isPresent()) {
                response.put("status", "error");
                response.put("message", "Không tìm thấy người dùng");
                return ResponseEntity.status(404).body(response);
            }

            User user = optionalUser.get();

            // Kiểm tra mật khẩu cũ (trong thực tế nên mã hóa mật khẩu)
            if (!oldPassword.equals(user.getPassword())) {
                response.put("status", "error");
                response.put("message", "Mật khẩu cũ không đúng");
                return ResponseEntity.status(401).body(response);
            }

            // Cập nhật mật khẩu mới
            user.setPassword(newPassword);
            userServiceImpl.updateUser(Math.toIntExact(userId), user);

            response.put("status", "success");
            response.put("message", "Thay đổi mật khẩu thành công");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");
        if(newPassword == null || confirmPassword == null) {
            response.put("status", "error");
            response.put("message", "Vui lòng nhập đầy đủ");
            return ResponseEntity.badRequest().body(response);
        }
        if(!newPassword.equals(confirmPassword)) {
            response.put("status", "error");
            response.put("message", "Mật khẩu mới và xác nhận mật khẩu không khớp");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            // Tìm user theo userId
            Optional<User> optionalUser = userService.findByUserId(userId);
            if (!optionalUser.isPresent()) {
                response.put("status", "error");
                response.put("message", "Không tìm thấy người dùng");
                return ResponseEntity.status(404).body(response);
            }

            User user = optionalUser.get();
            // Cập nhật mật khẩu mới
            user.setPassword(newPassword);
            userServiceImpl.updateUser(Math.toIntExact(userId), user);

            response.put("status", "success");
            response.put("message", "Thay đổi mật khẩu thành công");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi server: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }

    }


}

