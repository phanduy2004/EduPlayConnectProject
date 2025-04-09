package vn_hcmute.Real_Time_Chat_Final.model;

public class GameRoomPlayerDTO {
    private Long gameRoomPlayerId; // ID của bản ghi trong bảng game_room_players
    private Long userId;           // ID người dùng
    private String username;       // Tên người dùng
    private boolean isReady;       // Trạng thái sẵn sàng

    // Constructor mặc định (bắt buộc cho Jackson)
    public GameRoomPlayerDTO() {
    }

    // Constructor đầy đủ
    public GameRoomPlayerDTO(Long gameRoomPlayerId, Long userId, String username, boolean isReady) {
        this.gameRoomPlayerId = gameRoomPlayerId;
        this.userId = userId;
        this.username = username;
        this.isReady = isReady;
    }

    // Getter và Setter
    public Long getGameRoomPlayerId() {
        return gameRoomPlayerId;
    }

    public void setGameRoomPlayerId(Long gameRoomPlayerId) {
        this.gameRoomPlayerId = gameRoomPlayerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}