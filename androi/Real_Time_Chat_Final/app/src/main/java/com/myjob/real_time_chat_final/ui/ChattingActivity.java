package com.myjob.real_time_chat_final.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private final Conversation conversation = new Conversation();;
    private final User user = new User();
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
            Log.e("ChattingActivity", "Lỗi: Thiếu dữ liệu truyền vào Intent!");
            finish(); // Kết thúc Activity nếu dữ liệu bị lỗi
        }

        user.setId(getIntent().getIntExtra("chat_sender_id", -1));
        conversation.setId(getIntent().getIntExtra("conversation_id", -1));
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
    private void setupTaskbarName(String user){
        // Ánh xạ view
        chatToolbar = findViewById(R.id.chatToolbar);
        recyclerView = findViewById(R.id.recyclerView);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);

        // Thiết lập Toolbar
        setSupportActionBar(chatToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Lấy dữ liệu từ Intent (truyền từ danh sách chat)

        if (user != null) {
            getSupportActionBar().setTitle(user);
        }

        // Bắt sự kiện click nút back trên Toolbar
        chatToolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupWebSocketListener() {
        webSocketManager.subscribeToMessages(conversation.getId(), message -> { // ✅ Truyền conversationId vào
            Gson gson = new Gson();
            Message chatMessage = gson.fromJson(message, Message.class);
            runOnUiThread(() -> {
                adapter.addMessage(chatMessage);
                recyclerView.scrollToPosition(messageList.size() - 1);
            });
        });
    }

    private void setupSendMessageListener() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = edtMessage.getText().toString().trim();
                if (!text.isEmpty()) {
                    Timestamp timestamp = convertToTimestamp(getCurrentTime());
                    Log.d("ChattingActivity", "📤 conversationId: " + conversation.getId());

                    Message newMessage = new Message(conversation,user, text, timestamp);

                    // Kiểm tra xem conversationId có đúng không
                    Log.d("ChattingActivity", "📤 Sending message: " + new Gson().toJson(newMessage));

                    webSocketManager.sendMessage(new Gson().toJson(newMessage));
                    edtMessage.setText("");
                }
            }
        });
    }

    private void loadMessages() {
        messageService.getMessagesByConversationId(conversation.getId()).enqueue(new Callback<List<Message>>() {
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
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.disconnect();
    }
}