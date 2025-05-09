package com.myjob.real_time_chat_final.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.PlayerAdapter;
import com.myjob.real_time_chat_final.api.UserService;
import com.myjob.real_time_chat_final.config.GameWebSocketService;
import com.myjob.real_time_chat_final.model.GameRoom;
import com.myjob.real_time_chat_final.model.GameRoomPlayer;
import com.myjob.real_time_chat_final.model.User;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MultiplayerRoomActivity extends AppCompatActivity implements GameWebSocketService.GameWebSocketListener {

    private static final String TAG = "MultiplayerRoomActivity";

    private TextView tvRoomInfo;
    private TextView tvRoomCode;
    private TextView tvPlayerCount;
    private RecyclerView recyclerViewPlayers;
    private Button btnStartGame;
    private Button btnLeaveRoom;
    private Button btnToggleReady;
    private Button btnShareRoom;
    private User user;
    private GameWebSocketService gameWebSocketService;
    private String userId;
    private String roomId;
    private String hostId;
    private String categoryId;
    private String categoryName;
    private boolean isHost = false;
    private boolean isReady = false;
    private PlayerAdapter playerAdapter;
    private boolean isRoomCreated = false;
    private boolean isGameStarting = false; // Cờ để ngăn leaveRoom khi game bắt đầu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_room);

        initViews();
        initData();
        setupListeners();
    }

    private void initViews() {
        tvRoomInfo = findViewById(R.id.tvRoomInfo);
        tvRoomCode = findViewById(R.id.tvRoomCode);
        tvPlayerCount = findViewById(R.id.tvPlayerCount);
        recyclerViewPlayers = findViewById(R.id.recyclerViewPlayers);
        btnStartGame = findViewById(R.id.btnStartGame);
        btnLeaveRoom = findViewById(R.id.btnLeaveRoom);
        btnToggleReady = findViewById(R.id.btnToggleReady);
        btnShareRoom = findViewById(R.id.btnShareRoom);

        recyclerViewPlayers.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("CATEGORY_ID");
        categoryName = intent.getStringExtra("CATEGORY_NAME");
        roomId = intent.getStringExtra("ROOM_ID");
        hostId = intent.getStringExtra("HOST_ID");
        userId = intent.getStringExtra("USER_ID");
        isHost = intent.getBooleanExtra("IS_HOST", false);

        Log.d(TAG, "Intent extras: " + (intent.getExtras() != null ? intent.getExtras().toString() : "null"));
        Log.d(TAG, "CATEGORY_ID: " + categoryId);
        Log.d(TAG, "CATEGORY_NAME: " + categoryName);
        Log.d(TAG, "ROOM_ID: " + roomId);
        Log.d(TAG, "HOST_ID: " + hostId);
        Log.d(TAG, "USER_ID: " + userId);
        Log.d(TAG, "IS_HOST: " + isHost);

        if (userId == null || userId.trim().isEmpty()) {
            userId = String.valueOf(LoginActivity.userid);
            if (userId == null || userId.equals("null") || userId.trim().isEmpty()) {
                finish();
                return;
            }
        }

        gameWebSocketService = GameWebSocketService.getInstance();
        gameWebSocketService.initialize(userId);
        gameWebSocketService.addListener(this);
        playerAdapter = new PlayerAdapter(new ArrayList<>(), userId, hostId);
        recyclerViewPlayers.setAdapter(playerAdapter);

        getUserById(Long.parseLong(userId));

        updateUI();

        if (isHost && roomId == null) {
            tvRoomInfo.setText("Creating new room...");
            tvRoomCode.setVisibility(View.GONE);
            gameWebSocketService.createRoom(userId, categoryId, categoryName);
        } else if (roomId != null) {
            tvRoomCode.setText("Room Code: " + roomId);
            tvRoomCode.setVisibility(View.VISIBLE);
            tvRoomInfo.setText(isHost ? "Hosting room: " + categoryName : "Connecting to room: " + roomId);
            processRoomJoining();
        } else {
            Toast.makeText(this, "Error: Invalid room state", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateUI() {
        Log.d(TAG, "Updating UI - isHost: " + isHost + ", isReady: " + isReady);
        btnStartGame.setVisibility(isHost ? View.VISIBLE : View.GONE);
        btnStartGame.setEnabled(isHost);
        btnShareRoom.setVisibility(isHost ? View.VISIBLE : View.GONE);
        btnToggleReady.setVisibility(isHost ? View.GONE : View.VISIBLE);
        btnToggleReady.setText(isReady ? "Cancel Ready" : "Ready");
    }

    private void processRoomJoining() {
        if (roomId != null) {
            if (isHost) {
                Log.d(TAG, "Host subscribing to existing room: " + roomId);
                tvRoomInfo.setText("Hosting room: " + categoryName);
            } else {
                Log.d(TAG, "Joining existing room: " + roomId);
                gameWebSocketService.joinRoom(roomId);
                tvRoomInfo.setText("Joining room: " + roomId);
            }
            gameWebSocketService.subscribeToRoomUpdates(roomId);
            gameWebSocketService.subscribeToGameStart(roomId);
        } else {
            Toast.makeText(this, "Error: Cannot join room", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupListeners() {
        btnLeaveRoom.setOnClickListener(v -> leaveRoom());
        btnStartGame.setOnClickListener(v -> startGame());
        btnToggleReady.setOnClickListener(v -> toggleReady());
        btnShareRoom.setOnClickListener(v -> shareRoomCode());
    }

    private void getUserById(long userId) {
        UserService userService = RetrofitClient.getApiUserService();
        Call<User> call = userService.getUserById(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                if (user != null) {
                    Log.d(TAG, "User fetched successfully: " + user.getUsername());
                    if (!isHost || isRoomCreated) {
                        processRoomJoining();
                    }
                } else {
                    Toast.makeText(MultiplayerRoomActivity.this, "Couldn't get user information", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "User response body is null");
                    finish();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MultiplayerRoomActivity.this, "Error calling API to get user", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting user: " + t.getMessage());
                finish();
            }
        });
    }

    private void leaveRoom() {
        if (roomId != null) {
            gameWebSocketService.leaveRoom(roomId);
            Toast.makeText(this, "Left room", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "User " + userId + " leaving room " + roomId);
            finish();
        } else {
            Toast.makeText(this, "Cannot leave room as you are not connected", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void startGame() {
        if (!isHost) {
            Toast.makeText(this, "Only the admin can start the game", Toast.LENGTH_SHORT).show();
            return;
        }
        if (roomId != null) {
            gameWebSocketService.startGame(roomId);
            Log.d(TAG, "Starting game for room " + roomId);
        } else {
            Toast.makeText(this, "Cannot start game: no room created", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleReady() {
        if (roomId != null) {
            isReady = !isReady;
            gameWebSocketService.toggleReady(roomId, isReady);
            btnToggleReady.setText(isReady ? "Cancel Ready" : "Ready");
            Log.d(TAG, "Toggled ready for user " + userId + ": " + isReady);
        }
    }

    private void shareRoomCode() {
        if (roomId != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Join my quiz game! Room code: " + roomId);
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "Share room code"));
        } else {
            Toast.makeText(this, "Room code not available yet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoomUpdate(GameRoom room) {
        runOnUiThread(() -> {
            if (room.getPlayers() == null || room.getPlayers().isEmpty()) {
                if (isHost && user != null) {
                    List<GameRoomPlayer> players = new ArrayList<>();
                    GameRoomPlayer hostPlayer = new GameRoomPlayer();
                    hostPlayer.setUser(user);
                    hostPlayer.setGameRoom(room);
                    players.add(hostPlayer);
                    room.setPlayers(players);
                    Log.d(TAG, "Added host to players manually");
                }
            }

            roomId = String.valueOf(room.getRoomId());
            tvRoomInfo.setText("Room: " + (room.getCategory() != null ? room.getCategory().getName() : "Unknown"));
            tvRoomCode.setText("Room Code: " + room.getRoomId());
            tvRoomCode.setVisibility(View.VISIBLE);
            tvPlayerCount.setText("Users: " + room.getPlayers().size() + "/" + room.getMaxPlayers());

            hostId = String.valueOf(room.getHost().getId());
            List<GameRoomPlayer> players = room.getPlayers();
            Log.d(TAG, "Updating players: " + players.size());
            for (GameRoomPlayer player : players) {
                if (String.valueOf(player.getUser().getId()).equals(userId)) {
                    isReady = player.isReady();
                    Log.d(TAG, "Synced isReady for user " + userId + ": " + isReady);
                }
            }
            playerAdapter.updatePlayers(players, hostId);
            recyclerViewPlayers.getAdapter().notifyDataSetChanged();

            isHost = userId.equals(hostId);
            updateUI();

            if (!isRoomCreated && isHost) {
                Toast.makeText(this, "Room created: " + room.getRoomId(), Toast.LENGTH_SHORT).show();
                isRoomCreated = true;
            } else {
                Toast.makeText(this, "Room updated: " + players.size() + " players", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onGameStart(String gameData) {
        runOnUiThread(() -> {
            Log.d(TAG, "Game is starting with data: " + gameData);
            isGameStarting = true; // Đặt cờ để ngăn leaveRoom trong onDestroy
            Intent intent = new Intent(MultiplayerRoomActivity.this, QuizActivity.class);
            intent.putExtra("ROOM_ID", roomId);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("CATEGORY_ID", categoryId);
            intent.putExtra("CATEGORY_NAME", categoryName);
            intent.putExtra("HOST_ID", hostId);
            intent.putExtra("QUESTIONS", gameData);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "WebSocket error: " + error);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (roomId != null && !isGameStarting) {
            gameWebSocketService.leaveRoom(roomId);
            Log.d(TAG, "Leaving room " + roomId + " in onDestroy for user " + userId);
        } else {
            Log.d(TAG, "Not leaving room " + roomId + " because game is starting or roomId is null");
        }
        gameWebSocketService.removeListener(this);
        Log.d(TAG, "Activity destroyed, WebSocket listener removed");
    }
}