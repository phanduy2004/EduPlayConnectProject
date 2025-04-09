package com.myjob.real_time_chat_final.config;

import android.util.Log;

import com.google.gson.Gson;
import com.myjob.real_time_chat_final.model.GameRoom;

import java.util.ArrayList;
import java.util.List;

public class GameWebSocketService {
    private static final String TAG = "GameWebSocketService";
    private final WebSocketManager webSocketManager;
    private String userId;
    private List<GameWebSocketListener> listeners = new ArrayList<>();
    private Gson gson = new Gson();

    private static final String CREATE_ROOM_URL = "/app/game.createRoom";
    private static final String JOIN_ROOM_URL = "/app/game.joinRoom";
    private static final String LEAVE_ROOM_URL = "/app/game.leaveRoom";
    private static final String START_GAME_URL = "/app/game.startGame";
    private static final String TOGGLE_READY_URL = "/app/game.toggleReady";

    private static final String ROOM_UPDATES_TOPIC = "/topic/game.room.";
    private static final String GAME_START_TOPIC = "/topic/game.start.";
    private static final String ROOM_CREATED_TOPIC = "/topic/game.room.created";

    private static GameWebSocketService instance;

    private GameWebSocketService() {
        webSocketManager = WebSocketManager.getInstance();
    }

    public static GameWebSocketService getInstance() {
        if (instance == null) {
            instance = new GameWebSocketService();
        }
        return instance;
    }

    public void initialize(String userId) {
        this.userId = userId;
        webSocketManager.connect();
    }

    public void createRoom(String userId, String categoryId, String categoryName) {
        try {
            // Subscribe trước khi gửi yêu cầu tạo phòng
            subscribeToRoomCreated();

            // Gửi yêu cầu tạo phòng
            CreateRoomRequest request = new CreateRoomRequest(userId, categoryId, categoryName);
            String jsonMessage = gson.toJson(request);
            Log.d(TAG, "Sending create room request: " + jsonMessage);
            webSocketManager.sendRequest(jsonMessage, CREATE_ROOM_URL);
        } catch (Exception e) {
            Log.e(TAG, "Error creating room", e);
            notifyError("Error creating room: " + e.getMessage());
        }
    }

    private void subscribeToRoomCreated() {
        webSocketManager.subscribeToTopic(ROOM_CREATED_TOPIC, new WebSocketManager.MessageListener() {
            @Override
            public void onNewMessage(String message) {
                Log.d(TAG, "Received room created message: " + message);
                try {
                    GameRoom gameRoom = gson.fromJson(message, GameRoom.class);
                    if (String.valueOf(gameRoom.getHost().getId()).equals(userId)) {
                        notifyRoomUpdate(gameRoom);
                        subscribeToRoomUpdates(String.valueOf(gameRoom.getRoomId()));
                        subscribeToGameStart(String.valueOf(gameRoom.getRoomId()));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing room created message", e);
                    notifyError("Error parsing room created message: " + e.getMessage());
                }
            }
        });
    }

    public void joinRoom(String roomId) {
        try {
            subscribeToRoomUpdates(roomId);
            subscribeToGameStart(roomId);
            JoinRoomRequest request = new JoinRoomRequest(userId, roomId);
            String jsonMessage = gson.toJson(request);
            webSocketManager.sendRequest(jsonMessage, JOIN_ROOM_URL);
        } catch (Exception e) {
            Log.e(TAG, "Error joining room", e);
            notifyError("Error joining room: " + e.getMessage());
        }
    }

    public void leaveRoom(String roomId) {
        try {
            subscribeToRoomUpdates(roomId);
            LeaveRoomRequest request = new LeaveRoomRequest(userId, roomId);
            String jsonMessage = gson.toJson(request);
            webSocketManager.sendRequest(jsonMessage, LEAVE_ROOM_URL);

        } catch (Exception e) {
            Log.e(TAG, "Error leaving room", e);
            notifyError("Error leaving room: " + e.getMessage());
        }
    }

    public void startGame(String roomId) {
        try {
            StartGameRequest request = new StartGameRequest(roomId, userId);
            String jsonMessage = gson.toJson(request);
            webSocketManager.sendRequest(jsonMessage, START_GAME_URL);
        } catch (Exception e) {
            Log.e(TAG, "Error starting game", e);
            notifyError("Error starting game: " + e.getMessage());
        }
    }

    public void toggleReady(String roomId, boolean ready) {
        try {
            subscribeToRoomUpdates(roomId);
            ToggleReadyRequest request = new ToggleReadyRequest(userId, roomId, ready);
            String jsonMessage = gson.toJson(request);
            webSocketManager.sendRequest(jsonMessage, TOGGLE_READY_URL);
        } catch (Exception e) {
            Log.e(TAG, "Error toggling ready state", e);
            notifyError("Error updating ready state: " + e.getMessage());
        }
    }

        public void subscribeToRoomUpdates(String roomId) {
            webSocketManager.subscribeToTopic(ROOM_UPDATES_TOPIC + roomId, new WebSocketManager.MessageListener() {
                @Override
                public void onNewMessage(String message) {
                    Log.d(TAG, "Received room update for room " + roomId + ": " + message);
                    try {
                        GameRoom gameRoom = gson.fromJson(message, GameRoom.class);
                        Log.d(TAG, "Parsed GameRoom: " + gson.toJson(gameRoom));
                        notifyRoomUpdate(gameRoom);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing room update", e);
                        notifyError("Error parsing room update: " + e.getMessage());
                    }
                }
            });
        }

    private void subscribeToGameStart(String roomId) {
        webSocketManager.subscribeToTopic(GAME_START_TOPIC + roomId, new WebSocketManager.MessageListener() {
            @Override
            public void onNewMessage(String message) {
                notifyGameStart(message);
            }
        });
    }

    public void notifyRoomUpdate(GameRoom gameRoom) {
        for (GameWebSocketListener listener : listeners) {
            listener.onRoomUpdate(gameRoom);
        }
    }

    private void notifyGameStart(String gameData) {
        for (GameWebSocketListener listener : listeners) {
            listener.onGameStart(gameData);
        }
    }

    private void notifyError(String errorMessage) {
        for (GameWebSocketListener listener : listeners) {
            listener.onError(errorMessage);
        }
    }

    public void addListener(GameWebSocketListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(GameWebSocketListener listener) {
        listeners.remove(listener);
    }

    public void cleanup() {
        listeners.clear();
    }

    // Các lớp request
    private static class CreateRoomRequest {
        private String userId;
        private String categoryId;
        private String categoryName;

        public CreateRoomRequest(String userId, String categoryId, String categoryName) {
            this.userId = userId;
            this.categoryId = categoryId;
            this.categoryName = categoryName;
        }
    }

    private static class JoinRoomRequest {
        private String userId;
        private String roomId;

        public JoinRoomRequest(String userId, String roomId) {
            this.userId = userId;
            this.roomId = roomId;
        }
    }

    private static class LeaveRoomRequest {
        private String userId;
        private String roomId;

        public LeaveRoomRequest(String userId, String roomId) {
            this.userId = userId;
            this.roomId = roomId;
        }
    }

    private static class StartGameRequest {
        private String roomId;
        private String userId;

        public StartGameRequest(String roomId, String userId) {
            this.roomId = roomId;
            this.userId = userId;
        }
    }

    private static class ToggleReadyRequest {
        private String userId;
        private String roomId;
        private boolean ready;

        public ToggleReadyRequest(String userId, String roomId, boolean ready) {
            this.userId = userId;
            this.roomId = roomId;
            this.ready = ready;
        }
    }

    public interface GameWebSocketListener {
        void onRoomUpdate(GameRoom room);
        void onGameStart(String gameData);
        void onError(String error);
    }
}