package com.myjob.real_time_chat_final.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyCodeResetPassActivity extends AppCompatActivity {
    PinView otpCode;
    Button btn_switchChangePass;
    ImageView backForgotPass;
    String userId, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_code_reset_pass);
        anhxa();
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        email = intent.getStringExtra("Email");

        btn_switchChangePass.setOnClickListener(view -> {
            EventVerifyOTP();
        });
    }
    void anhxa()
    {
        otpCode = findViewById(R.id.password_otpCode);
        btn_switchChangePass = findViewById(R.id.confirmOtp);
        backForgotPass = findViewById(R.id.img_backEmail);
    }
    private void EventVerifyOTP(){
        String otp = otpCode.getText().toString();
        if(otp.isEmpty()){
            Toast.makeText(this, "Vui lòng nhập mã OTP!", Toast.LENGTH_SHORT).show();
            return;
        }
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
                        Toast.makeText(VerifyCodeResetPassActivity.this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VerifyCodeResetPassActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("userId", userId);
                        // Gửi userId sang ResetPasswordActivity
                        startActivity(intent);
                    } else {
                        Toast.makeText(VerifyCodeResetPassActivity.this, "Xác thực thất bại!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string(); // Lấy nội dung lỗi từ server
                        Log.e("VerifyCode", "Error response: " + errorBody);
                    } catch (Exception e) {
                        Log.e("VerifyCode", "Error parsing error body", e);
                    }
                    Toast.makeText(VerifyCodeResetPassActivity.this, "Lỗi từ server!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(VerifyCodeResetPassActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}