package com.myjob.real_time_chat_final.config;

import android.util.Log;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class QuizWebSocketService {
    private static final String TAG = "QuizWebSocketService";
    private static volatile QuizWebSocketService instance;
    private WebSocketManager webSocketManager;
    private final List<QuizWebSocketListener> listeners = new ArrayList<>();
    private String userId;
    private Gson gson = new Gson();
    private static final String SUBMIT_ANSWER_URL = "/app/game.submitAnswer";
    private static final String END_GAME_URL = "/app/game.end";
    private static final String RANKING_UPDATE_TOPIC = "/topic/game.ranking.";
    private static final String GAME_END_TOPIC = "/topic/game.end.";

    private QuizWebSocketService() {
        webSocketManager = WebSocketManager.getInstance();
    }

    public static QuizWebSocketService getInstance() {
        if (instance == null) {
            synchronized (QuizWebSocketService.class) {
                if (instance == null) {
                    instance = new QuizWebSocketService();
                }
            }
        }
        return instance;
    }

    public void initialize(String userId) {
        this.userId = userId;
        webSocketManager.connect();
        Log.d(TAG, "Initialized with userId: " + userId);
    }

    public void submitAnswer(String roomId, String questionId, String answer) {
        if (userId == null || roomId == null) {
            Log.e(TAG, "User ID or Room ID is null, cannot submit answer");
            notifyError("Invalid user or room ID");
            return;
        }
        AnswerRequest answerRequest = new AnswerRequest(roomId, userId, questionId, answer);
        String jsonMessage = gson.toJson(answerRequest);
        webSocketManager.sendRequest(jsonMessage, SUBMIT_ANSWER_URL);
        Log.d(TAG, "Submitted answer: " + jsonMessage);
    }

    public void endGame(String roomId, String userId) {
        if (userId == null || roomId == null) {
            Log.e(TAG, "User ID or Room ID is null, cannot end game");
            notifyError("Invalid user or room ID");
            return;
        }
        AnswerRequest endGameRequest = new AnswerRequest(roomId, userId, null, null);
        String jsonMessage = gson.toJson(endGameRequest);
        webSocketManager.sendRequest(jsonMessage, END_GAME_URL);
        Log.d(TAG, "Sent end game request: " + jsonMessage);
    }

    public void subscribeToRanking(String roomId) {
        if (roomId == null) {
            Log.e(TAG, "Room ID is null, cannot subscribe to ranking");
            notifyError("Invalid room ID");
            return;
        }
        String topic = RANKING_UPDATE_TOPIC + roomId;
        webSocketManager.subscribeToTopic(topic, message -> {
            Log.d(TAG, "Received ranking update for room " + roomId + ": " + message);
            notifyRankingUpdate(message);
        });
        Log.d(TAG, "Subscribed to ranking topic: " + topic);
    }

    public void subscribeToGameEnd(String roomId) {
        if (roomId == null) {
            Log.e(TAG, "Room ID is null, cannot subscribe to game end");
            notifyError("Invalid room ID");
            return;
        }
        String topic = GAME_END_TOPIC + roomId;
        webSocketManager.subscribeToTopic(topic, message -> {
            Log.d(TAG, "Received game end signal for room " + roomId + ": " + message);
            notifyGameEnd(roomId);
        });
        Log.d(TAG, "Subscribed to game end topic: " + topic);
    }

    public void unsubscribeFromRanking(String roomId) {
        if (roomId != null) {
            String topic = RANKING_UPDATE_TOPIC + roomId;
            webSocketManager.unsubscribeFromTopic(topic);
            Log.d(TAG, "Unsubscribed from ranking topic: " + topic);
        }
    }

    public void unsubscribeFromGameEnd(String roomId) {
        if (roomId != null) {
            String topic = GAME_END_TOPIC + roomId;
            webSocketManager.unsubscribeFromTopic(topic);
            Log.d(TAG, "Unsubscribed from game end topic: " + topic);
        }
    }

    public void addListener(QuizWebSocketListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            Log.d(TAG, "Listener added, total listeners: " + listeners.size());
        }
    }

    public void removeListener(QuizWebSocketListener listener) {
        listeners.remove(listener);
        Log.d(TAG, "Listener removed, total listeners: " + listeners.size());
    }

    private void notifyRankingUpdate(String rankingData) {
        for (QuizWebSocketListener listener : new ArrayList<>(listeners)) {
            listener.onRankingUpdate(rankingData);
        }
    }

    private void notifyGameEnd(String roomId) {
        for (QuizWebSocketListener listener : new ArrayList<>(listeners)) {
            listener.onGameEnd(roomId);
        }
    }

    private void notifyError(String error) {
        for (QuizWebSocketListener listener : new ArrayList<>(listeners)) {
            listener.onError(error);
        }
    }

    public static class AnswerRequest {
        private String roomId;
        private String userId;
        private String questionId;
        private String answer;

        public AnswerRequest(String roomId, String userId, String questionId, String answer) {
            this.roomId = roomId;
            this.userId = userId;
            this.questionId = questionId;
            this.answer = answer;
        }

        // Getters for logging
        public String getRoomId() {
            return roomId;
        }

        public String getUserId() {
            return userId;
        }

        public String getQuestionId() {
            return questionId;
        }
    }

    public interface QuizWebSocketListener {
        void onRankingUpdate(String rankingData);
        void onGameEnd(String roomId);
        void onError(String error);
    }
}