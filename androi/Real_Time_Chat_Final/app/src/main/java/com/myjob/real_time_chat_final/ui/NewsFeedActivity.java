package com.myjob.real_time_chat_final.ui;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedActivity extends AppCompatActivity {

    private RecyclerView postsRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    private RecyclerView storiesRecyclerView;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;

    private EditText statusInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_feed);

        View mainView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Khởi tạo RecyclerView cho Stories
        storiesRecyclerView = findViewById(R.id.stories_recycler_view);
        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        storiesRecyclerView.setHasFixedSize(true);

        // Dữ liệu mẫu cho Stories
        storyList = new ArrayList<>();
        storyList.add(new Story(true, null, null, null, false)); // Create Story
        storyList.add(new Story(false, "Phan Kỳ Anh", R.drawable.ic_user, R.drawable.ic_user, true)); // Chưa xem
        storyList.add(new Story(false, "Hà Thu Phượng", R.drawable.ic_user, R.drawable.ic_user, false)); // Đã xem
        storyList.add(new Story(false, "Trương Quỳnh Anh", R.drawable.ic_user, R.drawable.ic_user, true)); // Chưa xem

        storyAdapter = new StoryAdapter(storyList);
        storiesRecyclerView.setAdapter(storyAdapter);

        // Khởi tạo RecyclerView cho bài đăng
        postsRecyclerView = findViewById(R.id.posts_recycler_view);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postsRecyclerView.setHasFixedSize(true);

        // Dữ liệu mẫu cho bài đăng
        postList = new ArrayList<>();
        postList.add(new Post("Lê Thị Hải", "2d ago", "Con dựợng đi đến thân cộng có nguồi trai quá chồng gai, có nguồi đi trên con dựợng bà...", R.drawable.ic_user));
        postList.add(new Post("Hà Thu Phượng", "1h ago", "Hôm nay trời đẹp, đi dạo một vòng nhé!", R.drawable.ic_user));
        postList.add(new Post("Lê Bảo", "3h ago", "Chia sẻ một bức ảnh đẹp hôm qua chụp được!", R.drawable.ic_user));

        postAdapter = new PostAdapter(postList);
        postsRecyclerView.setAdapter(postAdapter);

        // Khởi tạo EditText "What's on your mind?" và thêm sự kiện click
        statusInput = findViewById(R.id.status_input);
        statusInput.setOnClickListener(v -> showCreatePostDialog());
    }

    private void showCreatePostDialog() {
        // Tạo dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_create_post);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Ánh xạ các thành phần trong dialog
        ImageButton closeButton = dialog.findViewById(R.id.close_button);
        Button postButton = dialog.findViewById(R.id.post_button);
        EditText postInput = dialog.findViewById(R.id.post_input);

        // Xử lý nút "Close"
        closeButton.setOnClickListener(v -> dialog.dismiss());

        // Xử lý nút "Post"
        postButton.setOnClickListener(v -> {
            String postContent = postInput.getText().toString().trim();
            if (!postContent.isEmpty()) {
                // Thêm bài đăng mới vào danh sách
                postList.add(0, new Post("Phan Tất Duy", "Just now", postContent, R.drawable.ic_user));
                postAdapter.notifyItemInserted(0);
                postsRecyclerView.scrollToPosition(0);
                Toast.makeText(this, "Post created!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter some content!", Toast.LENGTH_SHORT).show();
            }
        });

        // Hiển thị dialog
        dialog.show();
    }
}

// Lớp Story
class Story {
    boolean isCreateStory;
    String username;
    Integer avatarResId;
    Integer imageResId;
    boolean isUnseen;

    public Story(boolean isCreateStory, String username, Integer avatarResId, Integer imageResId, boolean isUnseen) {
        this.isCreateStory = isCreateStory;
        this.username = username;
        this.avatarResId = avatarResId;
        this.imageResId = imageResId;
        this.isUnseen = isUnseen;
    }

    public boolean isCreateStory() {
        return isCreateStory;
    }

    public String getUsername() {
        return username;
    }

    public Integer getAvatarResId() {
        return avatarResId;
    }

    public Integer getImageResId() {
        return imageResId;
    }

    public boolean isUnseen() {
        return isUnseen;
    }
}

// Adapter cho Stories
class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {
    private List<Story> storyList;

    public StoryAdapter(List<Story> storyList) {
        this.storyList = storyList;
    }

    @Override
    public StoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoryViewHolder holder, int position) {
        Story story = storyList.get(position);
        if (story.isCreateStory()) {
            holder.createStoryLayout.setVisibility(View.VISIBLE);
            holder.storyAvatar.setVisibility(View.GONE);
            holder.storyBorder.setVisibility(View.GONE);
            holder.storyImageContainer.setVisibility(View.GONE);
            holder.storyUsername.setVisibility(View.GONE);
        } else {
            holder.createStoryLayout.setVisibility(View.GONE);
            holder.storyAvatar.setVisibility(View.VISIBLE);
            holder.storyBorder.setVisibility(View.VISIBLE);
            holder.storyImageContainer.setVisibility(View.VISIBLE);
            holder.storyUsername.setVisibility(View.VISIBLE);
            holder.storyUsername.setText(story.getUsername());

            // Hiển thị avatar
            if (story.getAvatarResId() != null) {
                holder.storyAvatar.setImageResource(story.getAvatarResId());
            } else {
                holder.storyAvatar.setImageResource(R.drawable.ic_user);
            }

            // Hiển thị hình ảnh Story
            if (story.getImageResId() != null) {
                holder.storyImage.setImageResource(story.getImageResId());
            } else {
                holder.storyImage.setImageResource(R.drawable.ic_user);
            }

            // Cập nhật màu viền dựa trên trạng thái xem
            int borderColor = story.isUnseen() ? Color.GREEN : Color.BLUE;
            holder.storyBorder.setBackgroundTintList(ColorStateList.valueOf(borderColor));
            holder.storyImageBorder.setBackgroundTintList(ColorStateList.valueOf(borderColor));
        }
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    static class StoryViewHolder extends RecyclerView.ViewHolder {
        LinearLayout createStoryLayout;
        FrameLayout storyImageContainer;
        ImageView storyAvatar, storyImage, storyBorder, storyImageBorder;
        TextView storyUsername;

        public StoryViewHolder(View itemView) {
            super(itemView);
            createStoryLayout = itemView.findViewById(R.id.create_story_layout);
            storyAvatar = itemView.findViewById(R.id.story_avatar);
            storyImage = itemView.findViewById(R.id.story_image);
            storyBorder = itemView.findViewById(R.id.story_border);
            storyImageBorder = itemView.findViewById(R.id.story_image_border);
            storyImageContainer = itemView.findViewById(R.id.story_image_container);
            storyUsername = itemView.findViewById(R.id.story_username);
        }
    }
}

// Lớp Post
class Post {
    String username, time, content;
    int imageResId;

    public Post(String username, String time, String content, int imageResId) {
        this.username = username;
        this.time = time;
        this.content = content;
        this.imageResId = imageResId;
    }

    public String getUsername() {
        return username;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public int getImageResId() {
        return imageResId;
    }
}

// Adapter cho bài đăng
class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.username.setText(post.getUsername());
        holder.time.setText(post.getTime());
        holder.content.setText(post.getContent());
        holder.image.setImageResource(post.getImageResId());
        holder.avatar.setImageResource(R.drawable.ic_user);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView username, time, content;
        ImageView image, avatar;

        public PostViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.post_username);
            time = itemView.findViewById(R.id.post_time);
            content = itemView.findViewById(R.id.post_content);
            image = itemView.findViewById(R.id.post_image);
            avatar = itemView.findViewById(R.id.post_avatar);
        }
    }
}