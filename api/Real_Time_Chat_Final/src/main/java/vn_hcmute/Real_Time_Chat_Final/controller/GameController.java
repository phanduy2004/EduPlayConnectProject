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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class GameController {

    @Autowired
    private GameRoomService gameRoomService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final Map<Long, Map<Long, Integer>> roomAnswerCounts = new HashMap<>();

    @MessageMapping("/game.createRoom")
    public void createRoom(CreateRoomRequest request) {
        User user = new User();
        user.setId(Long.parseLong(request.getUserId()));

        Category category = new Category();
        category.setId(Long.parseLong(request.getCategoryId()));
        category.setName(request.getCategoryName());

        GameRoom room = gameRoomService.createRoom(category, user);
        log.info("Room created with ID: {}", room.getRoomId());

        messagingTemplate.convertAndSend("/topic/game.room.created", room);
        messagingTemplate.convertAndSend("/topic/game.room." + room.getRoomId(), room);
    }

    @MessageMapping("/game.joinRoom")
    public void joinRoom(@Payload JoinRoomRequest request) {
        log.info("joinRoom called with request: {}", request);
        User user = new User();
        user.setId(Long.parseLong(request.getUserId()));
        try {
            GameRoom room = gameRoomService.joinRoom(Long.parseLong(request.getRoomId()), user);
            log.info("Room updated: {}", room);
            messagingTemplate.convertAndSend("/topic/game.room." + room.getRoomId(), room);
            log.info("Message sent to /topic/game.room.{}", room.getRoomId());
        } catch (Exception e) {
            log.error("Error in joinRoom: {}", e.getMessage());
        }
    }

    @MessageMapping("/game.leaveRoom")
    public void leaveRoom(LeaveRoomRequest request) {
        log.info("leaveRoom called with request: {}", request);
        GameRoom room = gameRoomService.leaveRoom(Long.parseLong(request.getRoomId()),
                Long.parseLong(request.getUserId()));
        if (room != null) {
            messagingTemplate.convertAndSend("/topic/game.room." + room.getRoomId(), room);
            log.info("Room updated after leave: {}", room);
            // End game if no online players remain
            if (room.getPlayers().stream().noneMatch(p -> p.getUser().isStatus())) {
                room = gameRoomService.endGame(room.getRoomId());
                messagingTemplate.convertAndSend("/topic/game.ranking." + room.getRoomId(), room.getPlayers());
                messagingTemplate.convertAndSend("/topic/game.end." + room.getRoomId(), room.getRoomId());
                log.info("No online players left, ended game for room {}", room.getRoomId());
            }
        }
    }

    @MessageMapping("/game.startGame")
    public void startGame(StartGameRequest request) {
        log.info("startGame called with request: {}", request);
        GameRoom room = gameRoomService.startGame(request.getRoomId());
        if (room != null) {
            messagingTemplate.convertAndSend("/topic/game.room." + request.getRoomId(), room);
            messagingTemplate.convertAndSend("/topic/game.start." + request.getRoomId(), gameRoomService.getQuestions(request.getRoomId()));
            log.info("Game started for room {}", request.getRoomId());
        }
    }

    @MessageMapping("/game.toggleReady")
    public void toggleReady(ToggleReadyRequest request) {
        log.info("toggleReady called with request: {}", request);
        GameRoom room = gameRoomService.toggleReady(Long.parseLong(request.getRoomId()),
                Long.parseLong(request.getUserId()),
                request.isReady());
        if (room != null) {
            messagingTemplate.convertAndSend("/topic/game.room." + room.getRoomId(), room);
            log.info("Room updated after toggle ready: {}", room);
        }
    }

    @MessageMapping("/game.submitAnswer")
    public void submitAnswer(@Payload AnswerRequest request) {
        log.info("submitAnswer called with request: {}", request);
        try {
            if (request.getUserId() == null || request.getRoomId() == null || request.getQuestionId() == null) {
                log.error("Invalid request: userId={}, roomId={}, questionId={}",
                        request.getUserId(), request.getRoomId(), request.getQuestionId());
                messagingTemplate.convertAndSendToUser(
                        request.getUserId() != null ? request.getUserId() : "unknown",
                        "/queue/errors",
                        "Invalid submission data"
                );
                return;
            }
            GameRoom room = gameRoomService.submitAnswer(
                    Long.parseLong(request.getRoomId()),
                    request.getUserId(),
                    Long.parseLong(request.getQuestionId()),
                    request.getAnswer()
            );
            if (room != null) {
                // Track answer count
                Map<Long, Integer> answerCounts = roomAnswerCounts.computeIfAbsent(
                        Long.parseLong(request.getRoomId()), k -> new HashMap<>());
                Long userId = Long.parseLong(request.getUserId());
                answerCounts.put(userId, answerCounts.getOrDefault(userId, 0) + 1);
                log.info("User {} answered question in room {}. Total answers: {}",
                        userId, request.getRoomId(), answerCounts.get(userId));

                // Send ranking update
                messagingTemplate.convertAndSend("/topic/game.ranking." + request.getRoomId(), room.getPlayers());
                log.info("Ranking updated for room {}: {}", request.getRoomId(), room.getPlayers());

                // Check if all online players have answered all questions
                int totalQuestions = room.getCategory().getQuestions().size();
                boolean allCompleted = room.getPlayers().stream()
                        .filter(p -> p.getUser().isStatus()) // Only online players
                        .allMatch(p -> answerCounts.getOrDefault(p.getUser().getId(), 0) >= totalQuestions);
                if (allCompleted) {
                    room = gameRoomService.endGame(Long.parseLong(request.getRoomId()));
                    messagingTemplate.convertAndSend("/topic/game.room." + request.getRoomId(), room);
                    messagingTemplate.convertAndSend("/topic/game.ranking." + request.getRoomId(), room.getPlayers());
                    messagingTemplate.convertAndSend("/topic/game.end." + request.getRoomId(), request.getRoomId());
                    log.info("All online players completed, ended game for room {}", request.getRoomId());
                }
            } else {
                log.warn("No ranking update sent for room {}: player not found", request.getRoomId());
                messagingTemplate.convertAndSendToUser(
                        request.getUserId(),
                        "/queue/errors",
                        "Cannot submit answer: Player not found"
                );
            }
        } catch (Exception e) {
            log.error("Error in submitAnswer: {}", e.getMessage());
            messagingTemplate.convertAndSendToUser(
                    request.getUserId(),
                    "/queue/errors",
                    "Error submitting answer: " + e.getMessage()
            );
        }
    }

    @MessageMapping("/game.end")
    public void endGame(@Payload AnswerRequest request) {
        log.info("endGame called with request: {}", request);
        try {
            if (request.getRoomId() == null || request.getUserId() == null) {
                log.error("Invalid end game request: roomId={}, userId={}", request.getRoomId(), request.getUserId());
                messagingTemplate.convertAndSendToUser(
                        request.getUserId() != null ? request.getUserId() : "unknown",
                        "/queue/errors",
                        "Invalid end game request"
                );
                return;
            }
            Long roomId = Long.parseLong(request.getRoomId());
            GameRoom room = gameRoomService.endGame(roomId);
            if (room != null) {
                messagingTemplate.convertAndSend("/topic/game.room." + roomId, room);
                messagingTemplate.convertAndSend("/topic/game.ranking." + roomId, room.getPlayers());
                messagingTemplate.convertAndSend("/topic/game.end." + roomId, roomId.toString());
                log.info("Game ended for room {}: {}", roomId, room.getPlayers());
            } else {
                log.warn("Room {} not found for endGame", roomId);
            }
        } catch (Exception e) {
            log.error("Error in endGame: {}", e.getMessage());
            messagingTemplate.convertAndSendToUser(
                    request.getUserId(),
                    "/queue/errors",
                    "Error ending game: " + e.getMessage()
            );
        }
    }
}