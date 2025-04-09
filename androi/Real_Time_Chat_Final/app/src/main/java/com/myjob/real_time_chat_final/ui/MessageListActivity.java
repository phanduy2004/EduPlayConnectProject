package com.myjob.real_time_chat_final.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.myjob.real_time_chat_final.adapter.ListChatAdapter;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;
import com.myjob.real_time_chat_final.api.UserService;
import com.myjob.real_time_chat_final.databinding.ActivityMessageListBinding;
import com.myjob.real_time_chat_final.model.Conversation;
import com.myjob.real_time_chat_final.model.ConversationMember;
import com.myjob.real_time_chat_final.model.ListChat;
import com.myjob.real_time_chat_final.model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageListActivity extends AppCompatActivity {
    private ActivityMessageListBinding binding;
    private ListChatAdapter userAdapter;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // ✅ Khởi tạo binding
        binding = ActivityMessageListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ✅ Nhận userID từ Intent
        userID = getIntent().getIntExtra("USER_ID", -1);
        if (userID == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy userID", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("USER_ID", "Response body: " + userID);

        // ✅ Khởi tạo RecyclerView
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ✅ Khởi tạo adapter và xử lý sự kiện click
        userAdapter = new ListChatAdapter(new ArrayList<>(), user -> {
            if (user.getConversationId() == 0) {  // Kiểm tra ID hợp lệ
                Toast.makeText(MessageListActivity.this, "Lỗi: Cuộc trò chuyện không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MessageListActivity.this, ChattingActivity.class);
            intent.putExtra("chat_sender_id", userID);
            intent.putExtra("conversation_id", user.getConversationId());
            intent.putExtra("chat_user_name", user.getUsername());
            startActivity(intent);
        });

        recyclerView.setAdapter(userAdapter);

        // ✅ Gọi API để lấy danh sách bạn chat
        getChatUsers(userID);
    }


    private void getChatUsers(int userId) {
        UserService userService = RetrofitClient.getApiUserService();
        Call<List<ConversationMember>> call = userService.getContacts(userId);

        call.enqueue(new Callback<List<ConversationMember>>() {
            @Override
            public void onResponse(Call<List<ConversationMember>> call, Response<List<ConversationMember>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ConversationMember> conversationMembers = response.body();

                    // 🔥 Kiểm tra dữ liệu trả về từ API
                    Log.d("API_RESPONSE", "Data: " + new Gson().toJson(conversationMembers));

                    // Danh sách hội thoại
                    List<ListChat> chatList = new ArrayList<>();
                    Set<Integer> uniqueConversations = new HashSet<>();

                    for (ConversationMember member : conversationMembers) {
                        Conversation conversation = member.getConversation();
                        User chatUser = member.getUser();

                        if (conversation == null || chatUser == null) continue;

                        int conversationId = (int) conversation.getId();

                        // 🔥 Kiểm tra nếu đã có trong danh sách thì bỏ qua
                        if (uniqueConversations.contains(conversationId)) {
                            continue;
                        }

                        boolean isGroup = conversation.isGroup();
                        String chatUserName;
                        Log.d("Is_Group", "Data: " + isGroup);

                        // Nếu là nhóm → Lấy tên nhóm | Nếu không → Lấy tên người đối diện
                        if (isGroup) {
                            chatUserName = conversation.getName();
                        } else {
                            chatUserName = chatUser.getUsername();
                        }

                        // Tạo đối tượng ListChat
                        ListChat listChat = new ListChat();
                        listChat.setConversationId(conversationId);
                        listChat.setUsername(chatUserName);
                        listChat.setEmail(chatUser.getEmail());
                        listChat.setId((int) chatUser.getId());

                        // ✅ Thêm vào danh sách và đánh dấu conversationId đã xử lý
                        chatList.add(listChat);
                        uniqueConversations.add(conversationId);
                        Log.d("FILTERED_CHAT", "Thêm vào danh sách: " + new Gson().toJson(listChat));
                    }
                    userAdapter.updateData(chatList);
                    Log.d("FINAL_CHAT_LIST", "Danh sách hội thoại sau khi lọc: " + new Gson().toJson(chatList));
                } else {
                    Log.e("API_ERROR", "Error code: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<List<ConversationMember>> call, Throwable t) {
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
            }
        });
    }

}

