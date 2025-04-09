package vn_hcmute.Real_Time_Chat_Final.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import vn_hcmute.Real_Time_Chat_Final.entity.GameRoom;
import vn_hcmute.Real_Time_Chat_Final.entity.GameRoomPlayer;

import java.util.ArrayList;
import java.util.List;

public class GameRoomDTO {
    private Long roomId;
    private Long categoryId;
    private Long hostId;
    private List<GameRoomPlayerDTO> gameRoomPlayers;
    private boolean gameStarted;
    private int maxPlayers;

    public GameRoomDTO(GameRoom room) {
        this.roomId = room.getRoomId();
        this.categoryId = room.getCategory().getId();
        this.gameRoomPlayers = new ArrayList<>();
        this.hostId = room.getHost().getId();// Assumes User has getUsername()
        this.gameStarted = room.isGameStarted();
        this.maxPlayers = room.getMaxPlayers();
    }
    public GameRoomDTO() {
    }

    public List<GameRoomPlayerDTO> getGameRoomPlayers() {
        return gameRoomPlayers;
    }

    public void setGameRoomPlayers(List<GameRoomPlayerDTO> gameRoomPlayers) {
        this.gameRoomPlayers = gameRoomPlayers;
    }

    // Getters and setters
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Long getHostId() { return hostId; }
    public void setHostId(Long hostId) { this.hostId = hostId; }
    public boolean isGameStarted() { return gameStarted; }
    public void setGameStarted(boolean gameStarted) { this.gameStarted = gameStarted; }
    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
    @Override
    public String toString() {
        return "GameRoomDTO{" +
                "roomId=" + roomId +
                ", categoryId=" + categoryId +
                ", hostId=" + hostId +
                ", gameStarted=" + gameStarted +
                ", maxPlayers=" + maxPlayers +
                ", players=" + gameRoomPlayers +
                '}';
    }
}