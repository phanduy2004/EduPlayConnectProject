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
    private static final String RANKING_UPDATE_TOPIC = "/topic/game.ranking.";

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
        if (userId == null) {
            Log.e(TAG, "User ID is null, cannot submit answer");
            notifyError("User ID is not set");
            return;
        }
        AnswerRequest answerRequest = new AnswerRequest(roomId, userId, questionId, answer);
        String jsonMessage = gson.toJson(answerRequest);
        webSocketManager.sendRequest(jsonMessage, SUBMIT_ANSWER_URL);
        Log.d(TAG, "Submitted answer: " + jsonMessage);
    }

    public void subscribeToRanking(String roomId) {
        String topic = RANKING_UPDATE_TOPIC + roomId;
        webSocketManager.subscribeToTopic(topic, message -> {
            Log.d(TAG, "Received ranking update for room " + roomId + ": " + message);
            notifyRankingUpdate(message);
        });
    }

    public void unsubscribeFromRanking(String roomId) {
        String topic = RANKING_UPDATE_TOPIC + roomId;
        webSocketManager.unsubscribeFromTopic(topic);
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
        for (QuizWebSocketListener listener : listeners) {
            listener.onRankingUpdate(rankingData);
        }
    }

    private void notifyError(String error) {
        for (QuizWebSocketListener listener : listeners) {
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
    }

    public interface QuizWebSocketListener {
        void onRankingUpdate(String rankingData);
        void onError(String error);
    }
}