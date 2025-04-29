package com.myjob.real_time_chat_final.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.myjob.real_time_chat_final.OnCommentInteractionListener;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.PostAdapter;
import com.myjob.real_time_chat_final.adapter.SelectedImagesAdapter;
import com.myjob.real_time_chat_final.adapter.StoryAdapter;

import com.myjob.real_time_chat_final.api.CommentService;
import com.myjob.real_time_chat_final.api.PostService;
import com.myjob.real_time_chat_final.api.UserService;
import com.myjob.real_time_chat_final.config.WebSocketManager;
import com.myjob.real_time_chat_final.model.ImageResponse;
import com.myjob.real_time_chat_final.model.Story;
import com.myjob.real_time_chat_final.model.User;
import com.myjob.real_time_chat_final.modelDTO.CommentDTO;
import com.myjob.real_time_chat_final.modelDTO.CommentRequestDTO;
import com.myjob.real_time_chat_final.modelDTO.LikeNotificationDTO;
import com.myjob.real_time_chat_final.modelDTO.PostRequestDTO;
import com.myjob.real_time_chat_final.modelDTO.PostResponseDTO;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
    private Dialog createPostDialog; // Biến để lưu trữ dialog tạo bài post
    private RecyclerView storiesRecyclerView;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;
    private List<Uri> selectedImageUris;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView selectedImagePreview;
    private int userid = LoginActivity.userid;
    private Button statusInput;
    private WebSocketManager webSocketManager;
    private PostService postApiService;
    private CommentService commentApiService;
    private ProgressDialog progressDialog;
    private UserService userService;
    private User currentUser;
    private ImageView userAvatar;

    private int currentPage = 0;
    private int minPage = 0;
    private final int PAGE_SIZE = 10;
    private boolean isLoading = false;
    private boolean isLoadingOlderPosts = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_feed);
        Log.d("NewsFeedActivity", "Loaded activity_news_feed layout");

        View mainView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);

        postApiService = RetrofitClient.getApiPostService();
        commentApiService = RetrofitClient.getApiCommentService();
        userService = RetrofitClient.getApiUserService();

        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.connect();

        // Lắng nghe thông báo bình luận
        webSocketManager.subscribeToTopic("/topic/comments", message -> {
            runOnUiThread(() -> {
                try {
                    Gson gson = new Gson();
                    CommentDTO notification = gson.fromJson(message, CommentDTO.class);
                    // Bỏ qua nếu bình luận do chính người dùng hiện tại tạo
                    if (notification.getUserId() != null && notification.getUserId().equals((long) userid)) {
                        Log.d("NewsFeedActivity", "Ignoring self-created comment: " + notification.getId());
                        return;
                    }
                    for (int i = 0; i < postList.size(); i++) {
                        PostResponseDTO post = postList.get(i);
                        if (post.getId().equals(notification.getPostId())) {
                            if (post.getComments() == null) {
                                post.setComments(new ArrayList<>());
                            }
                            // Nếu là trả lời, luôn gắn vào bình luận cha
                            if (notification.getParentCommentId() != null) {
                                for (CommentDTO parent : post.getComments()) {
                                    if (parent.getId().equals(notification.getParentCommentId())) {
                                        if (parent.getReplies() == null) {
                                            parent.setReplies(new ArrayList<>());
                                        }
                                        // Kiểm tra trùng lặp
                                        if (!commentExists(parent.getReplies(), notification.getId())) {
                                            parent.getReplies().add(notification);
                                            sortComments(post.getComments());
                                            postAdapter.notifyItemChanged(i);
                                            Toast.makeText(NewsFeedActivity.this, notification.getUsername() + " commented: " + notification.getContent(), Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    }
                                }
                            } else {
                                // Kiểm tra trùng lặp
                                if (!commentExists(post.getComments(), notification.getId())) {
                                    post.getComments().add(notification);
                                    sortComments(post.getComments());
                                    postAdapter.notifyItemChanged(i);
                                    Toast.makeText(NewsFeedActivity.this, notification.getUsername() + " commented: " + notification.getContent(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    Log.e("NewsFeedActivity", "Error parsing comment notification: " + e.getMessage());
                }
            });
        });

        // Lắng nghe thông báo lượt thích
        webSocketManager.subscribeToTopic("/topic/likes", message -> {
            runOnUiThread(() -> {
                try {
                    Gson gson = new Gson();
                    LikeNotificationDTO notification = gson.fromJson(message, LikeNotificationDTO.class);
                    for (int i = 0; i < postList.size(); i++) {
                        PostResponseDTO post = postList.get(i);
                        if (post.getId().equals(notification.getPostId())) {
                            post.setLikeCount(post.getLikeCount() + 1);
                            post.setScore(calculatePostScore(post));
                            postAdapter.notifyItemChanged(i);
                            Toast.makeText(NewsFeedActivity.this, notification.getUsername() + " liked a post", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                } catch (Exception e) {
                    Log.e("NewsFeedActivity", "Error parsing like notification: " + e.getMessage());
                }
            });
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent data = result.getData();
                List<Uri> newImageUris = new ArrayList<>();
                int maxImages = 10; // Giới hạn tối đa 10 ảnh

                // Xử lý nhiều ảnh
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count && (selectedImageUris.size() + newImageUris.size()) < maxImages; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        newImageUris.add(imageUri);
                    }
                } else if (data.getData() != null && selectedImageUris.size() < maxImages) {
                    // Xử lý một ảnh
                    newImageUris.add(data.getData());
                }

                if (!newImageUris.isEmpty()) {
                    selectedImageUris.addAll(newImageUris);
                    Log.d("NewsFeedActivity", "Selected image URIs: " + selectedImageUris.size());

                    // Cập nhật RecyclerView trong dialog nếu đang mở
                    if (createPostDialog != null && createPostDialog.isShowing()) {
                        RecyclerView selectedImagesRecyclerView = createPostDialog.findViewById(R.id.selected_images_recycler_view);
                        if (selectedImagesRecyclerView != null) {
                            selectedImagesRecyclerView.setVisibility(View.VISIBLE);
                            SelectedImagesAdapter adapter = (SelectedImagesAdapter) selectedImagesRecyclerView.getAdapter();
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else if (selectedImageUris.size() >= maxImages) {
                    Toast.makeText(this, "Maximum 10 images allowed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No images selected", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Cancelled image selection", Toast.LENGTH_SHORT).show();
                Log.d("NewsFeedActivity", "Cancelled image selection");
            }
        });

        storiesRecyclerView = findViewById(R.id.stories_recycler_view);
        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        storiesRecyclerView.setHasFixedSize(true);

        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(storyList);
        storiesRecyclerView.setAdapter(storyAdapter);

        postsRecyclerView = findViewById(R.id.posts_recycler_view);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postsRecyclerView.setHasFixedSize(true);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(
                getSupportFragmentManager(),
                this::onLikeClick,
                this::onCommentClick,
                new OnCommentInteractionListener() {
                    @Override
                    public void onLikeComment(CommentDTO comment) {
                    }

                    @Override
                    public void onReplyComment(CommentDTO comment) {
                        showReplyDialog(comment);
                    }

                    @Override
                    public void onShareComment(CommentDTO comment) {
                    }
                }
        );
        postsRecyclerView.setAdapter(postAdapter);

        postsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + 2)) {
                    loadMorePosts();
                }

                if (!isLoadingOlderPosts && firstVisibleItem == 0 && dy < 0 && minPage > 0) {
                    loadOlderPosts();
                }
            }
        });

        statusInput = findViewById(R.id.status_input);
        if (statusInput != null) {
            Log.d("NewsFeedActivity", "Initialized status_input");
            statusInput.setOnClickListener(v -> {
                Log.d("NewsFeedActivity", "Clicked status_input, opening dialog");
                showCreatePostDialog();
            });
        } else {
            Log.e("NewsFeedActivity", "status_input not found");
        }

        userAvatar = findViewById(R.id.user_avatar);

        loadUserData(userid);
        loadPosts();
    }

    private boolean commentExists(List<CommentDTO> comments, Long commentId) {
        if (comments == null || commentId == null) return false;
        for (CommentDTO comment : comments) {
            if (comment.getId() != null && comment.getId().equals(commentId)) {
                return true;
            }
            if (comment.getReplies() != null && commentExists(comment.getReplies(), commentId)) {
                return true;
            }
        }
        return false;
    }

    private List<CommentDTO> getAllCommentsFlat(List<CommentDTO> comments) {
        List<CommentDTO> flatList = new ArrayList<>();
        if (comments != null) {
            for (CommentDTO comment : comments) {
                flatList.add(comment);
                if (comment.getReplies() != null) {
                    flatList.addAll(comment.getReplies());
                }
            }
        }
        return flatList;
    }

    private void sortComments(List<CommentDTO> comments) {
        if (comments != null) {
            Collections.sort(comments, (c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt()));
            for (CommentDTO comment : comments) {
                if (comment.getReplies() != null) {
                    Collections.sort(comment.getReplies(), (c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt()));
                }
            }
        }
    }

    private void onLikeClick(PostResponseDTO post) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        postApiService.likePost(post.getId(), (long) userid).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                } else {
                    Toast.makeText(NewsFeedActivity.this, "Failed to like post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("NewsFeedActivity", "Error liking post: " + t.getMessage());
                Toast.makeText(NewsFeedActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onCommentClick(PostResponseDTO post, String content, Long parentCommentId) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        CommentRequestDTO request = new CommentRequestDTO();
        request.setPostId(post.getId());
        request.setUserId((long) userid);
        request.setContent(content);
        request.setParentCommentId(parentCommentId);

        commentApiService.createComment(request).enqueue(new Callback<CommentDTO>() {
            @Override
            public void onResponse(Call<CommentDTO> call, Response<CommentDTO> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    CommentDTO newComment = response.body();
                    // Cập nhật danh sách bình luận cục bộ
                    for (int i = 0; i < postList.size(); i++) {
                        if (postList.get(i).getId().equals(post.getId())) {
                            PostResponseDTO targetPost = postList.get(i);
                            if (targetPost.getComments() == null) {
                                targetPost.setComments(new ArrayList<>());
                            }
                            if (parentCommentId != null) {
                                for (CommentDTO parent : targetPost.getComments()) {
                                    if (parent.getId().equals(parentCommentId)) {
                                        if (parent.getReplies() == null) {
                                            parent.setReplies(new ArrayList<>());
                                        }
                                        parent.getReplies().add(newComment);
                                        break;
                                    }
                                }
                            } else {
                                targetPost.getComments().add(newComment);
                            }
                            sortComments(targetPost.getComments());
                            postAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                } else {
                    Toast.makeText(NewsFeedActivity.this, "Failed to post comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentDTO> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("NewsFeedActivity", "Error posting comment: " + t.getMessage());
                Toast.makeText(NewsFeedActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showReplyDialog(CommentDTO comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reply to " + comment.getUsername());

        final EditText commentInput = new EditText(this);
        commentInput.setHint("Write a reply...");
        commentInput.setTextSize(14);
        commentInput.setPadding(16, 16, 16, 16);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 16, 16, 16);
        commentInput.setLayoutParams(params);
        builder.setView(commentInput);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String content = commentInput.getText().toString().trim();
            if (!content.isEmpty()) {
                for (PostResponseDTO post : postList) {
                    List<CommentDTO> allComments = getAllCommentsFlat(post.getComments());
                    if (allComments.contains(comment)) {
                        // Nếu comment là bình luận con, lấy parentCommentId của bình luận cha
                        Long parentId = comment.getParentCommentId() != null ? comment.getParentCommentId() : comment.getId();
                        onCommentClick(post, content, parentId);
                        dialog.dismiss();
                        break;
                    }
                }
            } else {
                Toast.makeText(this, "Reply cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        commentInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private double calculatePostScore(PostResponseDTO post) {
        long currentTime = System.currentTimeMillis();
        long createdAt = post.getCreatedAt().getTime();

        double timeScore = 1.0 / (1 + (currentTime - createdAt) / (1000 * 60 * 60));
        double likes = post.getLikeCount();
        double comments = (post.getComments() != null) ? post.getComments().size() : 0;
        double shares = 0;
        double engagementScore = (likes * 0.4) + (comments * 0.4) + (shares * 0.2);
        double affinityScore = 0.1;

        return (timeScore * 0.3) + (engagementScore * 0.5) + (affinityScore * 0.2);
    }

    private void sortPostsByScore() {
        Collections.sort(postList, (p1, p2) -> Double.compare(p2.getScore(), p1.getScore()));
    }

    private void loadUserData(int userId) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
            return;
        }
        userService.getUserById((long) userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUser = response.body();
                    if (userAvatar != null) {
                        if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()) {
                            String baseUrl = RetrofitClient.getBaseUrl();
                            String fullAvatarUrl = baseUrl + currentUser.getAvatarUrl();
                            Log.d("NewsFeedActivity", "Loading avatar URL: " + fullAvatarUrl);
                            if (fullAvatarUrl.contains("/uploads/")) {
                                Glide.with(NewsFeedActivity.this)
                                        .load(fullAvatarUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .circleCrop()
                                        .placeholder(R.drawable.ic_user)
                                        .error(R.drawable.ic_user)
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                                                Log.e("NewsFeedActivity", "Glide load failed for avatar: " + fullAvatarUrl, e);
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                                                Log.d("NewsFeedActivity", "Glide load successful for avatar: " + fullAvatarUrl);
                                                return false;
                                            }
                                        })
                                        .into(userAvatar);
                            } else {
                                Log.w("NewsFeedActivity", "Invalid avatar URL: " + fullAvatarUrl);
                                Glide.with(NewsFeedActivity.this)
                                        .load(R.drawable.ic_user)
                                        .circleCrop()
                                        .into(userAvatar);
                            }
                        } else {
                            Log.w("NewsFeedActivity", "Avatar URL is empty");
                            Glide.with(NewsFeedActivity.this)
                                    .load(R.drawable.ic_user)
                                    .circleCrop()
                                    .into(userAvatar);
                        }
                    } else {
                        Log.e("NewsFeedActivity", "userAvatar is null");
                    }
                } else {
                    Log.e("NewsFeedActivity", "Failed to load user info, code: " + response.code());
                    Toast.makeText(NewsFeedActivity.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("NewsFeedActivity", "Error loading user info: " + t.getMessage(), t);
                Toast.makeText(NewsFeedActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCreatePostDialog() {
        try {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_create_post);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            // Thiết lập kích thước dialog
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(dialog.getWindow().getAttributes());
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(params);

            // Lấy các view từ layout
            EditText postContent = dialog.findViewById(R.id.post_input);
            Spinner privacySpinner = dialog.findViewById(R.id.privacy_spinner);
            Button postButton = dialog.findViewById(R.id.post_button);
            ImageButton closeButton = dialog.findViewById(R.id.close_button);
            LinearLayout photoOptionLayout = dialog.findViewById(R.id.photo_option_layout);
            RecyclerView selectedImagesRecyclerView = dialog.findViewById(R.id.selected_images_recycler_view);
            LinearLayout tagOptionLayout = dialog.findViewById(R.id.tag_option_layout);
            LinearLayout feelingOptionLayout = dialog.findViewById(R.id.feeling_option_layout);
            LinearLayout checkinOptionLayout = dialog.findViewById(R.id.checkin_option_layout);
            LinearLayout liveOptionLayout = dialog.findViewById(R.id.live_option_layout);
            LinearLayout backgroundOptionLayout = dialog.findViewById(R.id.background_option_layout);
            ImageView userAvatar = dialog.findViewById(R.id.dialog_user_avatar);
            TextView userName = dialog.findViewById(R.id.dialog_user_name);
            ProgressBar uploadProgress = dialog.findViewById(R.id.upload_progress);

            // Kiểm tra null cho các view
            if (postContent == null || privacySpinner == null || postButton == null || closeButton == null ||
                    photoOptionLayout == null || selectedImagesRecyclerView == null ||
                    tagOptionLayout == null || feelingOptionLayout == null ||
                    checkinOptionLayout == null || liveOptionLayout == null ||
                    backgroundOptionLayout == null || userAvatar == null || userName == null || uploadProgress == null) {
                Log.e("NewsFeedActivity", "Một hoặc nhiều view trong dialog không được tìm thấy");
                Toast.makeText(this, "Lỗi tải giao diện dialog", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hiển thị thông tin người dùng
            if (currentUser != null) {
                userName.setText(currentUser.getUsername() != null ? currentUser.getUsername() : "Người dùng");
                String avataUrl = RetrofitClient.getBaseUrl() + currentUser.getAvatarUrl();
                Glide.with(this)
                        .load(avataUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(userAvatar);
            } else {
                Log.w("NewsFeedActivity", "currentUser là null, hiển thị giá trị mặc định");
                userName.setText("Người dùng");
                Glide.with(this)
                        .load(R.drawable.ic_user)
                        .circleCrop()
                        .into(userAvatar);
                Toast.makeText(this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            }

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.privacy_options,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            privacySpinner.setAdapter(adapter);

            // Khởi tạo RecyclerView cho ảnh preview
            if (selectedImageUris == null) {
                selectedImageUris = new ArrayList<>();
            }

            // Khởi tạo adapter mà không sử dụng imagesAdapter trong lambda
            SelectedImagesAdapter imagesAdapter = new SelectedImagesAdapter(this, selectedImageUris, position -> {
                selectedImageUris.remove(position);
                // Cập nhật RecyclerView trong dialog
                RecyclerView recyclerView = dialog.findViewById(R.id.selected_images_recycler_view);
                if (recyclerView != null) {
                    SelectedImagesAdapter adapterInstance = (SelectedImagesAdapter) recyclerView.getAdapter();
                    if (adapterInstance != null) {
                        adapterInstance.notifyItemRemoved(position);
                        if (selectedImageUris.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                }
            });

            selectedImagesRecyclerView.setAdapter(imagesAdapter);
            selectedImagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            selectedImagesRecyclerView.setVisibility(selectedImageUris.isEmpty() ? View.GONE : View.VISIBLE);

            photoOptionLayout.setOnClickListener(v -> {
                requestStoragePermission();
            });

            tagOptionLayout.setOnClickListener(v -> {
                Toast.makeText(this, "Tag people clicked", Toast.LENGTH_SHORT).show();
            });

            feelingOptionLayout.setOnClickListener(v -> {
                Toast.makeText(this, "Feeling/activity clicked", Toast.LENGTH_SHORT).show();
            });

            checkinOptionLayout.setOnClickListener(v -> {
                Toast.makeText(this, "Check in clicked", Toast.LENGTH_SHORT).show();
            });

            liveOptionLayout.setOnClickListener(v -> {
                Toast.makeText(this, "Live video clicked", Toast.LENGTH_SHORT).show();
            });

            backgroundOptionLayout.setOnClickListener(v -> {
                Toast.makeText(this, "Background color clicked", Toast.LENGTH_SHORT).show();
            });

            closeButton.setOnClickListener(v -> {
                selectedImageUris.clear();
                dialog.dismiss();
                createPostDialog = null;
            });

            postButton.setOnClickListener(v -> {
                String content = postContent.getText().toString().trim();
                String selectedPrivacy = privacySpinner.getSelectedItem().toString();

                String mappedPrivacy;
                switch (selectedPrivacy) {
                    case "Public":
                        mappedPrivacy = "PUBLIC";
                        break;
                    case "Friends":
                        mappedPrivacy = "FRIENDS";
                        break;
                    case "Private":
                        mappedPrivacy = "PRIVATE";
                        break;
                    default:
                        Toast.makeText(this, "Invalid privacy option", Toast.LENGTH_SHORT).show();
                        return;
                }

                if (content.isEmpty() && (selectedImageUris == null || selectedImageUris.isEmpty())) {
                    Toast.makeText(this, "Post content or image cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (currentUser == null) {
                    Toast.makeText(this, "User data not loaded", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show();
                if (selectedImageUris != null && !selectedImageUris.isEmpty()) {
                    Log.e("sai","dd");
                    uploadImagesForPost(content, mappedPrivacy, currentUser, dialog);
                } else {
                    Log.e("đúng","dd");
                    PostRequestDTO request = new PostRequestDTO();
                    request.setContent(content);
                    request.setUserId((long) userid);
                    request.setPrivacy(mappedPrivacy);
                    request.setImageUrls(null); // Không có ảnh
                    createPost(request, dialog);
                }
            });

            createPostDialog = dialog;
            dialog.show();
        } catch (Exception e) {
            Log.e("NewsFeedActivity", "Error showing dialog: " + e.getMessage(), e);
            Toast.makeText(this, "Error opening dialog", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImagesForPost(String postContent, String selectedPrivacy, User currentUser, Dialog dialog) {
        if (!isNetworkAvailable()) {
            progressDialog.dismiss();
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressBar uploadProgress = dialog.findViewById(R.id.upload_progress);
        if (uploadProgress != null) {
            uploadProgress.setVisibility(View.VISIBLE);
        }

        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (Uri imageUri : selectedImageUris) {
            byte[] imageBytes = getBytesFromUri(imageUri);
            if (imageBytes != null) {
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("file", "post_image_" + System.currentTimeMillis() + ".jpg", imageBody);
                imageParts.add(imagePart);
            }
        }

        if (imageParts.isEmpty()) {
            progressDialog.dismiss();
            if (uploadProgress != null) {
                uploadProgress.setVisibility(View.GONE);
            }
            Toast.makeText(this, "Failed to process images", Toast.LENGTH_SHORT).show();
            return;
        }

        postApiService.uploadImages(imageParts).enqueue(new Callback<List<ImageResponse>>() {
            @Override
            public void onResponse(Call<List<ImageResponse>> call, Response<List<ImageResponse>> response) {
                Log.d("NewsFeedActivity", "Request URL: " + call.request().url());
                progressDialog.dismiss();
                if (uploadProgress != null) {
                    uploadProgress.setVisibility(View.GONE);
                }
                if (response.isSuccessful() && response.body() != null) {
                    List<String> imageUrls = response.body().stream()
                            .filter(img -> img.getImageUrl() != null)
                            .map(img -> {
                                // Nối BASE_URL vào URL tương đối
                                String relativeUrl = img.getImageUrl();
                                return relativeUrl;
                            })
                            .collect(Collectors.toList());
                    if (imageUrls.isEmpty()) {
                        Toast.makeText(NewsFeedActivity.this, "No images uploaded", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    PostRequestDTO request = new PostRequestDTO();
                    request.setContent(postContent);
                    request.setUserId((long) userid);
                    request.setPrivacy(selectedPrivacy);
                    request.setImageUrls(imageUrls);
                    createPost(request, dialog);
                } else {
                    String errorMessage = response.body() != null && !response.body().isEmpty() && response.body().get(0).getError() != null
                            ? response.body().get(0).getError()
                            : "Failed to upload images, code: " + response.code();
                    Log.e("NewsFeedActivity", errorMessage);
                    Toast.makeText(NewsFeedActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ImageResponse>> call, Throwable t) {
                Log.e("NewsFeedActivity", "Error uploading images, URL: " + call.request().url() + ", message: " + t.getMessage(), t);
                progressDialog.dismiss();
                if (uploadProgress != null) {
                    uploadProgress.setVisibility(View.GONE);
                }
                Toast.makeText(NewsFeedActivity.this, "Error uploading images: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private byte[] getBytesFromUri(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] bytes = stream.toByteArray();
            Log.d("NewsFeedActivity", "Compressed image size: " + bytes.length + " bytes");
            bitmap.recycle();
            stream.close();
            return bytes;
        } catch (IOException e) {
            Log.e("NewsFeedActivity", "Error reading image: " + e.getMessage(), e);
            return null;
        }
    }

    private void checkUploadCompletion(int[] uploadCount, int totalImages, boolean[] hasError, List<String> imageUrls, String postContent, String selectedPrivacy, Dialog dialog) {
        if (uploadCount[0] == totalImages) {
            runOnUiThread(() -> {
                ProgressBar uploadProgress = dialog.findViewById(R.id.upload_progress);
                if (uploadProgress != null) {
                    uploadProgress.setVisibility(View.GONE);
                }
                progressDialog.dismiss();
                if (hasError[0] && imageUrls.isEmpty()) {
                    Toast.makeText(NewsFeedActivity.this, "Failed to upload images. Please try again.", Toast.LENGTH_LONG).show();
                } else {
                    PostRequestDTO request = new PostRequestDTO();
                    request.setContent(postContent);
                    request.setUserId((long) userid);
                    request.setPrivacy(selectedPrivacy);
                    request.setImageUrls(imageUrls.isEmpty() ? null : imageUrls); // Sử dụng setImageUrls
                    createPost(request, dialog);
                }
            });
        }
    }

    private void createPost(PostRequestDTO request, Dialog dialog) {
        if (!isNetworkAvailable()) {
            progressDialog.dismiss();
            ProgressBar uploadProgress = dialog.findViewById(R.id.upload_progress);
            if (uploadProgress != null) {
                uploadProgress.setVisibility(View.GONE);
            }
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
            return;
        }

        postApiService.createPost(request).enqueue(new Callback<PostResponseDTO>() {
            @Override
            public void onResponse(Call<PostResponseDTO> call, Response<PostResponseDTO> response) {
                progressDialog.dismiss();
                ProgressBar uploadProgress = dialog.findViewById(R.id.upload_progress);
                if (uploadProgress != null) {
                    uploadProgress.setVisibility(View.GONE);
                }
                if (response.isSuccessful() && response.body() != null) {
                    PostResponseDTO newPost = response.body();
                    newPost.setScore(calculatePostScore(newPost));
                    postAdapter.addNewPost(newPost);
                    postsRecyclerView.scrollToPosition(0);
                    dialog.dismiss();
                    createPostDialog = null;
                    if (selectedImageUris != null) {
                        selectedImageUris.clear();
                    }
                    Toast.makeText(NewsFeedActivity.this, "Tạo bài đăng thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewsFeedActivity.this, "Không thể tạo bài đăng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponseDTO> call, Throwable t) {
                progressDialog.dismiss();
                ProgressBar uploadProgress = dialog.findViewById(R.id.upload_progress);
                if (uploadProgress != null) {
                    uploadProgress.setVisibility(View.GONE);
                }
                Log.e("NewsFeedActivity", "Lỗi tạo bài đăng: " + t.getMessage());
                Toast.makeText(NewsFeedActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Cho phép chọn nhiều ảnh
        imagePickerLauncher.launch(intent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    private void loadPosts() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("NewsFeedActivity", "Loading posts, page: " + currentPage);
        isLoading = true;
        postApiService.getPosts(currentPage, PAGE_SIZE).enqueue(new Callback<List<PostResponseDTO>>() {
            @Override
            public void onResponse(Call<List<PostResponseDTO>> call, Response<List<PostResponseDTO>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("NewsFeedActivity", "Posts loaded, size: " + response.body().size());
                    postList.addAll(response.body());
                    for (PostResponseDTO post : postList) {
                        post.setScore(calculatePostScore(post));
                        Log.d("NewsFeedActivity", "Post " + post.getId() + " avatarUrl: " + post.getAvatarUrl());
                        Log.d("NewsFeedActivity", "Post " + post.getId() + " imageUrl: " + post.getImageUrl());
                        if (post.getAvatarUrl() != null && !post.getAvatarUrl().contains("/uploads/")) {
                            Log.w("NewsFeedActivity", "Invalid avatar URL for post " + post.getId() + ": " + post.getAvatarUrl());
                        }
                        if (post.getImageUrl() != null) {
                            for (String imageUrl : post.getImageUrl()) {
                                if (!imageUrl.contains("/uploads/")) {
                                    Log.w("NewsFeedActivity", "Invalid image URL for post " + post.getId() + ": " + imageUrl);
                                }
                            }
                        }
                    }
                    sortPostsByScore();
                    postAdapter.updatePosts(postList);
                    currentPage++;
                } else {
                    Log.e("NewsFeedActivity", "Failed to load posts, code: " + response.code());
                    Toast.makeText(NewsFeedActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PostResponseDTO>> call, Throwable t) {
                isLoading = false;
                Log.e("NewsFeedActivity", "Error loading posts: " + t.getMessage());
                Toast.makeText(NewsFeedActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMorePosts() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("NewsFeedActivity", "Loading more posts, page: " + currentPage);
        isLoading = true;
        postApiService.getPosts(currentPage, PAGE_SIZE).enqueue(new Callback<List<PostResponseDTO>>() {
            @Override
            public void onResponse(Call<List<PostResponseDTO>> call, Response<List<PostResponseDTO>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("NewsFeedActivity", "More posts loaded, size: " + response.body().size());
                    postList.addAll(response.body());
                    for (PostResponseDTO post : response.body()) {
                        post.setScore(calculatePostScore(post));
                    }
                    sortPostsByScore();
                    postAdapter.updatePosts(postList);
                    currentPage++;
                }
            }

            @Override
            public void onFailure(Call<List<PostResponseDTO>> call, Throwable t) {
                isLoading = false;
                Log.e("NewsFeedActivity", "Error loading more posts: " + t.getMessage());
                Toast.makeText(NewsFeedActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOlderPosts() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
            return;
        }

        isLoadingOlderPosts = true;
        int olderPage = minPage - 1;
        if (olderPage < 0) {
            isLoadingOlderPosts = false;
            return;
        }

        Log.d("NewsFeedActivity", "Loading older posts, page: " + olderPage);
        postApiService.getPosts(olderPage, PAGE_SIZE).enqueue(new Callback<List<PostResponseDTO>>() {
            @Override
            public void onResponse(Call<List<PostResponseDTO>> call, Response<List<PostResponseDTO>> response) {
                isLoadingOlderPosts = false;
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("NewsFeedActivity", "Older posts loaded, size: " + response.body().size());
                    postList.addAll(0, response.body());
                    for (PostResponseDTO post : response.body()) {
                        post.setScore(calculatePostScore(post));
                    }
                    sortPostsByScore();
                    postAdapter.addOlderPosts(response.body());
                    minPage = olderPage;
                }
            }

            @Override
            public void onFailure(Call<List<PostResponseDTO>> call, Throwable t) {
                isLoadingOlderPosts = false;
                Log.e("NewsFeedActivity", "Error loading older posts: " + t.getMessage());
                Toast.makeText(NewsFeedActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.disconnect();
    }
}