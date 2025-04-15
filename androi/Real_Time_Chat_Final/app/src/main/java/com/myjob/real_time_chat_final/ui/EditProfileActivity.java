package com.myjob.real_time_chat_final.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.api.UserService;
import com.myjob.real_time_chat_final.model.User;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ShapeableImageView avatar;
    private EditText etUsername, etEmail;
    private Switch switchStatus;
    private Button btnSave, btnBack, btnChangeAvatar;
    private User currentUser;
    private UserService userService; // Đổi tên biến theo quy ước

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Khởi tạo views
        avatar = findViewById(R.id.avatar);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);

        // Khởi tạo UserService từ RetrofitClient
        userService = RetrofitClient.getApiUserService();

        // Lấy userId từ Intent
        int userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy thông tin user hiện tại từ server
        loadUserData(userId);

        // Xử lý nút "Thay đổi ảnh đại diện"
        btnChangeAvatar.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng thay đổi ảnh chưa được triển khai", Toast.LENGTH_SHORT).show();
        });

        // Xử lý nút "Lưu"
        btnSave.setOnClickListener(v -> updateUser(userId));

        // Xử lý nút "Quay lại"
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserData(int userId) {
        userService.getUserById((long) userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    // Hiển thị thông tin user lên giao diện
                    etUsername.setText(currentUser.getUsername());
                    etEmail.setText(currentUser.getEmail());
                    // TODO: Tải ảnh đại diện từ avatarUrl nếu có (dùng Glide hoặc Picasso)
                } else {
                    Toast.makeText(EditProfileActivity.this, "Không thể tải thông tin user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUser(int userId) {
        // Lấy dữ liệu từ giao diện
        String newUsername = etUsername.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();

        // Kiểm tra dữ liệu
        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng email
        if (!newEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật thông tin user
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        // avatarUrl có thể được cập nhật từ logic chọn ảnh (nếu triển khai)

        // Gửi yêu cầu cập nhật lên server
        userService.updateUser(userId, currentUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại UserActivity
                } else {
                    String errorMessage = "Cập nhật thất bại";
                    if (response.code() == 400) {
                        errorMessage = response.message(); // Ví dụ: "Email already exists"
                    } else if (response.code() == 404) {
                        errorMessage = "Không tìm thấy user";
                    }
                    Toast.makeText(EditProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}