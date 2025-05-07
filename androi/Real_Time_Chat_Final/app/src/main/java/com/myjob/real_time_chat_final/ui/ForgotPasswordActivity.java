package com.myjob.real_time_chat_final.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class ForgotPasswordActivity extends AppCompatActivity {

    Button btnSwitchOTP;
    EditText edtEmail;
    ImageView imgBackLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        anhxa();
        btnSwitchOTP.setOnClickListener(view -> {
            eventSendOTP();
        });
    }
    void anhxa()
    {
        btnSwitchOTP = findViewById(R.id.btn_switchOTP);
        edtEmail = findViewById(R.id.edt_email);
        imgBackLogin = findViewById(R.id.imgBackLogin);
    }
    private void eventSendOTP()
    {
        String email = edtEmail.getText().toString();
        if(email.isEmpty()){
            Toast.makeText(this, "Vui lòng nhập Email", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        RetrofitClient.getApiUserService().sendOTPForgotPassword(email).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message");
                    String userId = response.body().get("userid");

                    // Nếu có userId thì coi là gửi OTP thành công
                    if (userId != null) {
                        Intent intent = new Intent(ForgotPasswordActivity.this, VerifyCodeResetPassActivity.class);
                        intent.putExtra("USER_ID", userId);
                        intent.putExtra("Email", email);
                        startActivity(intent);
                        finish();
                        Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Không tìm thấy userId trong phản hồi", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Response thất bại: " + response.code() + " - " + response.message());
                    Toast.makeText(ForgotPasswordActivity.this, "Gửi OTP thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API: " + t.getMessage(), t);
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}