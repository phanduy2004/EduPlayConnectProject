package com.myjob.real_time_chat_final.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.api.UserService;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    EditText edtNewPassword, edtConfirmPassword;
    ImageView backOTP;
    TextView exitActivity;
    Button resetPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        anhxa();
        eventTogglePasswordVisibility(edtNewPassword,R.drawable.baseline_lock_24, R.drawable.baseline_remove_red_eye_24, R.drawable.icons8_closed_eye_50);
        eventTogglePasswordVisibility(edtConfirmPassword,R.drawable.baseline_lock_open_24, R.drawable.baseline_remove_red_eye_24, R.drawable.icons8_closed_eye_50);
        Intent intent = getIntent();
        Long userId;
        String id = intent.getStringExtra("userId");
        userId = Long.parseLong(id);

        resetPass.setOnClickListener(view -> {
            Log.d("userId", userId.toString());
            EventResetPass(userId);

        });
        exitActivity.setOnClickListener(view -> {
            EventBackLogin();
        });
        backOTP.setOnClickListener(view -> {
            Intent intent1 = new Intent(ResetPasswordActivity.this, VerifyCodeResetPassActivity.class);
            startActivity(intent1);
        });
    }
    void anhxa()
    {
        edtNewPassword = findViewById(R.id.newPassword);
        edtConfirmPassword =findViewById(R.id.confirmNewPassword);
        resetPass = findViewById(R.id.btnResetPassword);
        backOTP = findViewById(R.id.imgBackOTP);
        exitActivity = findViewById(R.id.textExit);
    }

    private void eventTogglePasswordVisibility(EditText editText, int leftIconRes,
                                               int eyeOpenIconRes,
                                               int eyeClosedIconRes){
        editText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editText.getRight()
                        - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                    // Kiểm tra nếu đang ở chế độ mật khẩu
                    if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        // Hiển thị mật khẩu
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(
                                leftIconRes,
                                0,
                                eyeOpenIconRes,
                                0
                        );
                    } else {
                        // Ẩn mật khẩu
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(
                                leftIconRes,
                                0,
                                eyeClosedIconRes,
                                0
                        );
                    }

                    // Giữ con trỏ cuối text
                    editText.setSelection(editText.getText().length());
                    return true;
                }
            }

            return false;
        });
    }

    private void EventResetPass(Long userId)
    {
        String newPassword = edtNewPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu mới và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }
        // Tạo request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("newPassword", newPassword);
        requestBody.put("confirmPassword", confirmPassword);

        RetrofitClient.getApiUserService().resetPassword(userId, requestBody).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> result = response.body();
                    String status = (String) result.get("status");
                    String message = (String) result.get("message");

                    Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();

                    if ("success".equals(status)) {
                        // Thay đổi mật khẩu thành công, quay lại màn hình trước
                        EventBackLogin();
                    }else {
                        Toast.makeText(ResetPasswordActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void EventBackLogin(){
        Intent intent= new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}