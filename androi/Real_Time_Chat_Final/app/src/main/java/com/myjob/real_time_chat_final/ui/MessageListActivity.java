package com.myjob.real_time_chat_final.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.ListChatAdapter;
import com.myjob.real_time_chat_final.adapter.MembersAdapter;
import com.myjob.real_time_chat_final.api.ConversationService;
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
    private List<User> allUsers; // Danh sách tất cả người dùng
    private List<User> selectedUsers; // Danh sách người dùng được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Khởi tạo binding
        binding = ActivityMessageListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập Toolbar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tin nhắn");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút "Quay lại"
        }

        // Đổi màu icon "Quay lại"
        toolbar.setNavigationIcon(R.drawable.ic_back_ios);
        // Xử lý sự kiện nhấn nút "Quay lại"
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Nhận userID từ Intent
        userID = getIntent().getIntExtra("USER_ID", -1);
        if (userID == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy userID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.e("USER_ID", "User ID: " + userID);

        // Khởi tạo RecyclerView
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo adapter và xử lý sự kiện click
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

        // Thiết lập BottomNavigationView
        BottomNavigationView navBar = binding.navBar;
        navBar.setOnNavigationItemSelectedListener(item -> {
            // Xử lý các mục trong bottom navigation
            int itemId = item.getItemId();
            if (itemId == R.id.nav_chatmessage) {
                // Đã ở MessageListActivity, không làm gì
                return true;
            } else if (itemId == R.id.nav_home) {
                Intent intent = new Intent(MessageListActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_userhome) {
                Intent intent = new Intent(MessageListActivity.this, UserActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_addFriend) {
                Intent intent = new Intent(MessageListActivity.this, FriendListActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_findRoom) {
                Intent intent = new Intent(MessageListActivity.this, JoinRoomActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Đặt mục "Tin nhắn" được chọn mặc định
        navBar.setSelectedItemId(R.id.nav_chatmessage);

        // Gọi API để lấy danh sách bạn chat và tất cả người dùng
        getChatUsers(userID);
        loadAllUsers();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_create_group) {
            if (allUsers == null || allUsers.isEmpty()) {
                Toast.makeText(this, "Không thể tải danh sách người dùng", Toast.LENGTH_SHORT).show();
                return true;
            }
            showCreateGroupDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getChatUsers(int userId) {
        UserService userService = RetrofitClient.getApiUserService();
        Call<List<ConversationMember>> call = userService.getContacts(userId);

        call.enqueue(new Callback<List<ConversationMember>>() {
            @Override
            public void onResponse(Call<List<ConversationMember>> call, Response<List<ConversationMember>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ConversationMember> conversationMembers = response.body();

                    // Kiểm tra dữ liệu trả về từ API
                    Log.d("API_RESPONSE", "Data: " + new Gson().toJson(conversationMembers));

                    // Danh sách hội thoại
                    List<ListChat> chatList = new ArrayList<>();
                    Set<Integer> uniqueConversations = new HashSet<>();

                    for (ConversationMember member : conversationMembers) {
                        Conversation conversation = member.getConversation();
                        User chatUser = member.getUser();

                        if (conversation == null || chatUser == null) continue;

                        int conversationId = (int) conversation.getId();

                        // Kiểm tra nếu đã có trong danh sách thì bỏ qua
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
                        listChat.setAvatarUrl(chatUser.getAvatarUrl());
                        // Thêm vào danh sách và đánh dấu conversationId đã xử lý
                        chatList.add(listChat);
                        uniqueConversations.add(conversationId);
                        Log.d("FILTERED_CHAT", "Thêm vào danh sách: " + new Gson().toJson(listChat));
                    }
                    userAdapter.updateData(chatList);
                    Log.d("FINAL_CHAT_LIST", "Danh sách hội thoại sau khi lọc: " + new Gson().toJson(chatList));
                } else {
                    Log.e("API_ERROR", "Error code: " + response.code() + ", message: " + response.message());
                    Toast.makeText(MessageListActivity.this, "Không thể tải danh sách cuộc trò chuyện: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ConversationMember>> call, Throwable t) {
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
                Toast.makeText(MessageListActivity.this, "Lỗi tải danh sách cuộc trò chuyện: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllUsers() {
        UserService userService = RetrofitClient.getApiUserService();
        Call<List<User>> call = userService.getAllUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allUsers = response.body();
                    // Loại bỏ người dùng hiện tại khỏi danh sách
                    allUsers.removeIf(user -> user.getId() == userID);
                    Log.d("ALL_USERS", "Danh sách người dùng (sau khi lọc): " + new Gson().toJson(allUsers));
                } else {
                    Log.e("API_ERROR", "Error code: " + response.code() + ", message: " + response.message());
                    Toast.makeText(MessageListActivity.this, "Không thể tải danh sách người dùng: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
                Toast.makeText(MessageListActivity.this, "Lỗi tải danh sách người dùng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCreateGroupDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_create_group);
        dialog.setCancelable(true);

        // Tùy chỉnh dialog để có phong cách iPhone
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
        }

        EditText groupNameEditText = dialog.findViewById(R.id.group_name_edit_text);
        RecyclerView membersRecyclerView = dialog.findViewById(R.id.members_recycler_view);
        Button createGroupButton = dialog.findViewById(R.id.create_group_button);
        SearchView searchView = dialog.findViewById(R.id.search_view);

        // Tùy chỉnh SearchView
        searchView.setIconifiedByDefault(false); // Luôn hiển thị thanh tìm kiếm
        searchView.setMaxWidth(Integer.MAX_VALUE);
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        if (searchPlate != null) {
            searchPlate.setBackgroundResource(android.R.color.transparent); // Xóa nền mặc định của SearchView
        }
        int searchTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = searchView.findViewById(searchTextId);
        if (searchText != null) {
            searchText.setTextSize(16);
            searchText.setTextColor(Color.BLACK);
            searchText.setHintTextColor(Color.parseColor("#A0A0A0"));
            searchText.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL));
        }

        selectedUsers = new ArrayList<>();
        MembersAdapter membersAdapter = new MembersAdapter(allUsers != null ? allUsers : new ArrayList<>(), selectedUsers, user -> {
            if (selectedUsers.contains(user)) {
                selectedUsers.remove(user);
            } else {
                selectedUsers.add(user);
            }
        });
        membersRecyclerView.setAdapter(membersAdapter);

        // Xử lý tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                membersAdapter.filter(newText);
                return true;
            }
        });

        createGroupButton.setOnClickListener(v -> {
            String groupName = groupNameEditText.getText().toString().trim();
            if (groupName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên nhóm", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedUsers.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một thành viên", Toast.LENGTH_SHORT).show();
                return;
            }
            createGroup(groupName, selectedUsers);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void createGroup(String groupName, List<User> members) {
        ConversationService conversationService = RetrofitClient.getApiConversationService();

        // Tạo Conversation với is_group = true
        Conversation conversation = new Conversation();
        conversation.setGroup(true);
        conversation.setName(groupName);

        // Log dữ liệu gửi lên để kiểm tra
        Log.d("CREATE_GROUP_REQUEST", "Sending Conversation: " + new Gson().toJson(conversation));

        // Gửi yêu cầu tạo nhóm
        Call<Conversation> call = conversationService.createGroupConversation(conversation);
        call.enqueue(new Callback<Conversation>() {
            @Override
            public void onResponse(Call<Conversation> call, Response<Conversation> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Conversation createdConversation = response.body();
                    Log.d("CREATE_GROUP_RESPONSE", "Received Conversation: " + new Gson().toJson(createdConversation));
                    if (!createdConversation.isGroup()) {
                        Log.e("CREATE_GROUP_ERROR", "Backend returned a non-group conversation!");
                        Toast.makeText(MessageListActivity.this, "Lỗi: Cuộc trò chuyện không phải là nhóm", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addMembersToGroup(createdConversation.getId(), members);
                } else {
                    Log.e("CREATE_GROUP_ERROR", "Error code: " + response.code() + ", message: " + response.message());
                    Toast.makeText(MessageListActivity.this, "Tạo nhóm thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Conversation> call, Throwable t) {
                Log.e("CREATE_GROUP_ERROR", "Request failed: " + t.getMessage());
                Toast.makeText(MessageListActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMembersToGroup(long conversationId, List<User> members) {
        ConversationService conversationService = RetrofitClient.getApiConversationService();
        List<ConversationMember> conversationMembers = new ArrayList<>();

        // Thêm chính người dùng hiện tại (userID) vào nhóm (mặc định)
        User currentUser = new User();
        currentUser.setId(userID);
        ConversationMember currentUserMember = new ConversationMember();
        currentUserMember.setConversation(new Conversation(conversationId, true, null, null, null, null));
        currentUserMember.setUser(currentUser);
        conversationMembers.add(currentUserMember);

        // Thêm các thành viên được chọn
        for (User user : members) {
            // Đảm bảo không thêm lại người dùng hiện tại nếu họ đã được chọn
            if (user.getId() != userID) {
                ConversationMember member = new ConversationMember();
                member.setConversation(new Conversation(conversationId, true, null, null, null, null));
                member.setUser(user);
                conversationMembers.add(member);
            }
        }

        // Log dữ liệu gửi lên để kiểm tra
        Log.d("ADD_MEMBERS_REQUEST", "Sending Members: " + new Gson().toJson(conversationMembers));

        // Gửi yêu cầu thêm thành viên
        Call<Void> call = conversationService.addMembersToConversation(conversationId, conversationMembers);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MessageListActivity.this, "Tạo nhóm thành công", Toast.LENGTH_SHORT).show();
                    getChatUsers(userID); // Cập nhật danh sách cuộc trò chuyện
                } else {
                    Log.e("ADD_MEMBERS_ERROR", "Error code: " + response.code() + ", message: " + response.message());
                    Toast.makeText(MessageListActivity.this, "Thêm thành viên thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ADD_MEMBERS_ERROR", "Request failed: " + t.getMessage());
                Toast.makeText(MessageListActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}