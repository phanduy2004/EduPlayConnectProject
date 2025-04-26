package com.myjob.real_time_chat_final.ui;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.PostAdapter;
import com.myjob.real_time_chat_final.adapter.StoryAdapter;
import com.myjob.real_time_chat_final.api.PostService;
import com.myjob.real_time_chat_final.api.UserService;
import com.myjob.real_time_chat_final.config.WebSocketManager;
import com.myjob.real_time_chat_final.model.ImageResponse;
import com.myjob.real_time_chat_final.model.Story;
import com.myjob.real_time_chat_final.model.User;
import com.myjob.real_time_chat_final.modelDTO.PostRequestDTO;
import com.myjob.real_time_chat_final.modelDTO.PostResponseDTO;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFeedActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;
    private RecyclerView postsRecyclerView;
    private PostAdapter postAdapter;
    private List<PostResponseDTO> postList;

    private RecyclerView storiesRecyclerView;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView selectedImagePreview;
    private int userid = LoginActivity.userid;
    private Button statusInput;
    private WebSocketManager webSocketManager;
    private PostService postApiService;
    private ProgressDialog progressDialog;
    private UserService userService;
    private ShapeableImageView avatar; // Không cần ShapeableImageView vì user_avatar là ImageView
    private User currentUser;
    private ImageView userAvatar,storyImage; // Thêm biến để lưu ImageView user_avatar
    private TextView userName; // Thêm biến để lưu TextView user_name (nếu thêm vào layout)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_feed);
        Log.d("NewsFeedActivity", "Đã tải layout activity_news_feed");

        View mainView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Khởi tạo ProgressDialog để hiển thị thanh tải
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);

        // Khởi tạo PostApiService và UserService từ RetrofitClient
        postApiService = RetrofitClient.getApiPostService();
        userService = RetrofitClient.getApiUserService(); // Khởi tạo userService

        // Khởi tạo WebSocket để nhận thông báo bình luận hoặc tin nhắn
        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.connect();

        // Đăng ký lắng nghe bình luận mới qua WebSocket
        webSocketManager.subscribeToTopic("/topic/comments", message -> {
            runOnUiThread(() -> {
                Toast.makeText(NewsFeedActivity.this, "Có bình luận mới: " + message, Toast.LENGTH_SHORT).show();
            });
        });

        // Đăng ký lắng nghe tin nhắn mới qua WebSocket
        webSocketManager.subscribeToTopic("/topic/messages", message -> {
            runOnUiThread(() -> {
                Toast.makeText(NewsFeedActivity.this, "Có tin nhắn mới: " + message, Toast.LENGTH_SHORT).show();
            });
        });

        // Đăng ký imagePickerLauncher để chọn ảnh
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                Log.d("NewsFeedActivity", "Đã chọn ảnh URI: " + selectedImageUri);
                if (selectedImagePreview != null) {
                    selectedImagePreview.setVisibility(View.VISIBLE);
                    Glide.with(this).load(selectedImageUri).centerCrop().into(selectedImagePreview);
                }
            } else {
                Toast.makeText(this, "Đã hủy chọn ảnh", Toast.LENGTH_SHORT).show();
                Log.d("NewsFeedActivity", "Đã hủy chọn ảnh");
            }
        });

        // Khởi tạo RecyclerView cho Stories
        storiesRecyclerView = findViewById(R.id.stories_recycler_view);
        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        storiesRecyclerView.setHasFixedSize(true);

        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(storyList);
        storiesRecyclerView.setAdapter(storyAdapter);

        // Khởi tạo RecyclerView cho bài đăng
        postsRecyclerView = findViewById(R.id.posts_recycler_view);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postsRecyclerView.setHasFixedSize(true);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        postsRecyclerView.setAdapter(postAdapter);

        // Khởi tạo nút "Bạn đang nghĩ gì?" và thêm sự kiện click
        statusInput = findViewById(R.id.status_input);
        if (statusInput != null) {
            Log.d("NewsFeedActivity", "Đã khởi tạo status_input");
            statusInput.setOnClickListener(v -> {
                Log.d("NewsFeedActivity", "Đã nhấn status_input, mở dialog");
                showCreatePostDialog();
            });
        } else {
            Log.e("NewsFeedActivity", "Không tìm thấy status_input");
        }

        // Khởi tạo ImageView user_avatar và TextView user_name (nếu có)
        userAvatar = findViewById(R.id.user_avatar);
        storyImage = findViewById(R.id.add_story_image);
        // Gọi loadUserData để tải thông tin người dùng
        loadUserData(userid);
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
                    // Cập nhật avatar trong bảng tin
                    if (userAvatar != null) {
                        if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()) {
                            String baseUrl = RetrofitClient.getBaseUrl();
                            String fullAvatarUrl = baseUrl + currentUser.getAvatarUrl();
                            Log.d("NewsFeedActivity", "Loading avatar URL: " + fullAvatarUrl);
                            Glide.with(NewsFeedActivity.this)
                                    .load(fullAvatarUrl)
                                    .circleCrop()
                                    .error(R.drawable.ic_user)
                                    .into(userAvatar);
                            Glide.with(NewsFeedActivity.this)
                                    .load(fullAvatarUrl)
                                    .circleCrop()
                                    .error(R.drawable.ic_user)
                                    .into(storyImage);
                        } else {
                            Glide.with(NewsFeedActivity.this)
                                    .load(R.drawable.ic_user)
                                    .circleCrop()
                                    .into(userAvatar);
                        }
                    } else {
                        Log.e("NewsFeedActivity", "userAvatar is null");
                    }

                    // Cập nhật tên người dùng trong bảng tin (nếu có TextView user_name)
                    if (userName != null) {
                        userName.setText(currentUser.getUsername() != null ? currentUser.getUsername() : "Unknown User");
                    }
                } else {
                    Toast.makeText(NewsFeedActivity.this, "Không thể tải thông tin user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("NewsFeedActivity", "Lỗi tải thông tin user: " + t.getMessage(), t);
                Toast.makeText(NewsFeedActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCreatePostDialog() {
        try {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_create_post);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            Log.d("NewsFeedActivity", "Đã tải dialog create_post");

            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(dialog.getWindow().getAttributes());
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(params);

            ImageButton closeButton = dialog.findViewById(R.id.close_button);
            Button postButton = dialog.findViewById(R.id.post_button);
            EditText postInput = dialog.findViewById(R.id.post_input);
            Spinner privacySpinner = dialog.findViewById(R.id.privacy_spinner);
            LinearLayout photoOptionLayout = dialog.findViewById(R.id.photo_option_layout);
            LinearLayout tagOptionLayout = dialog.findViewById(R.id.tag_option_layout);
            LinearLayout feelingOptionLayout = dialog.findViewById(R.id.feeling_option_layout);
            LinearLayout checkinOptionLayout = dialog.findViewById(R.id.checkin_option_layout);
            LinearLayout liveOptionLayout = dialog.findViewById(R.id.live_option_layout);
            LinearLayout backgroundOptionLayout = dialog.findViewById(R.id.background_option_layout);
            TextView userName = dialog.findViewById(R.id.dialog_user_name);
            ImageView userAvatar = dialog.findViewById(R.id.dialog_user_avatar);
            selectedImagePreview = dialog.findViewById(R.id.selected_image_preview);

            selectedImageUri = null;
            if (selectedImagePreview != null) {
                selectedImagePreview.setVisibility(View.GONE);
            }

            // Cập nhật avatar và tên trong dialog từ currentUser
            if (currentUser != null) {
                if (userAvatar != null) {
                    if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()) {
                        String baseUrl = RetrofitClient.getBaseUrl();
                        String fullAvatarUrl = baseUrl + currentUser.getAvatarUrl();
                        Log.d("NewsFeedActivity", "Loading dialog avatar URL: " + fullAvatarUrl);
                        Glide.with(this)
                                .load(fullAvatarUrl)
                                .circleCrop()
                                .error(R.drawable.ic_user)
                                .into(userAvatar);
                    } else {
                        Glide.with(this)
                                .load(R.drawable.ic_user)
                                .circleCrop()
                                .into(userAvatar);
                    }
                }
                if (userName != null) {
                    userName.setText(currentUser.getUsername() != null ? currentUser.getUsername() : "Unknown User");
                }
            } else {
                Log.e("NewsFeedActivity", "currentUser is null, cannot update dialog avatar and name");
                // Đặt giá trị mặc định nếu currentUser chưa được tải
                if (userAvatar != null) {
                    Glide.with(this)
                            .load(R.drawable.ic_user)
                            .circleCrop()
                            .into(userAvatar);
                }
                if (userName != null) {
                    userName.setText("Unknown User");
                }
            }

            if (closeButton != null) {
                closeButton.setOnClickListener(v -> {
                    Log.d("NewsFeedActivity", "Đã nhấn nút đóng");
                    dialog.dismiss();
                });
            } else {
                Log.e("NewsFeedActivity", "Không tìm thấy close_button");
            }

            if (privacySpinner != null) {
                String[] privacyOptions = {"PUBLIC", "FRIENDS", "PRIVATE"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        privacyOptions
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                privacySpinner.setAdapter(adapter);
                Log.d("NewsFeedActivity", "Đã khởi tạo privacy_spinner");
            } else {
                Log.e("NewsFeedActivity", "Không tìm thấy privacy_spinner");
            }

            if (photoOptionLayout != null) {
                photoOptionLayout.setOnClickListener(v -> {
                    Log.d("NewsFeedActivity", "Đã nhấn photo_option_layout, yêu cầu quyền truy cập");
                    requestStoragePermission();
                });
            } else {
                Log.e("NewsFeedActivity", "Không tìm thấy photo_option_layout");
            }

            if (tagOptionLayout != null) {
                tagOptionLayout.setOnClickListener(v -> {
                    Toast.makeText(this, "Gắn thẻ người khác", Toast.LENGTH_SHORT).show();
                    Log.d("NewsFeedActivity", "Đã nhấn tag_option_layout");
                });
            } else {
                Log.e("NewsFeedActivity", "Không tìm thấy tag_option_layout");
            }

            if (feelingOptionLayout != null) {
                feelingOptionLayout.setOnClickListener(v -> {
                    Toast.makeText(this, "Thêm cảm xúc/hoạt động", Toast.LENGTH_SHORT).show();
                    Log.d("NewsFeedActivity", "Đã nhấn feeling_option_layout");
                });
            } else {
                Log.e("NewsFeedActivity", "Không tìm thấy feeling_option_layout");
            }

            if (checkinOptionLayout != null) {
                checkinOptionLayout.setOnClickListener(v -> {
                    Toast.makeText(this, "Check-in", Toast.LENGTH_SHORT).show();
                    Log.d("NewsFeedActivity", "Đã nhấn checkin_option_layout");
                });
            } else {
                Log.e("NewsFeedActivity", "Không tìm thấy checkin_option_layout");
            }

            if (liveOptionLayout != null) {
                liveOptionLayout.setOnClickListener(v -> {
                    Toast.makeText(this, "Bắt đầu video trực tiếp", Toast.LENGTH_SHORT).show();
                    Log.d("NewsFeedActivity", "Đã nhấn live_option_layout");
                });
            } else {
                Log.e("NewsFeedActivity", "Không tìm thấy live_option_layout");
            }

            if (backgroundOptionLayout != null) {
                backgroundOptionLayout.setOnClickListener(v -> {
                    Toast.makeText(this, "Thay đổi màu nền", Toast.LENGTH_SHORT).show();
                    Log.d("NewsFeedActivity", "Đã nhấn background_option_layout");
                });
            } else {
                Log.e("NewsFeedActivity", "Không tìm thấy background_option_layout");
            }

            if (postButton != null && postInput != null) {
                postButton.setOnClickListener(v -> {
                    String postContent = postInput.getText().toString().trim();
                    String selectedPrivacy = privacySpinner != null ? privacySpinner.getSelectedItem().toString() : "PUBLIC";
                    if (!postContent.isEmpty() || selectedImageUri != null) {
                        if (selectedImageUri != null) {
                            uploadImageForPost(postContent, selectedPrivacy, currentUser, dialog);
                        } else {
                            PostRequestDTO request = new PostRequestDTO();
                            request.setUserId(Long.valueOf(currentUser.getId()));
                            request.setContent(postContent);
                            request.setPrivacy(selectedPrivacy);
                            request.setImageUrl(null);

                            createPost(request, dialog);
                        }
                    } else {
                        Toast.makeText(this, "Vui lòng nhập nội dung hoặc chọn ảnh!", Toast.LENGTH_SHORT).show();
                        Log.d("NewsFeedActivity", "Nội dung bài đăng và ảnh đều trống");
                    }
                });
            } else {
                Log.e("NewsFeedActivity", "Không tìm thấy post_button hoặc post_input");
            }

            dialog.show();
            Log.d("NewsFeedActivity", "Đã hiển thị dialog");
        } catch (Exception e) {
            Log.e("NewsFeedActivity", "Lỗi khi hiển thị dialog: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khi mở dialog: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void uploadImageForPost(String postContent, String selectedPrivacy, User currentUser, Dialog dialog) {
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
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", "post_image.jpg", requestFile);

            progressDialog.show();

            postApiService.uploadImage(body).enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        String imageUrl = response.body().getImageUrl();
                        Toast.makeText(NewsFeedActivity.this, "Tải ảnh thành công", Toast.LENGTH_SHORT).show();

                        PostRequestDTO request = new PostRequestDTO();
                        request.setUserId(Long.valueOf(currentUser.getId()));
                        request.setContent(postContent);
                        request.setPrivacy(selectedPrivacy);
                        request.setImageUrl(imageUrl);

                        createPost(request, dialog);
                    } else {
                        String errorMessage = "Tải ảnh thất bại";
                        if (response.code() == 400) {
                            errorMessage = "Dữ liệu không hợp lệ";
                        } else if (response.code() == 500) {
                            errorMessage = "Lỗi server";
                        }
                        Toast.makeText(NewsFeedActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("NewsFeedActivity", "Lỗi tải ảnh: " + t.getMessage(), t);
                    String errorMessage = t instanceof IOException ? "Lỗi kết nối mạng. Vui lòng kiểm tra Internet." : "Lỗi tải ảnh: " + t.getMessage();
                    Toast.makeText(NewsFeedActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
            Log.e("NewsFeedActivity", "Lỗi xử lý ảnh: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createPost(PostRequestDTO request, Dialog dialog) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        postApiService.createPost(request).enqueue(new Callback<PostResponseDTO>() {
            @Override
            public void onResponse(Call<PostResponseDTO> call, Response<PostResponseDTO> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    PostResponseDTO newPost = response.body();
                    postList.add(0, newPost);
                    postAdapter.notifyItemInserted(0);
                    postsRecyclerView.scrollToPosition(0);
                    Toast.makeText(NewsFeedActivity.this, "Bài đăng đã được tạo!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    String errorMessage;
                    if (response.code() == 400) {
                        errorMessage = "Dữ liệu không hợp lệ";
                    } else if (response.code() == 404) {
                        errorMessage = "Không tìm thấy user";
                    } else if (response.code() == 500) {
                        errorMessage = "Lỗi server: " + (response.errorBody() != null ? response.errorBody().toString() : "Không xác định");
                    } else {
                        errorMessage = "Không thể tạo bài đăng: " + response.code();
                    }
                    Toast.makeText(NewsFeedActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("NewsFeedActivity", "Lỗi tạo bài đăng: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<PostResponseDTO> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("NewsFeedActivity", "Lỗi tạo bài đăng: " + t.getMessage(), t);
                String errorMessage = t instanceof IOException ? "Lỗi kết nối mạng. Vui lòng kiểm tra Internet." : "Lỗi tạo bài đăng: " + t.getMessage();
                Toast.makeText(NewsFeedActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void requestStoragePermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            Log.d("NewsFeedActivity", "Đã cấp quyền: " + permission);
            openGallery();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            Log.d("NewsFeedActivity", "Hiển thị lý do cần quyền");
            Toast.makeText(this, "Cần quyền truy cập thư viện để chọn ảnh", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{permission}, STORAGE_PERMISSION_CODE);
        } else {
            Log.d("NewsFeedActivity", "Yêu cầu quyền: " + permission);
            ActivityCompat.requestPermissions(this, new String[]{permission}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("NewsFeedActivity", "Đã cấp quyền, mở thư viện ảnh");
                openGallery();
            } else {
                String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                        Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    Log.d("NewsFeedActivity", "Quyền bị từ chối vĩnh viễn: " + permission);
                    Toast.makeText(this, "Quyền truy cập thư viện bị từ chối. Vui lòng cấp quyền trong cài đặt.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } else {
                    Log.d("NewsFeedActivity", "Quyền bị từ chối: " + permission);
                    Toast.makeText(this, "Cần quyền truy cập thư viện để chọn ảnh", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void openGallery() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            Log.d("NewsFeedActivity", "Mở thư viện ảnh với ACTION_PICK_IMAGES (API 33+)");
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            Log.d("NewsFeedActivity", "Mở thư viện ảnh với ACTION_PICK (API < 33)");
        }
        try {
            imagePickerLauncher.launch(intent);
        } catch (Exception e) {
            Log.e("NewsFeedActivity", "Lỗi khi mở thư viện ảnh: " + e.getMessage(), e);
            Toast.makeText(this, "Không thể mở thư viện ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
            Log.e("NewsFeedActivity", "Lỗi đọc dữ liệu ảnh: " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.disconnect();
    }
}