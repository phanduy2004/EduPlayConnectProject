package vn_hcmute.Real_Time_Chat_Final.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn_hcmute.Real_Time_Chat_Final.entity.Category;
import vn_hcmute.Real_Time_Chat_Final.entity.GameRoom;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import vn_hcmute.Real_Time_Chat_Final.repository.UserRepository;
import vn_hcmute.Real_Time_Chat_Final.service.impl.GameRoomService;
import vn_hcmute.Real_Time_Chat_Final.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/rooms")
public class GameRoomRestController {

    @Autowired
    private GameRoomService gameRoomService;
    @Autowired
    private UserRepository userRepository;

    // Lấy da)nh sách các phòng đang hoạt động (chưa bắt đầu game
    @GetMapping
    public ResponseEntity<List<GameRoom>> getAvailableRooms() {
        List<GameRoom> availableRooms = gameRoomService.getAllRooms().stream()
                .filter(room -> !room.isGameStarted() && room.getPlayers().size() < room.getMaxPlayers())
                .collect(Collectors.toList());

        return ResponseEntity.ok(availableRooms);
    }
   @PostMapping("/create")
    public ResponseEntity<GameRoom> createRoom(@RequestBody GameRoom room) {
        // Đảm bảo rằng room không null và các thông tin cần thiết được cung cấp
        if (room == null || room.getHost() == null || room.getMaxPlayers() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        User newUser = userRepository.findById(room.getHost().getId()).orElse(null);
        Category newCategory = room.getCategory();
        // Tạo phòng mới
        GameRoom createdRoom = gameRoomService.createRoom(newCategory,newUser);

        // Trả về phòng mới đã tạo
        return ResponseEntity.ok(createdRoom);
    }
    // Lấy thông tin chi tiết về một phòng cụ thể
    @GetMapping("/{roomId}")
    public ResponseEntity<GameRoom> getRoomDetails(@PathVariable Long roomId) {
        GameRoom room = gameRoomService.getRoom(roomId);

        if (room != null) {
            return ResponseEntity.ok(room);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // API để kiểm tra xem phòng có tồn tại không (tiện cho việc hiển thị lỗi khi nhập mã phòng)
    @GetMapping("/exists/{roomId}")
    public ResponseEntity<Boolean> roomExists(@PathVariable Long roomId) {
        boolean exists = gameRoomService.getRoom(roomId) != null;
        return ResponseEntity.ok(exists);
    }
}