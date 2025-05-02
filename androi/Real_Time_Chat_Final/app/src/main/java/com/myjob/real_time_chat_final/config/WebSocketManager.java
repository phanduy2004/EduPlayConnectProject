package com.myjob.real_time_chat_final.config;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    private static volatile WebSocketManager instance;
    private StompClient stompClient;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private boolean isConnected = false;
    private Map<String, Disposable> topicSubscriptions = new HashMap<>();
    private WebSocketManager() {
        initWebSocket();
    }

    public static WebSocketManager getInstance() {
        if (instance == null) {
            synchronized (WebSocketManager.class) {
                if (instance == null) {
                    instance = new WebSocketManager();
                }
            }
        }
        return instance;
    }

    private void initWebSocket() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://http://10.0.2.2:8686/ws/websocket");
        compositeDisposable.add(stompClient.lifecycle()
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            isConnected = true;
                            Log.d(TAG, "WebSocket Connected");
                            break;
                        case ERROR:
                            isConnected = false;
                            Log.e(TAG, "WebSocket Error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            isConnected = false;
                            Log.d(TAG, "WebSocket Closed");
                            break;
                    }
                }));
    }

    public void connect() {
        if (!isConnected) {
            stompClient.connect();
        }
    }

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
            compositeDisposable.clear();
            isConnected = false;
        }
    }

    public void sendMessage(String jsonMessage) {
        if (isConnected) {
            compositeDisposable.add(stompClient.send("/app/chat.sendMessage", jsonMessage)
                    .subscribe(() -> Log.d(TAG, "Message sent: " + jsonMessage),
                            throwable -> Log.e(TAG, "Error sending message", throwable)));
        } else {
            Log.e(TAG, "WebSocket is not connected. Cannot send message.");
        }
    }
    public void sendRequest(String jsonMessage, String url) {
        if (isConnected) {
            compositeDisposable.add(stompClient.send(url, jsonMessage)
                    .subscribe(() -> Log.d(TAG, "Message sent: " + jsonMessage),
                            throwable -> Log.e(TAG, "Error sending message", throwable)));
        } else {
            Log.e(TAG, "WebSocket is not connected. Cannot send message.");
        }
    }

    public void subscribeToMessages(int conversationId, MessageListener listener) { // ✅ Thêm conversationId vào tham số
        if (stompClient != null) {
            compositeDisposable.add(stompClient.topic("/topic/conversation/" + conversationId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(topicMessage -> {
                        Log.d(TAG, "Received: " + topicMessage.getPayload());
                        listener.onNewMessage(topicMessage.getPayload());
                    }, throwable -> Log.e(TAG, "Error in subscription", throwable)));
        }
    }
    public void subscribeToRequest( MessageListener listener) { // ✅ Thêm conversationId vào tham số
        if (stompClient != null) {
            compositeDisposable.add(stompClient.topic("/topic/friendRequests")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(topicMessage -> {
                        Log.d(TAG, "Received: " + topicMessage.getPayload());
                        listener.onNewMessage(topicMessage.getPayload());
                    }, throwable -> Log.e(TAG, "Error in subscription", throwable)));
        }
    }
    // Thêm phương thức mới cho việc đăng ký topic liên quan đến trò chơi
    public void subscribeToTopic(String topic, MessageListener listener) {
        if (stompClient != null) {
            compositeDisposable.add(stompClient.topic(topic)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(topicMessage -> {
                        Log.d(TAG, "Received from " + topic + ": " + topicMessage.getPayload());
                        listener.onNewMessage(topicMessage.getPayload());
                    }, throwable -> Log.e(TAG, "Error in subscription to " + topic, throwable)));
        }
    }
    public void unsubscribeFromTopic(String topic) {
        Disposable subscription = topicSubscriptions.get(topic);
        if (subscription != null && !subscription.isDisposed()) {
            Log.d(TAG, "Unsubscribing from topic: " + topic);
            subscription.dispose();
            topicSubscriptions.remove(topic);
        }
    }


    public interface MessageListener {
        void onNewMessage(String message);
    }
}