package com.myjob.real_time_chat_final.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyCodeActivity extends AppCompatActivity {
    private EditText editOtp;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_code);
        editOtp = findViewById(R.id.otpEditText);
        Button verifyButton = findViewById(R.id.confirmOtpButton);
        email = getIntent().getStringExtra("email");
        verifyButton.setOnClickListener(v -> verifyOTP());
    }
    private void verifyOTP() {
        String otp = editOtp.getText().toString().trim();
        if (otp.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã OTP!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gửi dữ liệu xác thực OTP
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("code", otp);
        requestBody.put("email", email);

        RetrofitClient.getApiUserService().verifyCode(requestBody).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message");
                    Log.d("VerifyCode", "Server response: " + response.body());

                    if ("success".equals(message)) {
                        Toast.makeText(VerifyCodeActivity.this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VerifyCodeActivity.this, LoginActivity.class);
                        // Gửi userId sang MessListActivity
                        startActivity(intent);
                    } else {
                        Toast.makeText(VerifyCodeActivity.this, "Xác thực thất bại!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string(); // Lấy nội dung lỗi từ server
                        Log.e("VerifyCode", "Error response: " + errorBody);
                    } catch (Exception e) {
                        Log.e("VerifyCode", "Error parsing error body", e);
                    }
                    Toast.makeText(VerifyCodeActivity.this, "Lỗi từ server!", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(VerifyCodeActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}