package com.myjob.real_time_chat_final.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    private ProgressBar progressBar;
    private BottomNavigationView navBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        initViews();
        setupToolbar();
        setupBottomNavigation();
        setupListeners();
    }

    private void initViews() {
        etRoomCode = findViewById(R.id.etRoomCode);
        btnJoinRoom = findViewById(R.id.btnJoinRoom);
        progressBar = findViewById(R.id.progressBar);
        navBar = findViewById(R.id.navBar);

        progressBar.setVisibility(View.GONE);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tham gia phòng");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupBottomNavigation() {
        navBar.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_findRoom) {
                return true;
            } else if (itemId == R.id.nav_home) {
                Intent intent = new Intent(JoinRoomActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_userhome) {
                Intent intent = new Intent(JoinRoomActivity.this, UserActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_addFriend) {
                Intent intent = new Intent(JoinRoomActivity.this, FriendListActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_chatmessage) {
                Intent intent = new Intent(JoinRoomActivity.this, MessageListActivity.class);
                intent.putExtra("USER_ID", getIntent().getIntExtra("USER_ID", -1));
                startActivity(intent);
                return true;
            }
            return false;
        });

        navBar.setSelectedItemId(R.id.nav_findRoom);
    }

    private void setupListeners() {
        btnJoinRoom.setOnClickListener(v -> attemptToJoinRoom());
    }

    private void attemptToJoinRoom() {
        String roomCode = etRoomCode.getText().toString().trim();

        if (TextUtils.isEmpty(roomCode)) {
            etRoomCode.setError("Vui lòng nhập mã phòng");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnJoinRoom.setEnabled(false);

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
                        Toast.makeText(JoinRoomActivity.this, "Trò chơi đã bắt đầu", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (room.getPlayers().size() >= room.getMaxPlayers()) {
                        Toast.makeText(JoinRoomActivity.this, "Phòng đã đầy", Toast.LENGTH_LONG).show();
                        return;
                    }

                    joinRoom(roomCode, room);
                } else {
                    Toast.makeText(JoinRoomActivity.this, "Không tìm thấy phòng", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GameRoom> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnJoinRoom.setEnabled(true);
                Toast.makeText(JoinRoomActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
        finish();
    }
}