package com.myjob.real_time_chat_final.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.MessageAdapter;
import com.myjob.real_time_chat_final.api.MessageService;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;
import com.myjob.real_time_chat_final.model.Conversation;
import com.myjob.real_time_chat_final.model.Message;
import com.myjob.real_time_chat_final.config.WebSocketManager;
import com.myjob.real_time_chat_final.model.User;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChattingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private MessageService messageService;
    private WebSocketManager webSocketManager;
    private Conversation conversation;
    private User user;
    private Toolbar chatToolbar;
    private EditText edtMessage;
    private ImageButton btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        int senderId = getIntent().getIntExtra("chat_sender_id", -1);
        int conversationId = getIntent().getIntExtra("conversation_id", -1);
        String userName = getIntent().getStringExtra("chat_user_name");

        if (senderId == -1 || conversationId == -1 || userName == null) {
            Log.e("ChattingActivity", "Thiếu dữ liệu Intent: senderId=" + senderId + ", conversationId=" + conversationId + ", userName=" + userName);
            Toast.makeText(this, "Lỗi: Không thể mở trò chuyện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        user = new User();
        user.setId(senderId);
        conversation = new Conversation();
        conversation.setId(conversationId);

        recyclerView = findViewById(R.id.recyclerView);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messageList, user.getId());
        recyclerView.setAdapter(adapter);

        messageService = RetrofitClient.getApiMessageService();
        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.connect();

        setupTaskbarName(userName);
        loadMessages();
        setupWebSocketListener();
        setupSendMessageListener();
    }

    private void setupTaskbarName(String userName) {
        chatToolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(chatToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(userName);
        chatToolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupWebSocketListener() {
        webSocketManager.subscribeToMessages((int) conversation.getId(), message -> {
            Gson gson = new Gson();
            Message chatMessage = gson.fromJson(message, Message.class);
            runOnUiThread(() -> {
                adapter.addMessage(chatMessage);
                recyclerView.scrollToPosition(messageList.size() - 1);
            });
        });
    }

    private void setupSendMessageListener() {
        btnSend.setOnClickListener(v -> {
            String text = edtMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                Timestamp timestamp = convertToTimestamp(getCurrentTime());
                Log.d("ChattingActivity", "Gửi tin nhắn, conversationId: " + conversation.getId());
                Message newMessage = new Message(conversation, user, text, timestamp);
                Log.d("ChattingActivity", "Tin nhắn: " + new Gson().toJson(newMessage));
                webSocketManager.sendMessage(new Gson().toJson(newMessage));
                edtMessage.setText("");
            }
        });
    }

    private void loadMessages() {
        messageService.getMessagesByConversationId((int) conversation.getId()).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageList.clear();
                    for (Message message : response.body()) {
                        Log.d("API_RESPONSE", "Message: " + new Gson().toJson(message));
                    }
                    messageList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.e("ChattingActivity", "Lỗi tải tin nhắn: " + t.getMessage());
            }
        });
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    private Timestamp convertToTimestamp(String timeString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date parsedDate = dateFormat.parse(timeString);
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            Log.e("ChattingActivity", "Lỗi parse timestamp: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}