package com.myjob.real_time_chat_final.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GameRoomPlayer implements Serializable {
    private Long id;
    private GameRoom gameRoom;
    private User user;
    @SerializedName("ready") // Tên trường trong JSON từ server
    private boolean isReady;  // Thêm thuộc tính isReady
    private long score;

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public GameRoomPlayer(Long id, GameRoom gameRoom, User user, boolean isReady, long score) {
        this.id = id;
        this.gameRoom = gameRoom;
        this.user = user;
        this.isReady = isReady;
        this.score = score;
    }

    public GameRoomPlayer() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GameRoom getGameRoom() {
        return gameRoom;
    }

    public void setGameRoom(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isReady() {  // Getter cho isReady
        return isReady;
    }

    public void setReady(boolean isReady) {  // Setter cho isReady
        this.isReady = isReady;
    }
}
