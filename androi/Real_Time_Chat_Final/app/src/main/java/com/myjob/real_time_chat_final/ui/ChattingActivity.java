package com.myjob.real_time_chat_final.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.MessageAdapter;
import com.myjob.real_time_chat_final.api.MessageService;
import com.myjob.real_time_chat_final.modelDTO.PageResponse;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;
import com.myjob.real_time_chat_final.model.Conversation;
import com.myjob.real_time_chat_final.model.Message;
import com.myjob.real_time_chat_final.config.WebSocketManager;
import com.myjob.real_time_chat_final.model.User;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
    private TextView tvUserName;
    private EditText edtMessage;
    private ImageButton btnSend;
    private ImageButton btnAddMedia;
    private ImageButton btnEmoji;
    private ImageButton btnCall;
    private ImageButton btnVideoCall;
    private int currentPage = 0;
    private final int PAGE_SIZE = 20;
    private boolean isLoading = false;
    private LinearLayoutManager layoutManager;
    private boolean isLastPage = false;

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
        if (recyclerView == null) {
            Log.e("ChattingActivity", "RecyclerView is null. Check activity_chatting.xml");
            Toast.makeText(this, "Lỗi giao diện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView.setBackgroundColor(getResources().getColor(android.R.color.white));

        chatToolbar = findViewById(R.id.chatToolbar);
        tvUserName = findViewById(R.id.tvUserName);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        btnAddMedia = findViewById(R.id.btnAddMedia);
        btnEmoji = findViewById(R.id.btnEmoji);
        btnCall = findViewById(R.id.btnCall);
        btnVideoCall = findViewById(R.id.btnVideoCall);

        // Kiểm tra null sau khi ánh xạ
        if (chatToolbar == null) Log.e("ChattingActivity", "chatToolbar is null");
        if (tvUserName == null) Log.e("ChattingActivity", "tvUserName is null");
        if (edtMessage == null) Log.e("ChattingActivity", "edtMessage is null");
        if (btnSend == null) Log.e("ChattingActivity", "btnSend is null");
        if (btnAddMedia == null) Log.e("ChattingActivity", "btnAddMedia is null");
        if (btnEmoji == null) Log.e("ChattingActivity", "btnEmoji is null");
        if (btnCall == null) Log.e("ChattingActivity", "btnCall is null");
        if (btnVideoCall == null) Log.e("ChattingActivity", "btnVideoCall is null");

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(messageList, user.getId());
        recyclerView.setAdapter(adapter);

        messageService = RetrofitClient.getApiMessageService();
        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.connect();

        setupTaskbarName(userName);
        setupWebSocketListener();
        setupSendMessageListener();
        setupAdditionalButtons();
        setupScrollListener();
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);      // ✅ Cuộn đến tin nhắn mới nhất khi mở
        layoutManager.setReverseLayout(false);    // ✅ Thứ tự: cũ → mới
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        setupScrollListener();   // Gắn listener
        loadMessages(0);
    }

    private void setupTaskbarName(String userName) {
        setSupportActionBar(chatToolbar);
        if (chatToolbar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            tvUserName.setText(userName);
            chatToolbar.setNavigationOnClickListener(v -> finish());
        } else {
            Log.e("ChattingActivity", "chatToolbar is null in setupTaskbarName");
        }
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
        if (btnSend != null) {
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
        } else {
            Log.e("ChattingActivity", "btnSend is null in setupSendMessageListener");
        }
    }

    private void setupAdditionalButtons() {
        if (btnAddMedia != null) {
            btnAddMedia.setOnClickListener(v -> {
                Toast.makeText(this, "Chức năng thêm media chưa được triển khai", Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e("ChattingActivity", "btnAddMedia is null in setupAdditionalButtons");
        }

        if (btnEmoji != null) {
            btnEmoji.setOnClickListener(v -> {
                Toast.makeText(this, "Chức năng emoji chưa được triển khai", Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e("ChattingActivity", "btnEmoji is null in setupAdditionalButtons");
        }

        if (btnCall != null) {
            btnCall.setOnClickListener(v -> {
                Toast.makeText(this, "Chức năng gọi điện chưa được triển khai", Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e("ChattingActivity", "btnCall is null in setupAdditionalButtons"); // Dòng 110
        }

        if (btnVideoCall != null) {
            btnVideoCall.setOnClickListener(v -> {
                Toast.makeText(this, "Chức năng video call chưa được triển khai", Toast.LENGTH_SHORT).show();
            });
        } else {
            Log.e("ChattingActivity", "btnVideoCall is null in setupAdditionalButtons");
        }
    }

    private void setupScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager == null) return;

                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                Log.d("ChattingActivity", "firstVisibleItem=" + firstVisibleItem + ", isLoading=" + isLoading + ", isLastPage=" + isLastPage);

                if (firstVisibleItem <= 2 && !isLoading && !isLastPage) {
                    loadMessages(currentPage + 1);  // ✅ Tải thêm trang cũ
                }
            }
        });
    }

    private void loadMessages(int page) {
        if (isLoading) return;
        isLoading = true;

        messageService.getMessagesByConversationId(conversation.getId(), page, PAGE_SIZE)
                .enqueue(new Callback<PageResponse<Message>>() {
                    @Override
                    public void onResponse(Call<PageResponse<Message>> call, Response<PageResponse<Message>> response) {
                        isLoading = false;
                        if (response.isSuccessful() && response.body() != null) {
                            PageResponse<Message> pageResponse = response.body();
                            List<Message> newMessages = pageResponse.getContent();
                            isLastPage = pageResponse.isLast();

                            if (page == 0) {
                                // ✅ Tin nhắn mới nhất nằm ở cuối danh sách
                                messageList.clear();
                                messageList.addAll(newMessages);
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(messageList.size() - 1);
                            } else {
                                // ✅ Thêm các tin nhắn cũ vào đầu danh sách
                                int prevPosition = layoutManager.findFirstVisibleItemPosition();
                                View firstView = layoutManager.findViewByPosition(prevPosition);
                                int offset = (firstView == null) ? 0 : firstView.getTop();

                                messageList.addAll(0, newMessages);
                                adapter.notifyItemRangeInserted(0, newMessages.size());

                                // ✅ Giữ nguyên vị trí cuộn sau khi thêm dữ liệu
                                layoutManager.scrollToPositionWithOffset(prevPosition + newMessages.size(), offset);
                            }

                            if (!newMessages.isEmpty()) {
                                currentPage = page;
                            }
                        } else {
                            Toast.makeText(ChattingActivity.this, "Lỗi tải tin nhắn", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<Message>> call, Throwable t) {
                        isLoading = false;
                        Toast.makeText(ChattingActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
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