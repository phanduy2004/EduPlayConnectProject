package com.myjob.real_time_chat_final.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.api.UserService;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button saveButton;
    private UserService userService;
    private final long userId = LoginActivity.userid; // Giả sử bạn đã có userId từ màn hình trước (ví dụ: từ Intent)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Khởi tạo các view
        oldPasswordEditText = findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        saveButton = findViewById(R.id.saveButton);

        // Khởi tạo UserService từ RetrofitClient
        userService = RetrofitClient.getApiUserService();

        // Lấy userId từ Intent (giả sử bạn truyền userId từ màn hình trước)
        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy userId", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Xử lý sự kiện khi nhấn nút "Lưu"
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Kiểm tra các trường nhập liệu
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu mới và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("oldPassword", oldPassword);
        requestBody.put("newPassword", newPassword);
        requestBody.put("confirmPassword", confirmPassword);

        // Gọi API thay đổi mật khẩu
        Call<Map<String, Object>> call = userService.changePassword(userId, requestBody);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> result = response.body();
                    String status = (String) result.get("status");
                    String message = (String) result.get("message");

                    Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_SHORT).show();

                    if ("success".equals(status)) {
                        // Thay đổi mật khẩu thành công, quay lại màn hình trước
                        finish();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}