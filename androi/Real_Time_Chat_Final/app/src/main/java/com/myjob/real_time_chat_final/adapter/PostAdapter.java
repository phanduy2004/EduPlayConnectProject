package com.myjob.real_time_chat_final.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.myjob.real_time_chat_final.OnCommentInteractionListener;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.modelDTO.CommentDTO;
import com.myjob.real_time_chat_final.modelDTO.PostResponseDTO;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;
import com.myjob.real_time_chat_final.ui.ImageGalleryDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.graphics.drawable.Drawable;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<PostResponseDTO> postList = new ArrayList<>();
    private final FragmentManager fragmentManager;
    private final OnLikeClickListener likeClickListener;
    private final OnCommentClickListener commentClickListener;
    private final OnCommentInteractionListener commentInteractionListener;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private static final String TAG = "PostAdapter";
    private static final int INITIAL_COMMENT_COUNT = 3;
    private static final RequestOptions glideOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_user)
            .circleCrop();

    public interface OnLikeClickListener {
        void onLikeClick(PostResponseDTO post);
    }

    public interface OnCommentClickListener {
        void onCommentClick(PostResponseDTO post, String content, Long parentCommentId);
    }

    public PostAdapter(FragmentManager fragmentManager, OnLikeClickListener likeClickListener,
                       OnCommentClickListener commentClickListener, OnCommentInteractionListener commentInteractionListener) {
        this.fragmentManager = fragmentManager;
        this.likeClickListener = likeClickListener;
        this.commentClickListener = commentClickListener;
        this.commentInteractionListener = commentInteractionListener;
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

        // Hiển thị thông tin người dùng
        holder.usernameTextView.setText(post.getUsername() != null ? post.getUsername() : "Unknown");
        String avatarUrl = post.getAvatarUrl() != null && !post.getAvatarUrl().isEmpty()
                ? RetrofitClient.getBaseUrl() + post.getAvatarUrl() : null;
        Log.d(TAG, "Loading avatar URL: " + avatarUrl);
        holder.avatarProgressBar.setVisibility(View.VISIBLE);
        if (avatarUrl != null && avatarUrl.contains("/uploads/")) {
            Glide.with(holder.itemView.getContext())
                    .load(avatarUrl)
                    .thumbnail(0.25f)
                    .apply(glideOptions)
                    .override(100, 100)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e,
                                                    Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Glide load failed for avatar: " + avatarUrl, e);
                            holder.avatarProgressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                       com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            Log.d(TAG, "Glide load successful for avatar: " + avatarUrl);
                            holder.avatarProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.avatarImageView);
        } else {
            Log.w(TAG, "Invalid avatar URL: " + avatarUrl);
            holder.avatarImageView.setImageResource(R.drawable.ic_user);
            holder.avatarProgressBar.setVisibility(View.GONE);
        }

        // Hiển thị avatar trong ô nhập bình luận
        holder.commentAvatarProgressBar.setVisibility(View.VISIBLE);
        if (avatarUrl != null && avatarUrl.contains("/uploads/")) {
            Glide.with(holder.itemView.getContext())
                    .load(avatarUrl)
                    .thumbnail(0.25f)
                    .apply(glideOptions)
                    .override(80, 80)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e,
                                                    Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Glide load failed for comment avatar: " + avatarUrl, e);
                            holder.commentAvatarProgressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                       com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            Log.d(TAG, "Glide load successful for comment avatar: " + avatarUrl);
                            holder.commentAvatarProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.commentAvatarInput);
        } else {
            Log.w(TAG, "Invalid comment avatar URL: " + avatarUrl);
            holder.commentAvatarInput.setImageResource(R.drawable.ic_user);
            holder.commentAvatarProgressBar.setVisibility(View.GONE);
        }

        // Hiển thị thời gian và quyền riêng tư
        String time = post.getCreatedAt() != null ? sdf.format(post.getCreatedAt()) : "Unknown";
        holder.timeTextView.setText(time);
        holder.privacyTextView.setText(post.getPrivacy() != null ? post.getPrivacy() : "PUBLIC");

        // Hiển thị nội dung bài đăng
        holder.contentTextView.setText(post.getContent() != null ? post.getContent() : "");

        // Hiển thị media (ảnh và video)
        List<String> mediaUrls = post.getImageUrl();
        List<String> validMediaUrls = new ArrayList<>();
        if (mediaUrls != null && !mediaUrls.isEmpty()) {
            for (String url : mediaUrls) {
                if (url != null && !url.isEmpty() && url.contains("/uploads/")) {
                    validMediaUrls.add(url);
                } else {
                    Log.w(TAG, "Invalid media URL for post " + post.getId() + ": " + url);
                }
            }
        }

        if (!validMediaUrls.isEmpty()) {
            Log.d(TAG, "Loading media: " + validMediaUrls);
            holder.imagesRecyclerView.setVisibility(View.VISIBLE);
            Log.d(TAG, "imagesRecyclerView visibility: " + (holder.imagesRecyclerView.getVisibility() == View.VISIBLE ? "VISIBLE" : "GONE"));

            int mediaCount = validMediaUrls.size();
            if (!(holder.imagesRecyclerView.getLayoutManager() instanceof GridLayoutManager)) {
                GridLayoutManager newLayoutManager = new GridLayoutManager(holder.itemView.getContext(), 3);
                holder.imagesRecyclerView.setLayoutManager(newLayoutManager);
            }
            GridLayoutManager layoutManager = (GridLayoutManager) holder.imagesRecyclerView.getLayoutManager();
            if (mediaCount == 1 || mediaCount == 2 || mediaCount >= 4) {
                layoutManager.setSpanCount(mediaCount == 1 ? 1 : 2);
            } else if (mediaCount == 3) {
                layoutManager.setSpanCount(3);
            }

            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (mediaCount == 3) {
                        if (position == 0) {
                            return 2;
                        } else {
                            return 1;
                        }
                    }
                    return 1;
                }
            });

            if (holder.imageAdapter == null) {
                holder.imageAdapter = new PostImagesAdapter(validMediaUrls, () -> {
                    Log.d(TAG, "See more clicked for post: " + post.getId());
                    try {
                        ImageGalleryDialog dialog = ImageGalleryDialog.newInstance(new ArrayList<>(validMediaUrls));
                        dialog.show(fragmentManager, "ImageGalleryDialog");
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to show ImageGalleryDialog", e);
                    }
                }, position -> {
                    Log.d(TAG, "Image clicked at position: " + position);
                    try {
                        ImageGalleryDialog dialog = ImageGalleryDialog.newInstance(new ArrayList<>(validMediaUrls));
                        dialog.show(fragmentManager, "ImageGalleryDialog");
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to show ImageGalleryDialog", e);
                    }
                }, fragmentManager);
                holder.imagesRecyclerView.setAdapter(holder.imageAdapter);
            } else {
                holder.imageAdapter.updateMedia(validMediaUrls);
            }
            holder.imagesRecyclerView.requestLayout();
        } else {
            Log.d(TAG, "No valid media for post: " + post.getId());
            holder.imagesRecyclerView.setVisibility(View.GONE);
        }

        // Like và comment count
        holder.likeCountTextView.setText(post.getLikeCount() + " Likes");
        int commentCount = (post.getComments() != null) ? post.getComments().size() : 0;
        holder.commentCountTextView.setText(commentCount + " Comments");
        Log.d(TAG, "Post " + post.getId() + " has " + commentCount + " comments");

        // Comment recycler view với phân trang
        if (holder.commentAdapter == null) {
            holder.commentAdapter = new CommentAdapter(commentInteractionListener);
            holder.commentsRecyclerView.setAdapter(holder.commentAdapter);
            holder.commentsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            holder.commentsRecyclerView.setNestedScrollingEnabled(false);
        }

        List<CommentDTO> comments = post.getComments() != null ? post.getComments() : new ArrayList<>();
        List<CommentDTO> displayedComments = new ArrayList<>();
        if (comments.size() > INITIAL_COMMENT_COUNT) {
            displayedComments.addAll(comments.subList(0, INITIAL_COMMENT_COUNT));
            holder.seeMoreComments.setVisibility(View.VISIBLE);
            holder.seeMoreComments.setText("Xem thêm " + (comments.size() - INITIAL_COMMENT_COUNT) + " bình luận");
            holder.seeMoreComments.setOnClickListener(v -> {
                holder.commentAdapter.updateComments(comments);
                holder.seeMoreComments.setVisibility(View.GONE);
            });
        } else {
            displayedComments.addAll(comments);
            holder.seeMoreComments.setVisibility(View.GONE);
        }
        holder.commentAdapter.updateComments(displayedComments);

        // Xử lý sự kiện
        holder.commentSendButton.setOnClickListener(v -> {
            String commentContent = holder.commentInput.getText().toString().trim();
            if (!commentContent.isEmpty()) {
                commentClickListener.onCommentClick(post, commentContent, null);
                holder.commentInput.setText("");
            }
        });

        holder.likeButton.setOnClickListener(v -> likeClickListener.onLikeClick(post));

        holder.commentToggleButton.setOnClickListener(v -> holder.commentInput.requestFocus());

        holder.shareButton.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, post.getContent() != null ? post.getContent() : "");
            holder.itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài đăng"));
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updatePosts(List<PostResponseDTO> newPosts) {
        PostDiffCallback diffCallback = new PostDiffCallback(postList, newPosts);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        postList.clear();
        postList.addAll(newPosts);
        diffResult.dispatchUpdatesTo(this);
    }

    public void addNewPost(PostResponseDTO newPost) {
        Log.d(TAG, "Adding new post: id=" + newPost.getId());
        List<PostResponseDTO> newList = new ArrayList<>();
        newList.add(newPost);
        newList.addAll(postList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostDiffCallback(postList, newList));
        postList.clear();
        postList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public void addOlderPosts(List<PostResponseDTO> olderPosts) {
        Log.d(TAG, "Adding " + olderPosts.size() + " older posts");
        List<PostResponseDTO> newList = new ArrayList<>(olderPosts);
        newList.addAll(postList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostDiffCallback(postList, newList));
        postList.clear();
        postList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public void updatePost(Long postId, PostResponseDTO updatedPost) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId().equals(postId)) {
                postList.set(i, updatedPost);
                notifyItemChanged(i);
                break;
            }
        }
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView, commentAvatarInput;
        ProgressBar avatarProgressBar, commentAvatarProgressBar;
        TextView usernameTextView, timeTextView, privacyTextView, contentTextView,
                likeCountTextView, commentCountTextView, likeButton, commentToggleButton, shareButton,
                seeMoreComments;
        RecyclerView imagesRecyclerView, commentsRecyclerView;
        PostImagesAdapter imageAdapter;
        CommentAdapter commentAdapter;
        ImageButton commentSendButton;
        EditText commentInput;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.post_avatar);
            avatarProgressBar = itemView.findViewById(R.id.avatar_progress_bar);
            usernameTextView = itemView.findViewById(R.id.post_username);
            timeTextView = itemView.findViewById(R.id.post_time);
            privacyTextView = itemView.findViewById(R.id.post_privacy);
            contentTextView = itemView.findViewById(R.id.post_content);
            imagesRecyclerView = itemView.findViewById(R.id.post_images_recycler_view);
            likeCountTextView = itemView.findViewById(R.id.like_count);
            commentCountTextView = itemView.findViewById(R.id.comment_count);
            commentsRecyclerView = itemView.findViewById(R.id.comments_recycler_view);
            commentInput = itemView.findViewById(R.id.comment_input);
            commentAvatarInput = itemView.findViewById(R.id.comment_avatar_input);
            commentAvatarProgressBar = itemView.findViewById(R.id.comment_avatar_progress_bar);
            likeButton = itemView.findViewById(R.id.btn_like);
            commentToggleButton = itemView.findViewById(R.id.btn_comment_toggle);
            commentSendButton = itemView.findViewById(R.id.btn_comment_send);
            shareButton = itemView.findViewById(R.id.btn_share);
            seeMoreComments = itemView.findViewById(R.id.see_more_comments);

            imagesRecyclerView.setHasFixedSize(true);
            imagesRecyclerView.setNestedScrollingEnabled(false);
            imagesRecyclerView.setItemViewCacheSize(10);
            commentsRecyclerView.setHasFixedSize(true);
            commentsRecyclerView.setNestedScrollingEnabled(false);
        }
    }

    static class PostDiffCallback extends DiffUtil.Callback {
        private final List<PostResponseDTO> oldList;
        private final List<PostResponseDTO> newList;

        public PostDiffCallback(List<PostResponseDTO> oldList, List<PostResponseDTO> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            PostResponseDTO oldPost = oldList.get(oldItemPosition);
            PostResponseDTO newPost = newList.get(newItemPosition);
            return oldPost.getLikeCount() == newPost.getLikeCount() &&
                    (oldPost.getComments() != null ? oldPost.getComments().size() : 0) ==
                            (newPost.getComments() != null ? newPost.getComments().size() : 0) &&
                    oldPost.getContent().equals(newPost.getContent());
        }
    }
}