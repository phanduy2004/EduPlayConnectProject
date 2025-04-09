package com.myjob.real_time_chat_final.model;

import java.io.Serializable;
import java.util.List;
public class GameRoom implements Serializable {
    private Long roomId;
    private Category category;
    private User host;  // ✅ Đặt lại tên rõ nghĩa
    private List<GameRoomPlayer> players;
    private boolean gameStarted;
    private int maxPlayers;
    public GameRoom() {}

    public GameRoom(Long roomId,Category category, User host, boolean gameStarted, int maxPlayers) {
        this.roomId = roomId;
        this.category = category;
        this.host = host;
        this.gameStarted = gameStarted;
        this.maxPlayers = maxPlayers;
    }

    public Long getRoomId() {
        return roomId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public List<GameRoomPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<GameRoomPlayer> players) {
        this.players = players;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    @Override
    public String toString() {
        return "GameRoom{" +
                "roomId=" + roomId +
                ", category=" + category +
                ", host=" + host +
                ", players=" + players +
                ", gameStarted=" + gameStarted +
                ", maxPlayers=" + maxPlayers +
                '}';
    }
}
