package com.myjob.real_time_chat_final.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.api.UserService;
import com.myjob.real_time_chat_final.model.User;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;
    private ShapeableImageView avatar;
    private EditText etUsername, etEmail;
    private Button btnSave, btnBack, btnChangeAvatar;
    private User currentUser;
    private UserService userService;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

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

        // Khởi tạo trình chọn ảnh
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                Log.d("EditProfile", "Selected image URI: " + selectedImageUri);
                Glide.with(this).load(selectedImageUri).circleCrop().into(avatar);
            }
        });

        // Lấy thông tin user hiện tại từ server
        loadUserData(userId);

        // Xử lý nút "Thay đổi ảnh đại diện"
        btnChangeAvatar.setOnClickListener(v -> requestStoragePermission());

        // Xử lý nút "Lưu"
        btnSave.setOnClickListener(v -> updateUser(userId));

        // Xử lý nút "Quay lại"
        btnBack.setOnClickListener(v -> finish());
    }

    private void requestStoragePermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            Log.d("EditProfile", "Quyền đã được cấp: " + permission);
            openGallery();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            Toast.makeText(this, "Cần quyền truy cập thư viện để chọn ảnh đại diện", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{permission}, STORAGE_PERMISSION_CODE);
        } else {
            Log.d("EditProfile", "Yêu cầu quyền: " + permission);
            ActivityCompat.requestPermissions(this, new String[]{permission}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("EditProfile", "Quyền được cấp, mở thư viện ảnh");
                openGallery();
            } else {
                String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                        Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    Log.d("EditProfile", "Quyền bị từ chối vĩnh viễn: " + permission);
                    Toast.makeText(this, "Quyền truy cập thư viện bị từ chối. Vui lòng cấp quyền trong cài đặt.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } else {
                    Log.d("EditProfile", "Quyền bị từ chối: " + permission);
                    Toast.makeText(this, "Cần quyền truy cập thư viện để chọn ảnh", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void openGallery() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        try {
            imagePickerLauncher.launch(intent);
        } catch (Exception e) {
            Log.e("EditProfile", "Lỗi mở thư viện: " + e.getMessage(), e);
            Toast.makeText(this, "Không thể mở thư viện ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loadUserData(int userId) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
            return;
        }
        userService.getUserById((long) userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    etUsername.setText(currentUser.getUsername());
                    etEmail.setText(currentUser.getEmail());
                    if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()) {
                        // Thêm base URL vào avatarUrl
                        String baseUrl ="http://10.0.2.2:8686"; // Hoặc lấy từ RetrofitClient
                        String fullAvatarUrl = baseUrl + currentUser.getAvatarUrl();
                        Log.d("EditProfile", "Loading avatar URL: " + fullAvatarUrl);
                        Glide.with(EditProfileActivity.this)
                                .load(fullAvatarUrl)
                                .circleCrop()
                                .error(R.drawable.ic_user) // Hình ảnh mặc định nếu tải thất bại
                                .into(avatar);
                    } else {
                        // Hiển thị ảnh mặc định nếu không có avatarUrl
                        Glide.with(EditProfileActivity.this)
                                .load(R.drawable.ic_user)
                                .circleCrop()
                                .into(avatar);
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Không thể tải thông tin user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("EditProfile", "Lỗi tải thông tin user: " + t.getMessage(), t);
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUser(int userId) {
        String newUsername = etUsername.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();

        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);

        if (selectedImageUri != null) {
            uploadAvatar(userId, () -> updateUserInfo(userId));
        } else {
            updateUserInfo(userId);
        }
    }

    private void uploadAvatar(int userId, Runnable onSuccess) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImageUri == null) {
            Toast.makeText(this, "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String mimeType = getContentResolver().getType(selectedImageUri);
            if (mimeType == null || !(mimeType.equals("image/jpeg") || mimeType.equals("image/png"))) {
                Toast.makeText(this, "Chỉ hỗ trợ định dạng JPEG hoặc PNG", Toast.LENGTH_SHORT).show();
                return;
            }

            byte[] imageBytes = getBytesFromUri(selectedImageUri);
            if (imageBytes == null) {
                Toast.makeText(this, "Không thể đọc file ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", "image.jpg", requestFile);

            userService.uploadAvatar((long) userId, body).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        currentUser.setAvatarUrl(response.body().getAvatarUrl());
                        Toast.makeText(EditProfileActivity.this, "Tải ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                        // Tải lại ảnh đại diện ngay sau khi upload
                        String baseUrl ="http://10.0.2.2:8686";
                        String fullAvatarUrl = baseUrl + response.body().getAvatarUrl();
                        Glide.with(EditProfileActivity.this)
                                .load(fullAvatarUrl)
                                .circleCrop()
                                .error(R.drawable.ic_user)
                                .into(avatar);
                        onSuccess.run();
                    } else {
                        String errorMessage = "Tải ảnh thất bại";
                        if (response.code() == 400) {
                            errorMessage = "Dữ liệu không hợp lệ";
                        } else if (response.code() == 404) {
                            errorMessage = "Không tìm thấy user";
                        }
                        Toast.makeText(EditProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e("EditProfile", "Lỗi tải ảnh: " + t.getMessage(), t);
                    String errorMessage = t instanceof IOException ? "Lỗi kết nối mạng. Vui lòng kiểm tra Internet." : "Lỗi tải ảnh: " + t.getMessage();
                    Toast.makeText(EditProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e("EditProfile", "Lỗi xử lý ảnh: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserInfo(int userId) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
            return;
        }
        userService.updateUser(userId, currentUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMessage = "Cập nhật thất bại";
                    if (response.code() == 400) {
                        errorMessage = response.message();
                    } else if (response.code() == 404) {
                        errorMessage = "Không tìm thấy user";
                    }
                    Toast.makeText(EditProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("EditProfile", "Lỗi cập nhật user: " + t.getMessage(), t);
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private byte[] getBytesFromUri(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            if (inputStream == null) {
                return null;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            Log.e("EditProfile", "Lỗi đọc dữ liệu ảnh: " + e.getMessage(), e);
            return null;
        }
    }
}