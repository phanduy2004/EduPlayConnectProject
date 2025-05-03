package com.myjob.real_time_chat_final.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;
import com.myjob.real_time_chat_final.api.UserService;
import com.myjob.real_time_chat_final.model.User;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword, editConfirmPassword, edtUsername;
    private Button btnRegister;
    private UserService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        edtEmail = findViewById(R.id.emailEditText);
        edtPassword = findViewById(R.id.passwordEditText);
        editConfirmPassword = findViewById(R.id.confirmPasswordEditText);
        edtUsername = findViewById(R.id.usernameEditText);
        btnRegister = findViewById(R.id.registerButton);

        apiService = RetrofitClient.getInstance().create(UserService.class);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        TextView loginTextView = findViewById(R.id.loginTextView);
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String passwordConfirm = editConfirmPassword.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();

        // Kiểm tra đầu vào rỗng
        if (email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || username.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra xác thực mật khẩu
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(RegisterActivity.this, "Mật khẩu xác thực không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(username, email, password, false);
        Call<Map<String, String>> call = apiService.signUpPostForm(user);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String message = response.body().get("message");
                        Log.d("API_RESPONSE", "Phản hồi: " + message);
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, VerifyCodeActivity.class);
                        intent.putExtra("email", user.getEmail());
                        startActivity(intent);
                    } else {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Lỗi không xác định";
                        Log.e("API_RESPONSE", "Error: " + errorBody);
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại! " + errorBody, Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Lỗi xử lý phản hồi!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Đăng ký", "Lỗi: " + t.getMessage());
            }
        });
    }
}