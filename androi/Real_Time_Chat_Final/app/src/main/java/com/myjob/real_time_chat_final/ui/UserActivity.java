package com.myjob.real_time_chat_final.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.api.UserService;
import com.myjob.real_time_chat_final.model.User;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    private final int currentUserId = LoginActivity.userid;
    private TextView tvName;
    private ShapeableImageView avatar;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Lấy userId từ SharedPreferences

        // Kiểm tra userId
        if (currentUserId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin đăng nhập. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            // Chuyển về màn hình đăng nhập
            Intent loginIntent = new Intent(UserActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Khởi tạo views
        tvName = findViewById(R.id.tvName);
        avatar = findViewById(R.id.avatar);

        // Khởi tạo UserService từ RetrofitClient
        userService = RetrofitClient.getApiUserService();

        // Tải thông tin user từ server
        loadUserData();

        // Thiết lập sự kiện click cho các item
        findViewById(R.id.account_setup).setOnClickListener(v ->
                Toast.makeText(this, "Thiết lập tài khoản", Toast.LENGTH_SHORT).show());

        findViewById(R.id.edit_info).setOnClickListener(v -> {
            Log.d("UserActivity", "Nhấn vào 'Chỉnh sửa thông tin', userId: " + currentUserId);
            Intent intent = new Intent(UserActivity.this, EditProfileActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivityForResult(intent, 1); // Sử dụng startActivityForResult để nhận kết quả từ EditProfileActivity
        });

        findViewById(R.id.change_password).setOnClickListener(v ->
                Toast.makeText(this, "Thay đổi mật khẩu", Toast.LENGTH_SHORT).show());

        findViewById(R.id.notification).setOnClickListener(v ->
                Toast.makeText(this, "Bật thông báo", Toast.LENGTH_SHORT).show());

        findViewById(R.id.auto_update).setOnClickListener(v ->
                Toast.makeText(this, "Tự động cập nhật", Toast.LENGTH_SHORT).show());

        findViewById(R.id.about_us).setOnClickListener(v ->
                Toast.makeText(this, "Về chúng tôi", Toast.LENGTH_SHORT).show());

        findViewById(R.id.privacy_policy).setOnClickListener(v ->
                Toast.makeText(this, "Chính sách bảo mật", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnLogout).setOnClickListener(v ->
                Toast.makeText(this, "Đăng xuất", Toast.LENGTH_SHORT).show());
    }

    private void loadUserData() {
        userService.getUserById((long) currentUserId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    // Hiển thị username lên tvName
                    tvName.setText(user.getUsername());
                    // Tải ảnh đại diện từ avatarUrl bằng Glide
                    if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                        // Thêm base URL vào avatarUrl
                        String baseUrl ="http://10.0.2.2:8686"; // Hoặc lấy từ RetrofitClient
                        String fullAvatarUrl = baseUrl + user.getAvatarUrl();
                        Log.d("EditProfile", "Loading avatar URL: " + fullAvatarUrl);
                        Glide.with(UserActivity.this)
                                .load(fullAvatarUrl)
                                .circleCrop()
                                .error(R.drawable.ic_user) // Hình ảnh mặc định nếu tải thất bại
                                .into(avatar);
                    } else {
                        // Hiển thị ảnh mặc định nếu không có avatarUrl
                        Glide.with(UserActivity.this)
                                .load(R.drawable.ic_user)
                                .circleCrop()
                                .into(avatar);
                    }
                } else {
                    Toast.makeText(UserActivity.this, "Không thể tải thông tin user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Sau khi cập nhật thông tin từ EditProfileActivity, tải lại thông tin user
            loadUserData();
        }
    }
}