package com.myjob.real_time_chat_final.adapter;

import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.target.Target;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.modelDTO.PostResponseDTO;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import android.util.Log;
import java.sql.Timestamp;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<PostResponseDTO> postList;

    public PostAdapter(List<PostResponseDTO> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostResponseDTO post = postList.get(position);

        // Hiển thị tên người dùng
        if (holder.postUsername != null) {
            holder.postUsername.setText(post.getUsername() != null ? post.getUsername() : "Unknown User");
        }

        // Hiển thị thời gian
        if (holder.postTime != null && post.getCreatedAt() != null) {
            holder.postTime.setText(DateUtils.getRelativeTimeSpanString(
                    post.getCreatedAt().getTime(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
            ));
        }

        // Hiển thị quyền riêng tư
        if (holder.postPrivacy != null) {
            holder.postPrivacy.setText(post.getPrivacy() != null ? post.getPrivacy() : "PUBLIC");
        }

        // Hiển thị nội dung bài đăng
        if (holder.postContent != null) {
            holder.postContent.setText(post.getContent() != null ? post.getContent() : "");
        }

        // Hiển thị ảnh (nếu có)
        if (holder.postImage != null) {
            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                String baseUrl = RetrofitClient.getBaseUrl(); // Lấy base URL từ RetrofitClient
                String fullImageUrl = baseUrl + post.getImageUrl();
                Log.d("PostAdapter", "Loading post image URL: " + fullImageUrl);

                holder.postImage.setVisibility(View.VISIBLE);
                Glide.with(holder.itemView.getContext())
                        .load(fullImageUrl)
                        .transform(new CenterCrop()) // Giữ tỷ lệ ảnh, tương tự scaleType="centerCrop"
                        .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {


                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                                Log.e("PostAdapter", "Failed to load image: " + fullImageUrl + ", error: " + (e != null ? e.getMessage() : "Unknown error"));
                                holder.postImage.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                Log.d("PostAdapter", "Successfully loaded image: " + fullImageUrl);
                                return false;
                            }
                        })
                        .into(holder.postImage);
            } else {
                holder.postImage.setVisibility(View.GONE);
            }
        }

        // Hiển thị số lượt thích và bình luận (mặc định là 0 vì DTO chưa có dữ liệu này)
        if (holder.likeCount != null) {
            holder.likeCount.setText("0");
        }
        if (holder.commentCount != null) {
            holder.commentCount.setText("0");
        }

        // Xử lý sự kiện cho các nút (có thể thêm logic sau)
        if (holder.btnLike != null) {
            holder.btnLike.setOnClickListener(v -> {
                // TODO: Thêm logic cho nút Like
            });
        }
        if (holder.btnComment != null) {
            holder.btnComment.setOnClickListener(v -> {
                // TODO: Thêm logic cho nút Comment
            });
        }
        if (holder.btnShare != null) {
            holder.btnShare.setOnClickListener(v -> {
                // TODO: Thêm logic cho nút Share
            });
        }

        // Hiển thị avatar (nếu có logic tải avatar từ user)
        if (holder.postAvatar != null) {
            // Hiện tại để mặc định, có thể thêm logic tải avatar từ user nếu cần
            holder.postAvatar.setImageResource(R.drawable.ic_user);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postAvatar;
        TextView postUsername;
        TextView postTime;
        TextView postPrivacy;
        TextView postContent;
        ImageView postImage;
        TextView likeCount;
        TextView commentCount;
        Button btnLike;
        Button btnComment;
        Button btnShare;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postAvatar = itemView.findViewById(R.id.post_avatar);
            postUsername = itemView.findViewById(R.id.post_username);
            postTime = itemView.findViewById(R.id.post_time);
            postPrivacy = itemView.findViewById(R.id.post_privacy);
            postContent = itemView.findViewById(R.id.post_content);
            postImage = itemView.findViewById(R.id.post_image);
            likeCount = itemView.findViewById(R.id.like_count);
            commentCount = itemView.findViewById(R.id.comment_count);
            btnLike = itemView.findViewById(R.id.btn_like);
            btnComment = itemView.findViewById(R.id.btn_comment);
            btnShare = itemView.findViewById(R.id.btn_share);
        }
    }
}