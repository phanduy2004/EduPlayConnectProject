package com.myjob.real_time_chat_final.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.api.GameRoomService;
import com.myjob.real_time_chat_final.model.GameRoom;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinRoomActivity extends AppCompatActivity {

    private EditText etRoomCode;
    private Button btnJoinRoom;
    private Button btnBack;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etRoomCode = findViewById(R.id.etRoomCode);
        btnJoinRoom = findViewById(R.id.btnJoinRoom);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);
    }

    private void setupListeners() {
        btnJoinRoom.setOnClickListener(v -> attemptToJoinRoom());
        btnBack.setOnClickListener(v -> finish());
    }

    private void attemptToJoinRoom() {
        String roomCode = etRoomCode.getText().toString().trim();

        if (TextUtils.isEmpty(roomCode)) {
            etRoomCode.setError("Please enter a room code");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnJoinRoom.setEnabled(false);

        // First check if room exists
        checkRoomExists(roomCode);
    }

    private void checkRoomExists(String roomCode) {
        GameRoomService gameRoomService = RetrofitClient.getApiGameRoomService();
        Call<GameRoom> call = gameRoomService.getRoomDetails(Long.valueOf(roomCode));

        call.enqueue(new Callback<GameRoom>() {
            @Override
            public void onResponse(Call<GameRoom> call, Response<GameRoom> response) {
                progressBar.setVisibility(View.GONE);
                btnJoinRoom.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    GameRoom room = response.body();

                    if (room.isGameStarted()) {
                        Toast.makeText(JoinRoomActivity.this, "This game has already started", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (room.getPlayers().size() >= room.getMaxPlayers()) {
                        Toast.makeText(JoinRoomActivity.this, "This room is full", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Room exists and has space - join it
                    joinRoom(roomCode, room);
                } else {
                    Toast.makeText(JoinRoomActivity.this, "Room not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GameRoom> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnJoinRoom.setEnabled(true);
                Toast.makeText(JoinRoomActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void joinRoom(String roomCode, GameRoom room) {
        Intent intent = new Intent(JoinRoomActivity.this, MultiplayerRoomActivity.class);
        intent.putExtra("ROOM_ID", roomCode);
        intent.putExtra("HOST_ID", String.valueOf(room.getHost().getId()));
        intent.putExtra("CATEGORY_ID", String.valueOf(room.getCategory().getId()));
        intent.putExtra("CATEGORY_NAME", room.getCategory().getName());
        startActivity(intent);
        finish(); // Close this screen
    }
}