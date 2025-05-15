package com.myjob.real_time_chat_final.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.ListChatAdapter;
import com.myjob.real_time_chat_final.adapter.MembersAdapter;
import com.myjob.real_time_chat_final.api.ConversationService;
import com.myjob.real_time_chat_final.modelDTO.ContactDTO;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;
import com.myjob.real_time_chat_final.api.UserService;
import com.myjob.real_time_chat_final.databinding.ActivityMessageListBinding;
import com.myjob.real_time_chat_final.model.Conversation;
import com.myjob.real_time_chat_final.model.ConversationMember;
import com.myjob.real_time_chat_final.model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageListActivity extends AppCompatActivity {
    private ActivityMessageListBinding binding;
    private ListChatAdapter userAdapter;
    private final int userID = LoginActivity.userid;
    private List<User> allUsers;
    private List<User> selectedUsers;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private ShapeableImageView avatar;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Toast currentToast; // Quản lý Toast để tránh hiển thị quá nhiều

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMessageListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                Log.d("MessageList", "Selected image URI: " + selectedImageUri);
                if (avatar != null) {
                    Glide.with(this).load(selectedImageUri).circleCrop().into(avatar);
                }
            }
        });

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("messenger");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationIcon(R.drawable.ic_back_ios);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (userID == -1) {
            showToast("Lỗi: Không tìm thấy userID");
            finish();
            return;
        }
        Log.e("USER_ID", "User ID: " + userID);

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new ListChatAdapter(new ArrayList<>(), user -> {
            if (user.getConversationId() == 0) {
                showToast("Lỗi: Cuộc trò chuyện không hợp lệ");
                return;
            }

            Intent intent = new Intent(MessageListActivity.this, ChattingActivity.class);
            intent.putExtra("chat_sender_id", userID);
            intent.putExtra("conversation_id", user.getConversationId());
            intent.putExtra("chat_user_name", user.getUsername());
            startActivity(intent);
        });

        recyclerView.setAdapter(userAdapter);

        BottomNavigationView navBar = binding.navBar;
        navBar.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_chatmessage) {
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
            } else if (itemId == R.id.nav_newsfeed) {
                Intent intent = new Intent(MessageListActivity.this, NewsFeedActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        navBar.setSelectedItemId(R.id.nav_chatmessage);

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
                showToast("Không thể tải danh sách người dùng");
                return true;
            }
            showCreateGroupDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getChatUsers(int userId) {
        ConversationService conversationService = RetrofitClient.getApiConversationService();
        Call<List<ContactDTO>> call = conversationService.getContacts(userId);

        call.enqueue(new Callback<List<ContactDTO>>() {
            @Override
            public void onResponse(Call<List<ContactDTO>> call, Response<List<ContactDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ContactDTO> conversationMembers = response.body();
                    Log.d("API_RESPONSE", "Data: " + new Gson().toJson(conversationMembers));
                    userAdapter.updateData(conversationMembers);
                    Log.d("FINAL_CHAT_LIST", "Danh sách hội thoại sau khi lọc: " + new Gson().toJson(conversationMembers));
                } else {
                    Log.e("API_ERROR", "Error code: " + response.code() + ", message: " + response.message());
                    showToast("Không thể tải danh sách cuộc trò chuyện: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<ContactDTO>> call, Throwable t) {
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
                showToast("Lỗi tải danh sách cuộc trò chuyện: " + t.getMessage());
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
                    allUsers.removeIf(user -> user.getId() == userID);
                    Log.d("ALL_USERS", "Danh sách người dùng (sau khi lọc): " + new Gson().toJson(allUsers));
                } else {
                    Log.e("API_ERROR", "Error code: " + response.code() + ", message: " + response.message());
                    showToast("Không thể tải danh sách người dùng: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("API_ERROR", "Request failed: " + t.getMessage());
                showToast("Lỗi tải danh sách người dùng: " + t.getMessage());
            }
        });
    }

    private void showCreateGroupDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_create_group);
        dialog.setCancelable(true);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
        }

        EditText groupNameEditText = dialog.findViewById(R.id.group_name_edit_text);
        RecyclerView membersRecyclerView = dialog.findViewById(R.id.members_recycler_view);
        Button createGroupButton = dialog.findViewById(R.id.create_group_button);
        SearchView searchView = dialog.findViewById(R.id.search_view);
        Button selectPhoto = dialog.findViewById(R.id.select_avatar_button);
        avatar = dialog.findViewById(R.id.group_avatar_image);

        if (selectedImageUri != null) {
            Glide.with(this).load(selectedImageUri).circleCrop().into(avatar);
        }

        selectPhoto.setOnClickListener(v -> requestStoragePermission());

        searchView.setIconifiedByDefault(false);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        if (searchPlate != null) {
            searchPlate.setBackgroundResource(android.R.color.transparent);
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
                showToast("Vui lòng nhập tên nhóm");
                return;
            }
            if (selectedUsers.isEmpty()) {
                showToast("Vui lòng chọn ít nhất một thành viên");
                return;
            }
            createGroup(groupName, selectedUsers);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void requestStoragePermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            Log.d("MessageList", "Quyền đã được cấp: " + permission);
            openGallery();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            showToast("Cần quyền truy cập thư viện để chọn ảnh đại diện");
            ActivityCompat.requestPermissions(this, new String[]{permission}, STORAGE_PERMISSION_CODE);
        } else {
            Log.d("MessageList", "Yêu cầu quyền: " + permission);
            ActivityCompat.requestPermissions(this, new String[]{permission}, STORAGE_PERMISSION_CODE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.setAction(MediaStore.ACTION_PICK_IMAGES);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Log.d("MessageList", "Trying ACTION_PICK_IMAGES");
        } else {
            intent.setAction(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Log.d("MessageList", "Trying ACTION_PICK");
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                imagePickerLauncher.launch(intent);
                Log.d("MessageList", "Launching intent: " + intent.getAction());
            } catch (Exception e) {
                Log.e("MessageList", "Lỗi mở thư viện: " + e.getMessage(), e);
                showToast("Không thể mở thư viện ảnh");
            }
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Log.d("MessageList", "Trying ACTION_GET_CONTENT as fallback");
            if (intent.resolveActivity(getPackageManager()) != null) {
                try {
                    imagePickerLauncher.launch(Intent.createChooser(intent, "Chọn ứng dụng để chọn ảnh"));
                    Log.d("MessageList", "Launching fallback intent with chooser");
                } catch (Exception e) {
                    Log.e("MessageList", "Lỗi mở thư viện (fallback): " + e.getMessage(), e);
                    showToast("Không tìm thấy ứng dụng để chọn ảnh");
                }
            } else {
                Log.e("MessageList", "Không có ứng dụng nào hỗ trợ chọn ảnh");
                showToast("Không có ứng dụng nào hỗ trợ chọn ảnh. Vui lòng cài đặt Google Photos hoặc ứng dụng thư viện ảnh từ Play Store.");
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private byte[] getBytesFromUri(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            if (inputStream == null) return null;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            Log.e("MessageList", "Lỗi đọc dữ liệu ảnh: " + e.getMessage(), e);
            return null;
        }
    }

    private void createGroup(String groupName, List<User> members) {
        StringBuilder errorMessage = new StringBuilder();
        if (!isNetworkAvailable()) {
            errorMessage.append("Không có kết nối mạng\n");
        }
        if (selectedImageUri == null) {
            errorMessage.append("Vui lòng chọn ảnh đại diện cho nhóm\n");
        }

        if (errorMessage.length() > 0) {
            showToast(errorMessage.toString().trim());
            return;
        }

        ConversationService conversationService = RetrofitClient.getApiConversationService();

        byte[] imageBytes = getBytesFromUri(selectedImageUri);
        if (imageBytes == null) {
            showToast("Không thể đọc ảnh");
            return;
        }

        String fileName = "group_avatar_" + System.currentTimeMillis() + ".jpg";
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
        MultipartBody.Part avatarPart = MultipartBody.Part.createFormData("avatar", fileName, requestBody);

        Call<ResponseBody> uploadCall = conversationService.uploadGroupAvatar(avatarPart);
        uploadCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String avatarUrl = response.body().string();
                        Log.d("UPLOAD_AVATAR", "Avatar URL: " + avatarUrl);

                        Conversation conversation = new Conversation();
                        conversation.setGroup(true);
                        conversation.setName(groupName);
                        conversation.setAvatarUrl(avatarUrl);

                        Log.d("CREATE_GROUP_REQUEST", "Sending Conversation: " + new Gson().toJson(conversation));

                        Call<Conversation> createGroupCall = conversationService.createGroupConversation(conversation);
                        createGroupCall.enqueue(new Callback<Conversation>() {
                            @Override
                            public void onResponse(Call<Conversation> call, Response<Conversation> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Conversation createdConversation = response.body();
                                    Log.d("CREATE_GROUP_RESPONSE", "Received Conversation: " + new Gson().toJson(createdConversation));
                                    if (!createdConversation.isGroup()) {
                                        Log.e("CREATE_GROUP_ERROR", "Backend returned a non-group conversation!");
                                        showToast("Lỗi: Cuộc trò chuyện không phải là nhóm");
                                        return;
                                    }
                                    addMembersToGroup(createdConversation.getId(), members);
                                } else {
                                    Log.e("CREATE_GROUP_ERROR", "Error code: " + response.code() + ", message: " + response.message());
                                    showToast("Tạo nhóm thất bại: " + response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<Conversation> call, Throwable t) {
                                Log.e("CREATE_GROUP_ERROR", "Request failed: " + t.getMessage());
                                showToast("Lỗi: " + t.getMessage());
                            }
                        });
                    } catch (IOException e) {
                        Log.e("UPLOAD_ERROR", "Error reading response: " + e.getMessage(), e);
                        showToast("Lỗi đọc phản hồi từ server: " + e.getMessage());
                    }
                } else {
                    String errorBody = "Unknown error";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                        Log.e("UPLOAD_ERROR", "Error code: " + response.code() + ", error body: " + errorBody);
                        showToast("Upload ảnh thất bại, mã lỗi: " + response.code() + ", chi tiết: " + errorBody);
                    } catch (IOException e) {
                        Log.e("UPLOAD_ERROR", "Error reading error body: " + e.getMessage(), e);
                        showToast("Upload ảnh thất bại, mã lỗi: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("UPLOAD_ERROR", "Request failed: " + t.getMessage(), t);
                showToast("Upload ảnh thất bại: " + t.getMessage());
            }
        });
    }

    private void addMembersToGroup(long conversationId, List<User> members) {
        ConversationService conversationService = RetrofitClient.getApiConversationService();
        List<ConversationMember> conversationMembers = new ArrayList<>();

        User currentUser = new User();
        currentUser.setId(userID);
        ConversationMember currentUserMember = new ConversationMember();
        currentUserMember.setConversation(new Conversation(conversationId, true, null, null, null, null, null));
        currentUserMember.setUser(currentUser);
        conversationMembers.add(currentUserMember);

        for (User user : members) {
            if (user.getId() != userID) {
                ConversationMember member = new ConversationMember();
                member.setConversation(new Conversation(conversationId, true, null, null, null, null, null));
                member.setUser(user);
                conversationMembers.add(member);
            }
        }

        Log.d("ADD_MEMBERS_REQUEST", "Sending Members: " + new Gson().toJson(conversationMembers));

        Call<Void> call = conversationService.addMembersToConversation(conversationId, conversationMembers);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showToast("Tạo nhóm thành công");
                    getChatUsers(userID);
                    selectedImageUri = null;
                    if (avatar != null) {
                        Glide.with(MessageListActivity.this).load(R.drawable.ic_user).circleCrop().into(avatar);
                    }
                } else {
                    Log.e("ADD_MEMBERS_ERROR", "Error code: " + response.code() + ", message: " + response.message());
                    showToast("Thêm thành viên thất bại: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ADD_MEMBERS_ERROR", "Request failed: " + t.getMessage());
                showToast("Lỗi: " + t.getMessage());
            }
        });
    }

    private void showToast(String message) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        currentToast.show();
    }
}