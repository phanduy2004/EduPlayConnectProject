package vn_hcmute.Real_Time_Chat_Final.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import vn_hcmute.Real_Time_Chat_Final.entity.Category;
import vn_hcmute.Real_Time_Chat_Final.entity.GameRoom;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import vn_hcmute.Real_Time_Chat_Final.model.*;
import vn_hcmute.Real_Time_Chat_Final.service.impl.GameRoomService;

@Slf4j
@Controller
public class GameController {

    @Autowired
    private GameRoomService gameRoomService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game.createRoom")
    public void createRoom(CreateRoomRequest request) {
        User user = new User();
        user.setId(Long.parseLong(request.getUserId()));

        Category category = new Category();
        category.setId(Long.parseLong(request.getCategoryId()));
        category.setName(request.getCategoryName());

        GameRoom room = gameRoomService.createRoom(category, user);
        System.out.println("Room created with ID: " + room.getRoomId());

        // Gửi thông tin phòng qua topic chung
        messagingTemplate.convertAndSend("/topic/game.room.created", room);
        // Gửi cập nhật phòng qua topic cụ thể chỉ một lần khi tạo
        messagingTemplate.convertAndSend("/topic/game.room." + room.getRoomId(), room);
    }

    @MessageMapping("/game.joinRoom")
    public void joinRoom(@Payload JoinRoomRequest request) {
        log.info("joinRoom called with request: {}", request);
        User user = new User();
        user.setId(Long.parseLong(request.getUserId()));
        try {
            GameRoom room = gameRoomService.joinRoom(Long.parseLong(request.getRoomId()), user);
            log.info("RoomDTO before sending: {}", room.toString());
            messagingTemplate.convertAndSend("/topic/game.room." + room.getRoomId(), room);
            log.info("Message sent to /topic/game.room.{}", room.getRoomId());
        } catch (Exception e) {
            log.error("Error in joinRoom: ", e);
        }
    }

    @MessageMapping("/game.leaveRoom")
    public void leaveRoom(LeaveRoomRequest request) {
        GameRoomDTO room = gameRoomService.leaveRoom(Long.parseLong(request.getRoomId()),
                Long.parseLong(request.getUserId()));

        if (room != null) {
            // Gửi cập nhật phòng cho tất cả người dùng trong phòng
            messagingTemplate.convertAndSend("/topic/game.room." + room.getRoomId(), room);
        }
    }

    @MessageMapping("/game.startGame")
    public void startGame(StartGameRequest request) {
        GameRoom room = gameRoomService.getRoom(Long.parseLong(request.getRoomId()));

        // Kiểm tra xem người dùng có phải là chủ phòng không
        if (room != null && room.getHost().getId() == Long.parseLong(request.getUserId())) {
            // Tạo dữ liệu trò chơi (có thể là danh sách câu hỏi, v.v.)
            String gameData = gameRoomService.generateGameData(room);

            // Gửi thông báo bắt đầu trò chơi cho tất cả người chơi
            messagingTemplate.convertAndSend("/topic/game.start." + room.getRoomId(), gameData);

            // Cập nhật trạng thái phòng
            room.setGameStarted(true);
            messagingTemplate.convertAndSend("/topic/game.room." + room.getRoomId(), room);
        }
    }

    @MessageMapping("/game.toggleReady")
    public void toggleReady(ToggleReadyRequest request) {
        GameRoom room = gameRoomService.toggleReady(Long.parseLong(request.getRoomId()),
                Long.parseLong(request.getUserId()),
                request.isReady());

        if (room != null) {
            // Gửi cập nhật phòng cho tất cả người dùng trong phòng
            messagingTemplate.convertAndSend("/topic/game.room." + room.getRoomId(), room);
        }
    }
}