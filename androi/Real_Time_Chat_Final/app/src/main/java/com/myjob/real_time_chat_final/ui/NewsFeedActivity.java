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
import android.view.ViewGroup;
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
import com.google.gson.GsonBuilder;
import com.myjob.real_time_chat_final.OnCommentInteractionListener;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.NotificationAdapter;
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
import com.myjob.real_time_chat_final.modelDTO.NotificationDTO;
import com.myjob.real_time_chat_final.modelDTO.PostRequestDTO;
import com.myjob.real_time_chat_final.modelDTO.PostResponseDTO;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFeedActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;
    private RecyclerView postsRecyclerView;
    private PostAdapter postAdapter;
    private List<PostResponseDTO> postList;
    private Dialog createPostDialog;
    private RecyclerView storiesRecyclerView;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;
    private List<Uri> selectedImageUris;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private int userid = LoginActivity.userid;
    private Button statusInput;
    private WebSocketManager webSocketManager;
    private PostService postApiService;
    private CommentService commentApiService;
    private ProgressDialog progressDialog;
    private UserService userService;
    private User currentUser;
    private ImageView userAvatar;
    private TextView notificationBadge;
    private ImageButton notificationIcon;
    private List<NotificationDTO> notifications = new ArrayList<>();
    private NotificationAdapter notificationAdapter;
    private Dialog notificationDialog;
    private int currentPage = 0;
    private int minPage = 0;
    private final int PAGE_SIZE = 10;
    private boolean isLoading = false;
    private boolean isLoadingOlderPosts = false;
    private String currentUserAvatarUrl;

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

        notificationIcon = findViewById(R.id.notification_icon);
        notificationBadge = findViewById(R.id.notification_badge);
        notificationIcon.setOnClickListener(v -> showNotificationDialog());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);

        postApiService = RetrofitClient.getApiPostService();
        commentApiService = RetrofitClient.getApiCommentService();
        userService = RetrofitClient.getApiUserService();

        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.connect();

        // Subscribe to notifications
        webSocketManager.subscribeToTopic("/topic/notifications/" + userid, message -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    Log.d("NewsFeedActivity", "Received WebSocket notification: " + message);
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();
                    NotificationDTO notification = gson.fromJson(message, NotificationDTO.class);

                    // Validate notification
                    if (notification.getUserId() == null || notification.getType() == null || notification.getContent() == null) {
                        Log.w("NewsFeedActivity", "Invalid notification: " + message);
                        return;
                    }

                    // Ignore notifications from self
                    if (notification.getActorId() != null && notification.getActorId().equals((long) userid)) {
                        Log.d("NewsFeedActivity", "Ignoring self-notification: " + notification.getContent());
                        return;
                    }

                    runOnUiThread(() -> {
                        // Add notification to list (newest first)
                        notifications.add(0, notification);
                        // Update badge if unread
                        if (!notification.isRead()) {
                            int unreadCount = getUnreadCount();
                            updateNotificationBadge(unreadCount);
                            Toast.makeText(NewsFeedActivity.this, notification.getContent(), Toast.LENGTH_SHORT).show();
                        }
                        // Update dialog if open
                        if (notificationDialog != null && notificationDialog.isShowing() && notificationAdapter != null) {
                            notificationAdapter.updateNotifications(notifications);
                        }
                    });
                } catch (Exception e) {
                    Log.e("NewsFeedActivity", "Error parsing notification: " + message, e);
                    runOnUiThread(() -> Toast.makeText(NewsFeedActivity.this, "Error processing notification", Toast.LENGTH_SHORT).show());
                } finally {
                    executor.shutdown();
                }
            });
        });

        // Subscribe to comments
        webSocketManager.subscribeToTopic("/topic/comments", message -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    Log.d("NewsFeedActivity", "Received WebSocket message: " + message);
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();
                    CommentDTO notification = gson.fromJson(message, CommentDTO.class);

                    if (notification.getPostId() == null) {
                        Log.w("NewsFeedActivity", "Invalid comment notification: Missing postId: " + message);
                        return;
                    }
                    if (notification.getId() == null) {
                        Log.w("NewsFeedActivity", "Invalid comment notification: Missing commentId: " + message);
                        return;
                    }
                    if (notification.getContent() == null || notification.getContent().isEmpty()) {
                        Log.w("NewsFeedActivity", "Invalid comment notification: Empty content: " + message);
                        return;
                    }
                    if (notification.getCreatedAt() == null) {
                        Log.w("NewsFeedActivity", "Invalid comment notification: Missing createdAt: " + message);
                        return;
                    }
                    if (notification.getUserId() != null && notification.getUserId().equals((long) userid)) {
                        Log.d("NewsFeedActivity", "Ignoring self-comment: commentId=" + notification.getId() + ", content=" + notification.getContent());
                        return;
                    }

                    if (notification.getReplies() == null) {
                        notification.setReplies(new ArrayList<>());
                    }

                    runOnUiThread(() -> {
                        for (int i = 0; i < postList.size(); i++) {
                            PostResponseDTO post = postList.get(i);
                            if (post.getId().equals(notification.getPostId())) {
                                if (post.getComments() == null) {
                                    post.setComments(new ArrayList<>());
                                }
                                if (notification.getParentCommentId() != null) {
                                    CommentDTO parent = findParentComment(post.getComments(), notification.getParentCommentId());
                                    if (parent != null) {
                                        if (parent.getReplies() == null) {
                                            parent.setReplies(new ArrayList<>());
                                        }
                                        if (!commentExists(parent.getReplies(), notification.getId())) {
                                            Log.d("NewsFeedActivity", "Adding reply to parent: commentId=" + notification.getId() + ", parentId=" + notification.getParentCommentId());
                                            parent.getReplies().add(notification);
                                            sortComments(post.getComments());
                                            postAdapter.notifyItemChanged(i);
                                            Toast.makeText(NewsFeedActivity.this, notification.getUsername() + " đã trả lời: " + notification.getContent(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.d("NewsFeedActivity", "Reply already exists: commentId=" + notification.getId());
                                        }
                                    } else {
                                        Log.w("NewsFeedActivity", "Parent comment not found: parentId=" + notification.getParentCommentId());
                                    }
                                } else {
                                    if (!commentExists(post.getComments(), notification.getId())) {
                                        Log.d("NewsFeedActivity", "Adding comment: commentId=" + notification.getId());
                                        post.getComments().add(notification);
                                        sortComments(post.getComments());
                                        postAdapter.notifyItemChanged(i);
                                        Toast.makeText(NewsFeedActivity.this, notification.getUsername() + " đã bình luận: " + notification.getContent(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.d("NewsFeedActivity", "Comment already exists: commentId=" + notification.getId());
                                    }
                                }
                                break;
                            }
                        }
                        if (!postList.stream().anyMatch(p -> p.getId().equals(notification.getPostId()))) {
                            Log.w("NewsFeedActivity", "Post not found for comment notification: postId=" + notification.getPostId());
                        }
                    });
                } catch (Exception e) {
                    Log.e("NewsFeedActivity", "Error parsing comment notification: " + message, e);
                } finally {
                    executor.shutdown();
                }
            });
        });

        // Subscribe to likes
        webSocketManager.subscribeToTopic("/topic/likes", message -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                            .create();
                    LikeNotificationDTO notification = gson.fromJson(message, LikeNotificationDTO.class);
                    if (notification.getPostId() == null || notification.getAction() == null) {
                        Log.w("NewsFeedActivity", "Invalid like notification: " + message);
                        return;
                    }
                    // Bỏ qua thông báo từ chính người dùng hiện tại
                    if (notification.getUserId().equals((long) userid)) {
                        Log.d("NewsFeedActivity", "Ignoring self-like notification: postId=" + notification.getPostId() + ", action=" + notification.getAction());
                        return;
                    }
                    runOnUiThread(() -> {
                        for (int i = 0; i < postList.size(); i++) {
                            PostResponseDTO post = postList.get(i);
                            if (post.getId().equals(notification.getPostId())) {
                                int currentLikeCount = post.getLikeCount() != 0 ? post.getLikeCount() : 0;
                                if (notification.getAction().equals("LIKED")) {
                                    post.setLikeCount(currentLikeCount + 1);
                                } else if (notification.getAction().equals("UNLIKED")) {
                                    post.setLikeCount(Math.max(0, currentLikeCount - 1));
                                }
                                post.setScore(calculatePostScore(post));
                                postAdapter.notifyItemChanged(i);
                                Toast.makeText(NewsFeedActivity.this, notification.getUsername() + " " +
                                        (notification.getAction().equals("LIKED") ? "liked" : "unliked") + " the post", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e("NewsFeedActivity", "Error parsing like notification: " + message, e);
                } finally {
                    executor.shutdown();
                }
            });
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent data = result.getData();
                List<Uri> newImageUris = new ArrayList<>();
                int maxImages = 10;

                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count && (selectedImageUris.size() + newImageUris.size()) < maxImages; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        newImageUris.add(imageUri);
                    }
                } else if (data.getData() != null && selectedImageUris.size() < maxImages) {
                    newImageUris.add(data.getData());
                }

                if (!newImageUris.isEmpty()) {
                    selectedImageUris.addAll(newImageUris);
                    Log.d("NewsFeedActivity", "Selected image URIs: " + selectedImageUris.size());

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
                    public void onLikeComment(CommentDTO comment) {}
                    @Override
                    public void onReplyComment(CommentDTO comment) {
                        showReplyDialog(comment);
                    }
                    @Override
                    public void onShareComment(CommentDTO comment) {}
                }, currentUserAvatarUrl
        );
        postAdapter.updatePosts(postList);
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
        loadNotifications();
    }

    private CommentDTO findParentComment(List<CommentDTO> comments, Long parentCommentId) {
        if (comments == null || parentCommentId == null) {
            return null;
        }
        for (CommentDTO comment : comments) {
            if (comment.getId() != null && comment.getId().equals(parentCommentId)) {
                return comment;
            }
            if (comment.getReplies() != null) {
                CommentDTO found = findParentComment(comment.getReplies(), parentCommentId);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
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
                    flatList.addAll(getAllCommentsFlat(comment.getReplies()));
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

        postApiService.likePost(post.getId(), (long) userid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String status = response.body().string().trim();
                        Log.d("NewsFeedActivity", "Like status for post " + post.getId() + ": " + status);
                        for (int i = 0; i < postList.size(); i++) {
                            if (postList.get(i).getId().equals(post.getId())) {
                                PostResponseDTO targetPost = postList.get(i);
                                if (status.equals("LIKED")) {
                                    targetPost.setLikedByUser(true);
                                    targetPost.setLikeCount(targetPost.getLikeCount() != 0 ? targetPost.getLikeCount() + 1 : 1);
                                    if (!targetPost.getUserId().equals((long) userid)) {
                                        createNotification(
                                                targetPost.getUserId(),
                                                (long) userid,
                                                post.getId(),
                                                "LIKE",
                                                currentUser.getUsername() + " liked your post",
                                                currentUserAvatarUrl
                                        );
                                    }
                                } else if (status.equals("UNLIKED")) {
                                    targetPost.setLikedByUser(false);
                                    targetPost.setLikeCount(targetPost.getLikeCount() != 0 ? Math.max(0, targetPost.getLikeCount() - 1) : 0);
                                }
                                targetPost.setScore(calculatePostScore(targetPost));
                                postAdapter.notifyItemChanged(i);
                                break;
                            }
                        }
                    } catch (IOException e) {
                        Log.e("NewsFeedActivity", "Error reading response body: " + e.getMessage(), e);
                        Toast.makeText(NewsFeedActivity.this, "Error processing like", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("NewsFeedActivity", "Failed to like post: " + post.getId() + ", code: " + response.code() + ", message: " + response.message());
                    Toast.makeText(NewsFeedActivity.this, "Failed to like post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.e("NewsFeedActivity", "Error liking post: " + post.getId() + ", message: " + t.getMessage(), t);
                Toast.makeText(NewsFeedActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onCommentClick(PostResponseDTO post, String content, Long parentCommentId) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
            return;
        }

        CommentRequestDTO request = new CommentRequestDTO();
        request.setPostId(post.getId());
        request.setUserId((long) userid);
        request.setContent(content);
        request.setParentCommentId(parentCommentId);

        commentApiService.createComment(request).enqueue(new Callback<CommentDTO>() {
            @Override
            public void onResponse(Call<CommentDTO> call, Response<CommentDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CommentDTO newComment = response.body();
                    Log.d("NewsFeedActivity", "Successfully commented on post: postId=" + post.getId() + ", commentId=" + newComment.getId());
                    for (int i = 0; i < postList.size(); i++) {
                        if (postList.get(i).getId().equals(post.getId())) {
                            PostResponseDTO targetPost = postList.get(i);
                            if (targetPost.getComments() == null) {
                                targetPost.setComments(new ArrayList<>());
                            }
                            if (newComment.getReplies() == null) {
                                newComment.setReplies(new ArrayList<>());
                            }
                            if (parentCommentId != null) {
                                CommentDTO parent = findParentComment(targetPost.getComments(), parentCommentId);
                                if (parent != null) {
                                    if (parent.getReplies() == null) {
                                        parent.setReplies(new ArrayList<>());
                                    }
                                    if (!commentExists(parent.getReplies(), newComment.getId())) {
                                        Log.d("NewsFeedActivity", "Adding reply via API: commentId=" + newComment.getId() + ", parentId=" + parentCommentId);
                                        parent.getReplies().add(newComment);
                                        if (!parent.getUserId().equals((long) userid)) {
                                            createNotification(
                                                    parent.getUserId(),
                                                    (long) userid,
                                                    post.getId(),
                                                    "REPLY",
                                                    currentUser.getUsername() + " replied to your comment",
                                                    currentUserAvatarUrl
                                            );
                                        }
                                    } else {
                                        Log.d("NewsFeedActivity", "Reply already exists via API: commentId=" + newComment.getId());
                                    }
                                } else {
                                    Log.w("NewsFeedActivity", "Parent comment not found for reply: parentId=" + parentCommentId);
                                }
                            } else {
                                if (!commentExists(targetPost.getComments(), newComment.getId())) {
                                    Log.d("NewsFeedActivity", "Adding comment via API: commentId=" + newComment.getId());
                                    targetPost.getComments().add(newComment);
                                    if (!targetPost.getUserId().equals((long) userid)) {
                                        createNotification(
                                                targetPost.getUserId(),
                                                (long) userid,
                                                post.getId(),
                                                "COMMENT",
                                                currentUser.getUsername() + " commented on your post",
                                                currentUserAvatarUrl
                                        );
                                    }
                                } else {
                                    Log.d("NewsFeedActivity", "Comment already exists via API: commentId=" + newComment.getId());
                                }
                            }
                            sortComments(targetPost.getComments());
                            postAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                } else {
                    Log.e("NewsFeedActivity", "Failed to post comment on post: postId=" + post.getId() + ", code: " + response.code() + ", message: " + response.message());
                    Toast.makeText(NewsFeedActivity.this, "Failed to post comment: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentDTO> call, Throwable t) {

                Log.e("NewsFeedActivity", "Error posting comment on post: postId=" + post.getId() + ", message: " + t.getMessage(), t);
                Toast.makeText(NewsFeedActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNotifications() {
        postApiService.getNotifications((long) userid, 0, 20).enqueue(new Callback<List<NotificationDTO>>() {
            @Override
            public void onResponse(Call<List<NotificationDTO>> call, Response<List<NotificationDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notifications = response.body();
                    int unreadCount = getUnreadCount();
                    updateNotificationBadge(unreadCount);
                } else {
                    Log.e("NewsFeedActivity", "Failed to load notifications, code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<NotificationDTO>> call, Throwable t) {
                Log.e("NewsFeedActivity", "Error loading notifications: " + t.getMessage());
            }
        });
    }

    private int getUnreadCount() {
        int unreadCount = 0;
        for (NotificationDTO notification : notifications) {
            if (!notification.isRead()) {
                unreadCount++;
            }
        }
        return unreadCount;
    }

    private void updateNotificationBadge(int unreadCount) {
        if (notificationBadge != null) {
            if (unreadCount > 0) {
                notificationBadge.setText(String.valueOf(unreadCount));
                notificationBadge.setVisibility(View.VISIBLE);
            } else {
                notificationBadge.setVisibility(View.GONE);
            }
        }
    }

    private void createNotification(Long userId, Long actorId, Long postId, String type, String content, String userAvatarUrl) {
        NotificationDTO notification = new NotificationDTO();
        notification.setUserId(userId);
        notification.setActorId(actorId);
        notification.setPostId(postId);
        notification.setType(type);
        notification.setContent(content);
        notification.setUserAvatarUrl(userAvatarUrl);
        notification.setRead(false);

        postApiService.createNotification(notification).enqueue(new Callback<NotificationDTO>() {
            @Override
            public void onResponse(Call<NotificationDTO> call, Response<NotificationDTO> response) {
                if (response.isSuccessful()) {
                    Log.d("NewsFeedActivity", "Notification created for userId: " + userId + ", type: " + type);
                } else {
                    Log.e("NewsFeedActivity", "Failed to create notification: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<NotificationDTO> call, Throwable t) {
                Log.e("NewsFeedActivity", "Error creating notification: " + t.getMessage());
            }
        });
    }

    private void showNotificationDialog() {
        notificationDialog = new Dialog(this, R.style.DialogTheme);
        notificationDialog.setContentView(R.layout.dialog_notifications);
        notificationDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        notificationDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        RecyclerView notificationsRecyclerView = notificationDialog.findViewById(R.id.notifications_recycler_view);
        TextView noNotificationsText = notificationDialog.findViewById(R.id.no_notifications_text);
        Button closeButton = notificationDialog.findViewById(R.id.close_button);

        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter = new NotificationAdapter(this, notification -> {
            Toast.makeText(this, "Clicked: " + notification.getContent(), Toast.LENGTH_SHORT).show();
        });
        notificationsRecyclerView.setAdapter(notificationAdapter);

        postApiService.getNotifications((long) userid, 0, 20).enqueue(new Callback<List<NotificationDTO>>() {
            @Override
            public void onResponse(Call<List<NotificationDTO>> call, Response<List<NotificationDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notifications = response.body();
                    if (notifications.isEmpty()) {
                        noNotificationsText.setVisibility(View.VISIBLE);
                        notificationsRecyclerView.setVisibility(View.GONE);
                    } else {
                        noNotificationsText.setVisibility(View.GONE);
                        notificationsRecyclerView.setVisibility(View.VISIBLE);
                        notificationAdapter.updateNotifications(notifications);
                        updateNotificationBadge(getUnreadCount());
                    }
                } else {
                    Log.e("NewsFeedActivity", "Failed to load notifications, code: " + response.code());
                    Toast.makeText(NewsFeedActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NotificationDTO>> call, Throwable t) {
                Log.e("NewsFeedActivity", "Error loading notifications: " + t.getMessage());
                Toast.makeText(NewsFeedActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });

        closeButton.setOnClickListener(v -> {
            postApiService.markAllNotificationsAsRead(userid).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        for (NotificationDTO notification : notifications) {
                            notification.setRead(true);
                        }
                        notificationAdapter.updateNotifications(notifications);
                        updateNotificationBadge(0);
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("NewsFeedActivity", "Error marking notifications as read: " + t.getMessage());
                }
            });
            notificationDialog.dismiss();
            notificationDialog = null;
        });
        notificationDialog.show();
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
                _processorsAssistant: dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private double calculatePostScore(PostResponseDTO post) {
        long currentTime = System.currentTimeMillis();
        long createdAt = post.getCreatedAt().getTime();
        long timeDiff = currentTime - createdAt;

        double timeScore = 1.0 / (1 + timeDiff / (1000.0 * 60 * 60));
        double boostScore = 0.0;
        if (timeDiff <= 300_000) {
            boostScore = 100.0;
            Log.d("NewsFeedActivity", "Applying boost for new post: id=" + post.getId() + ", timeDiff=" + timeDiff + "ms");
        }

        double likes = post.getLikeCount() != 0 ? post.getLikeCount() : 0;
        double comments = (post.getComments() != null) ? post.getComments().size() : 0;
        double shares = 0;
        double engagementScore = (likes * 0.4) + (comments * 0.4) + (shares * 0.2);
        double affinityScore = 0.1;

        double totalScore = (timeScore * 0.3) + (engagementScore * 0.5) + (affinityScore * 0.2) + boostScore;
        Log.d("NewsFeedActivity", "Post score: id=" + post.getId() + ", totalScore=" + totalScore +
                ", timeScore=" + timeScore + ", boostScore=" + boostScore + ", engagementScore=" + engagementScore);
        return totalScore;
    }

    private void sortPostsByScore() {
        List<PostResponseDTO> sortedList = new ArrayList<>(postList);
        Collections.sort(sortedList, (p1, p2) -> Double.compare(p2.getScore(), p1.getScore()));
        postList.clear();
        postList.addAll(sortedList);
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
                    currentUserAvatarUrl = currentUser.getAvatarUrl();
                    if (userAvatar != null) {
                        String avatarUrl = currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()
                                ? RetrofitClient.getBaseUrl() + currentUser.getAvatarUrl() : null;
                        Log.d("NewsFeedActivity", "Loading avatar URL: " + avatarUrl);
                        if (avatarUrl != null && avatarUrl.contains("/uploads/")) {
                            Glide.with(NewsFeedActivity.this)
                                    .load(avatarUrl)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .circleCrop()
                                    .placeholder(R.drawable.ic_user)
                                    .error(R.drawable.ic_user)
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                                            Log.e("NewsFeedActivity", "Glide load failed for avatar: " + avatarUrl, e);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                                            Log.d("NewsFeedActivity", "Glide load successful for avatar: " + avatarUrl);
                                            return false;
                                        }
                                    })
                                    .into(userAvatar);
                        } else {
                            Log.w("NewsFeedActivity", "Invalid avatar URL: " + avatarUrl);
                            Glide.with(NewsFeedActivity.this)
                                    .load(R.drawable.ic_user)
                                    .circleCrop()
                                    .into(userAvatar);
                        }
                    } else {
                        Log.e("NewsFeedActivity", "userAvatar is null");
                    }
                    postAdapter = new PostAdapter(
                            getSupportFragmentManager(),
                            NewsFeedActivity.this::onLikeClick,
                            NewsFeedActivity.this::onCommentClick,
                            new OnCommentInteractionListener() {
                                @Override
                                public void onLikeComment(CommentDTO comment) {}
                                @Override
                                public void onReplyComment(CommentDTO comment) {
                                    showReplyDialog(comment);
                                }
                                @Override
                                public void onShareComment(CommentDTO comment) {}
                            }, RetrofitClient.getBaseUrl() + currentUser.getAvatarUrl()
                    );
                    postsRecyclerView.setAdapter(postAdapter);
                    postAdapter.updatePosts(postList);
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

            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(dialog.getWindow().getAttributes());
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(params);

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

            if (postContent == null || privacySpinner == null || postButton == null || closeButton == null ||
                    photoOptionLayout == null || selectedImagesRecyclerView == null ||
                    tagOptionLayout == null || feelingOptionLayout == null ||
                    checkinOptionLayout == null || liveOptionLayout == null ||
                    backgroundOptionLayout == null || userAvatar == null || userName == null || uploadProgress == null) {
                Log.e("NewsFeedActivity", "One or more views in dialog not found");
                Toast.makeText(this, "Error loading dialog UI", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentUser != null) {
                userName.setText(currentUser.getUsername() != null ? currentUser.getUsername() : "User");
                String avataUrl = RetrofitClient.getBaseUrl() + currentUser.getAvatarUrl();
                Glide.with(this)
                        .load(avataUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(userAvatar);
            } else {
                Log.w("NewsFeedActivity", "currentUser is null, showing default values");
                userName.setText("User");
                Glide.with(this)
                        .load(R.drawable.ic_user)
                        .circleCrop()
                        .into(userAvatar);
                Toast.makeText(this, "Unable to load user info", Toast.LENGTH_SHORT).show();
            }

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.privacy_options,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            privacySpinner.setAdapter(adapter);

            if (selectedImageUris == null) {
                selectedImageUris = new ArrayList<>();
            }

            SelectedImagesAdapter imagesAdapter = new SelectedImagesAdapter(this, selectedImageUris, position -> {
                selectedImageUris.remove(position);
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

                if (selectedImageUris != null && !selectedImageUris.isEmpty()) {
                    uploadImagesForPost(content, mappedPrivacy, currentUser, dialog);
                } else {
                    PostRequestDTO request = new PostRequestDTO();
                    request.setContent(content);
                    request.setUserId((long) userid);
                    request.setPrivacy(mappedPrivacy);
                    request.setImageUrls(null);
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
                if (uploadProgress != null) {
                    uploadProgress.setVisibility(View.GONE);
                }
                if (response.isSuccessful() && response.body() != null) {
                    List<String> imageUrls = response.body().stream()
                            .filter(img -> img.getImageUrl() != null)
                            .map(img -> img.getImageUrl())
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

                if (hasError[0] && imageUrls.isEmpty()) {
                    Toast.makeText(NewsFeedActivity.this, "Failed to upload images. Please try again.", Toast.LENGTH_LONG).show();
                } else {
                    PostRequestDTO request = new PostRequestDTO();
                    request.setContent(postContent);
                    request.setUserId((long) userid);
                    request.setPrivacy(selectedPrivacy);
                    request.setImageUrls(imageUrls.isEmpty() ? null : imageUrls);
                    createPost(request, dialog);
                }
            });
        }
    }

    private void createPost(PostRequestDTO request, Dialog dialog) {
        if (!isNetworkAvailable()) {
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

                ProgressBar uploadProgress = dialog.findViewById(R.id.upload_progress);
                if (uploadProgress != null) {
                    uploadProgress.setVisibility(View.GONE);
                }
                if (response.isSuccessful() && response.body() != null) {
                    PostResponseDTO newPost = response.body();
                    Log.d("NewsFeedActivity", "Bài viết mới được tạo: id=" + newPost.getId() + ", nội dung=" + newPost.getContent() +
                            ", danh sách ảnh=" + newPost.getImageUrl() + ", bình luận=" + newPost.getComments() +
                            ", lượt thích=" + newPost.getLikeCount());
                    if (newPost.getId() == null) {
                        Log.e("NewsFeedActivity", "Bài viết mới có ID null!");
                        Toast.makeText(NewsFeedActivity.this, "Lỗi: Thiếu ID bài viết", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (newPost.getComments() == null) {
                        newPost.setComments(new ArrayList<>());
                    }
                    if (newPost.getLikeCount() == 0) {
                        newPost.setLikeCount(0);
                    }
                    if (newPost.getImageUrl() == null) {
                        newPost.setImageUrl(new ArrayList<>());
                    }
                    newPost.setScore(calculatePostScore(newPost));
                    postList.add(0, newPost);
                    postAdapter.addNewPost(newPost);
                    Log.d("NewsFeedActivity", "Danh sách bài viết sau khi thêm: " +
                            postList.stream().map(p -> String.valueOf(p.getId())).collect(Collectors.joining(", ")));
                    postsRecyclerView.scrollToPosition(0);
                    dialog.dismiss();
                    createPostDialog = null;
                    if (selectedImageUris != null) {
                        selectedImageUris.clear();
                    }
                    Toast.makeText(NewsFeedActivity.this, "Tạo bài đăng thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("NewsFeedActivity", "Không thể tạo bài đăng, mã lỗi: " + response.code() + ", thông điệp: " + response.message());
                    Toast.makeText(NewsFeedActivity.this, "Không thể tạo bài đăng: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponseDTO> call, Throwable t) {

                ProgressBar uploadProgress = dialog.findViewById(R.id.upload_progress);
                if (uploadProgress != null) {
                    uploadProgress.setVisibility(View.GONE);
                }
                Log.e("NewsFeedActivity", "Error creating post: " + t.getMessage(), t);
                Toast.makeText(NewsFeedActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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

        isLoading = true;
        postApiService.getPosts(currentPage, PAGE_SIZE, (long) userid).enqueue(new Callback<List<PostResponseDTO>>() {
            @Override
            public void onResponse(Call<List<PostResponseDTO>> call, Response<List<PostResponseDTO>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    postList.addAll(response.body());
                    for (PostResponseDTO post : postList) {
                        post.setScore(calculatePostScore(post));
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
                Toast.makeText(NewsFeedActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMorePosts() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
            return;
        }

        isLoading = true;
        postApiService.getPosts(currentPage, PAGE_SIZE, (long) userid).enqueue(new Callback<List<PostResponseDTO>>() {
            @Override
            public void onResponse(Call<List<PostResponseDTO>> call, Response<List<PostResponseDTO>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
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
                Toast.makeText(NewsFeedActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
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

        postApiService.getPosts(olderPage, PAGE_SIZE, (long) userid).enqueue(new Callback<List<PostResponseDTO>>() {
            @Override
            public void onResponse(Call<List<PostResponseDTO>> call, Response<List<PostResponseDTO>> response) {
                isLoadingOlderPosts = false;
                if (response.isSuccessful() && response.body() != null) {
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
                Toast.makeText(NewsFeedActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
