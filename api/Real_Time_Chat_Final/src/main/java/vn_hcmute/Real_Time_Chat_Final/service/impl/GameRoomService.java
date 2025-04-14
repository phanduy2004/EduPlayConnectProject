package vn_hcmute.Real_Time_Chat_Final.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn_hcmute.Real_Time_Chat_Final.entity.*;
import vn_hcmute.Real_Time_Chat_Final.model.GameRoomDTO;
import vn_hcmute.Real_Time_Chat_Final.model.GameRoomPlayerDTO;
import vn_hcmute.Real_Time_Chat_Final.repository.GameRoomPlayerRepository;
import vn_hcmute.Real_Time_Chat_Final.repository.GameRoomRepository;
import vn_hcmute.Real_Time_Chat_Final.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GameRoomService {

    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Autowired
    private GameRoomPlayerRepository gameRoomPlayerRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public GameRoom createRoom(Category category, User host) {
        // Tạo GameRoom
        GameRoom room = new GameRoom();
        room.setCategory(category);
        room.setHost(host);
        room.setGameStarted(false);
        room.setMaxPlayers(4);
        room.setPlayers(new ArrayList<>()); // Đã khởi tạo trong entity, có thể bỏ dòng này
        // Lưu GameRoom để có roomId
        room = gameRoomRepository.save(room);

        // Tìm User (đảm bảo managed entity)
        User managedUser = userRepository.findById(host.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo GameRoomPlayer
        GameRoomPlayer player = new GameRoomPlayer();
        player.setUser(managedUser);
        player.setGameRoom(room);
        player.setReady(true);

        // Thêm player vào room
        room.addPlayer(player);

        // Lưu lại GameRoom (quan trọng để cập nhật quan hệ)
        gameRoomPlayerRepository.save(player);

        log.info("Player after adding: {}", room.getPlayers().toString());
        return room;
    }

    public List<GameRoom> getAllRooms() {
        return gameRoomRepository.findAll();
    }

    @Transactional
    public GameRoom startGame(String roomId) {
        GameRoom room = gameRoomRepository.findById(Long.parseLong(roomId))
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.isGameStarted()) {
            throw new RuntimeException("Game already started");
        }

        // Tùy chọn: Kiểm tra tất cả người chơi đã sẵn sàng
        boolean allReady = room.getPlayers().stream().allMatch(GameRoomPlayer::isReady);
        if (!allReady) {
            throw new RuntimeException("Not all players are ready");
        }

        room.setGameStarted(true);
        return gameRoomRepository.save(room);
    }

    public List<Question> getQuestions(String roomId) {
        GameRoom room = gameRoomRepository.findById(Long.parseLong(roomId))
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return room.getCategory() != null ? room.getCategory().getQuestions() : Collections.emptyList();
    }

    @Transactional
    public GameRoom joinRoom(Long roomId, User user) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        GameRoom room = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

        Hibernate.initialize(room.getPlayers());
        long playerCount = gameRoomPlayerRepository.countByGameRoom(room);
        if (playerCount >= room.getMaxPlayers()) {
            throw new RuntimeException("Phòng đã đầy");
        }

        boolean alreadyInRoom = gameRoomPlayerRepository.existsByGameRoomAndUser(room, user);
        if (!alreadyInRoom) {
            GameRoomPlayer player = new GameRoomPlayer();
            player.setUser(managedUser);
            player.setGameRoom(room);
            if (room.getPlayers() == null) {
                room.setPlayers(new ArrayList<>());
            }
            room.addPlayer(player);
            log.info("Player before sending: {}", room.getPlayers().toString());
            gameRoomPlayerRepository.save(player);
        } else {
            // Nếu người dùng đã trong phòng, không cần thêm lại từ repository
            log.info("User {} already in room {}", user.getId(), roomId);
        }

        return room;
    }

    @Transactional
    public GameRoom leaveRoom(Long roomId, Long userId) {
        GameRoom room = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        GameRoomPlayer player = gameRoomPlayerRepository.findByGameRoom_RoomIdAndUser_Id(roomId, userId);
        if (player != null) {
            gameRoomPlayerRepository.delete(player); // Chỉ cần gọi delete là đủ
            room.getPlayers().remove(player); // <- nên thêm dòng này để Hibernate sync bộ nhớ
        }

        // Khởi tạo User nếu cần gửi lên client
        for (GameRoomPlayer p : room.getPlayers()) {
            Hibernate.initialize(p.getUser());
        }

        return room;
    }
    @Transactional
    public GameRoom endGame(Long roomId) {
        Optional<GameRoom> roomOpt = gameRoomRepository.findById(roomId);
        if (roomOpt.isPresent()) {
            GameRoom room = roomOpt.get();
            room.setGameStarted(false);
            // Tải players để tránh LazyInitializationException
            Hibernate.initialize(room.getPlayers());
            gameRoomRepository.save(room);
            log.info("Game ended for room {}, players: {}", roomId, room.getPlayers());
            return room;
        }
        log.warn("Room {} not found for endGame", roomId);
        return null;
    }
    public GameRoom getRoom(Long roomId) {
        return gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    @Transactional
    public GameRoom toggleReady(Long roomId, Long userId, boolean ready) {
        GameRoom room = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

        GameRoomPlayer player = gameRoomPlayerRepository.findByGameRoom_RoomIdAndUser_Id(roomId, userId);
        if (player == null) {
            throw new RuntimeException("Người chơi không có trong phòng");
        }

        player.setReady(ready);
        gameRoomPlayerRepository.save(player);

        // Khởi tạo players trước khi session đóng
        Hibernate.initialize(room.getPlayers());
        // Đảm bảo dữ liệu User trong mỗi GameRoomPlayer cũng được khởi tạo
        for (GameRoomPlayer p : room.getPlayers()) {
            Hibernate.initialize(p.getUser());
        }

        return room;
    }
    @Transactional
    public GameRoom submitAnswer(Long roomId, String userId, Long questionId, String answer) {
        GameRoom room = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Long parsedUserId;
        try {
            parsedUserId = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            log.error("Invalid userId format: {}", userId);
            return null;
        }

        log.info("Searching for user {} in room {}. Players: {}",
                parsedUserId, roomId,
                room.getPlayers().stream()
                        .map(p -> p.getUser() != null ? p.getUser().getId() : "null")
                        .collect(Collectors.toList()));

        GameRoomPlayer player = room.getPlayers().stream()
                .filter(p -> p.getUser() != null && p.getUser().getId() == parsedUserId)
                .findFirst()
                .orElse(null);

        if (player == null) {
            log.warn("Player with userId {} not found in room {}", parsedUserId, roomId);
            return null;
        }

        Question question = room.getCategory().getQuestions().stream()
                .filter(q -> q.getId() == questionId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Question not found"));

        if (answer != null && !answer.isEmpty() && answer.equals(question.getCorrectAnswer())) {
            player.setScore(player.getScore() + 1);
            gameRoomPlayerRepository.save(player);
            log.info("User {} answered correctly in room {}. New score: {}",
                    parsedUserId, roomId, player.getScore());
        } else {
            log.info("User {} answered incorrectly or timed out in room {}", parsedUserId, roomId);
        }

        return gameRoomRepository.save(room);
    }
    public String generateGameData(GameRoom room) {
        return "{"
                + "\"roomId\":" + room.getRoomId() + ","
                + "\"categoryId\":\"" + room.getCategory().getId() + "\","
                + "\"categoryName\":\"" + room.getCategory().getName() + "\","
                + "\"questions\":[{\"id\":1,\"text\":\"Sample Question 1\",\"options\":[\"A\",\"B\",\"C\",\"D\"],\"answer\":\"A\"},"
                + "{\"id\":2,\"text\":\"Sample Question 2\",\"options\":[\"A\",\"B\",\"C\",\"D\"],\"answer\":\"B\"}]"
                + "}";
    }

}
