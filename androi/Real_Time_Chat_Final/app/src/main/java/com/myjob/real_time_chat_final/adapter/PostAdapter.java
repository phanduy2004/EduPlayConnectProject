package com.myjob.real_time_chat_final.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<PostResponseDTO> postList = new ArrayList<>();
    private final FragmentManager fragmentManager;
    private final OnLikeClickListener likeClickListener;
    private final OnCommentClickListener commentClickListener;
    private final OnCommentInteractionListener commentInteractionListener;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private static final String TAG = "PostAdapter";
    private final Map<Long, Integer> displayedCommentCounts = new HashMap<>();
    private static final int INITIAL_COMMENT_COUNT = 2; // Số bình luận ban đầu
    private static final int LOAD_MORE_COUNT = 3; // Số bình luận tải thêm mỗi lần
    private String currentUserAvatarUrl;

    public interface OnLikeClickListener {
        void onLikeClick(PostResponseDTO post);
    }

    public interface OnCommentClickListener {
        void onCommentClick(PostResponseDTO post, String content, Long parentCommentId);
    }

    public PostAdapter(FragmentManager fragmentManager, OnLikeClickListener likeClickListener,
                       OnCommentClickListener commentClickListener, OnCommentInteractionListener commentInteractionListener,
                       String currentUserAvatarUrl) {
        this.fragmentManager = fragmentManager;
        this.likeClickListener = likeClickListener;
        this.commentClickListener = commentClickListener;
        this.commentInteractionListener = commentInteractionListener;
        this.currentUserAvatarUrl = currentUserAvatarUrl;
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
        holder.usernameTextView.setText(post.getUsername() != null ? post.getUsername() : "Không xác định");
        String avatarUrl = post.getAvatarUrl() != null && !post.getAvatarUrl().isEmpty()
                ? RetrofitClient.getBaseUrl() + post.getAvatarUrl() : null;
        Log.d(TAG, "Tải URL avatar: " + avatarUrl);
        if (avatarUrl != null && avatarUrl.contains("/uploads/")) {
            Glide.with(holder.itemView.getContext())
                    .load(avatarUrl)
                    .thumbnail(0.25f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_user)
                    .circleCrop()
                    .override(100, 100)
                    .error(R.drawable.ic_user)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e,
                                                    Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Tải avatar thất bại: " + avatarUrl, e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                       com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            Log.d(TAG, "Tải avatar thành công: " + avatarUrl);
                            return false;
                        }
                    })
                    .into(holder.avatarImageView);
        } else {
            Log.w(TAG, "URL avatar không hợp lệ: " + avatarUrl);
            holder.avatarImageView.setImageResource(R.drawable.ic_user);
        }

        // Hiển thị avatar trong ô nhập bình luận
        if (currentUserAvatarUrl != null && currentUserAvatarUrl.contains("/uploads/")) {
            Glide.with(holder.itemView.getContext())
                    .load(currentUserAvatarUrl)
                    .thumbnail(0.25f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_user)
                    .circleCrop()
                    .override(80, 80)
                    .error(R.drawable.ic_user)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e,
                                                    Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Tải avatar bình luận thất bại: " + currentUserAvatarUrl, e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                       com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            Log.d(TAG, "Tải avatar bình luận thành công: " + currentUserAvatarUrl);
                            return false;
                        }
                    })
                    .into(holder.commentAvatarInput);
        } else {
            Log.w(TAG, "URL avatar bình luận không hợp lệ: " + currentUserAvatarUrl);
            holder.commentAvatarInput.setImageResource(R.drawable.ic_user);
        }

        // Hiển thị thời gian và quyền riêng tư
        String time = post.getCreatedAt() != null ? sdf.format(post.getCreatedAt()) : "Không xác định";
        holder.timeTextView.setText(time);
        holder.privacyTextView.setText(post.getPrivacy() != null ? post.getPrivacy() : "PUBLIC");

        // Hiển thị nội dung bài đăng
        holder.contentTextView.setText(post.getContent() != null ? post.getContent() : "");

        // Hiển thị ảnh
        List<String> imageUrls = post.getImageUrl() != null ? post.getImageUrl() : new ArrayList<>();
        List<String> validImageUrls = new ArrayList<>();
        for (String url : imageUrls) {
            if (url != null && !url.isEmpty() && url.contains("/uploads/")) {
                validImageUrls.add(url);
            } else {
                Log.w(TAG, "URL ảnh không hợp lệ cho bài viết " + post.getId() + ": " + url);
            }
        }

        if (!validImageUrls.isEmpty()) {
            Log.d(TAG, "Tải ảnh: " + validImageUrls);
            holder.imagesRecyclerView.setVisibility(View.VISIBLE);
            Log.d(TAG, "imagesRecyclerView visibility: " + (holder.imagesRecyclerView.getVisibility() == View.VISIBLE ? "VISIBLE" : "GONE"));

            int imageCount = validImageUrls.size();
            if (!(holder.imagesRecyclerView.getLayoutManager() instanceof GridLayoutManager)) {
                GridLayoutManager newLayoutManager = new GridLayoutManager(holder.itemView.getContext(), 3);
                holder.imagesRecyclerView.setLayoutManager(newLayoutManager);
            }
            GridLayoutManager layoutManager = (GridLayoutManager) holder.imagesRecyclerView.getLayoutManager();
            if (imageCount == 1 || imageCount == 2 || imageCount >= 4) {
                layoutManager.setSpanCount(imageCount == 1 ? 1 : 2);
            } else if (imageCount == 3) {
                layoutManager.setSpanCount(3);
            }

            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (imageCount == 3) {
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
                holder.imageAdapter = new PostImagesAdapter(validImageUrls, () -> {
                    Log.d(TAG, "Nhấn xem thêm cho bài viết: " + post.getId());
                    try {
                        ImageGalleryDialog dialog = ImageGalleryDialog.newInstance(new ArrayList<>(validImageUrls));
                        dialog.show(fragmentManager, "ImageGalleryDialog");
                    } catch (Exception e) {
                        Log.e(TAG, "Không thể hiển thị ImageGalleryDialog", e);
                    }
                });
                holder.imagesRecyclerView.setAdapter(holder.imageAdapter);
            } else {
                holder.imageAdapter.updateImages(validImageUrls);
            }
            holder.imagesRecyclerView.requestLayout();
        } else {
            Log.d(TAG, "Không có ảnh hợp lệ cho bài viết: " + post.getId());
            holder.imagesRecyclerView.setVisibility(View.GONE);
        }

        // Like và comment count
        holder.likeCountTextView.setText((post.getLikeCount() != 0 ? post.getLikeCount() : 0) + " Lượt thích");
        int commentCount = (post.getComments() != null) ? post.getComments().size() : 0;
        holder.commentCountTextView.setText(commentCount + " Bình luận");
        Log.d(TAG, "Bài viết " + post.getId() + " có " + commentCount + " bình luận");
        // Cập nhật trạng thái nút Like
        boolean isLikedByUser = post.isLikedByUser();
        Log.d(TAG, "HELLO" + isLikedByUser);
        holder.likeButton.setText(isLikedByUser ? "Unlike" : "Like");
        holder.likeButton.setTextColor(holder.itemView.getContext().getResources().getColor(
                isLikedByUser ? R.color.like_button_liked : R.color.like_button_unliked));
        // Comment recycler view
        if (holder.commentAdapter == null) {
            holder.commentAdapter = new CommentAdapter(commentInteractionListener);
            holder.commentsRecyclerView.setAdapter(holder.commentAdapter);
            holder.commentsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            holder.commentsRecyclerView.setNestedScrollingEnabled(false);
        }

        // Hiển thị danh sách phẳng bao gồm cả bình luận gốc và replies
        List<CommentDTO> comments = post.getComments() != null ? post.getComments() : new ArrayList<>();
        List<CommentDTO> flatComments = getFlatCommentList(comments);
        holder.commentAdapter.updateComments(flatComments);
        holder.seeMoreComments.setVisibility(View.GONE);

        // Xử lý sự kiện
        holder.commentSendButton.setOnClickListener(v -> {
            Log.d(TAG, "Nút gửi bình luận được nhấn cho bài viết: id=" + post.getId());
            String commentContent = holder.commentInput.getText().toString().trim();
            if (!commentContent.isEmpty()) {
                Log.d(TAG, "Gửi bình luận: nội dung=" + commentContent + ", postId=" + post.getId());
                commentClickListener.onCommentClick(post, commentContent, null);
                holder.commentInput.setText("");
            } else {
                Log.d(TAG, "Nội dung bình luận trống, không gửi");
                Toast.makeText(holder.itemView.getContext(), "Vui lòng nhập nội dung bình luận", Toast.LENGTH_SHORT).show();
            }
        });

        holder.likeButton.setOnClickListener(v -> likeClickListener.onLikeClick(post));

        holder.commentToggleButton.setOnClickListener(v -> holder.commentInput.requestFocus());

        holder.shareButton.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, post.getContent() != null ? post.getContent() : "");
            holder.itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài viết"));
        });
    }

    private List<CommentDTO> getFlatCommentList(List<CommentDTO> comments) {
        List<CommentDTO> flatList = new ArrayList<>();
        if (comments != null) {
            for (CommentDTO comment : comments) {
                flatList.add(comment);
                if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                    flatList.addAll(comment.getReplies());
                }
            }
        }
        return flatList;
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
        displayedCommentCounts.clear();
        for (PostResponseDTO post : newPosts) {
            displayedCommentCounts.put(post.getId(), INITIAL_COMMENT_COUNT);
        }
        diffResult.dispatchUpdatesTo(this);
    }

    public void addNewPost(PostResponseDTO newPost) {
        Log.d(TAG, "Thêm bài viết mới: id=" + newPost.getId() + ", nội dung=" + newPost.getContent());
        if (newPost.getId() == null) {
            Log.e(TAG, "Bài viết mới có ID null, bỏ qua việc thêm");
            return;
        }
        List<PostResponseDTO> newList = new ArrayList<>();
        newList.add(newPost);
        newList.addAll(postList);
        displayedCommentCounts.put(newPost.getId(), INITIAL_COMMENT_COUNT);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostDiffCallback(postList, newList));
        postList.clear();
        postList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
        Log.d(TAG, "Kích thước danh sách bài viết sau khi thêm: " + postList.size() + ", danh sách id: " +
                postList.stream().map(p -> String.valueOf(p.getId())).collect(Collectors.joining(", ")));
    }

    public void addOlderPosts(List<PostResponseDTO> olderPosts) {
        Log.d(TAG, "Adding " + olderPosts.size() + " older posts");
        List<PostResponseDTO> newList = new ArrayList<>(olderPosts);
        newList.addAll(postList);
        for (PostResponseDTO post : olderPosts) {
            displayedCommentCounts.put(post.getId(), INITIAL_COMMENT_COUNT);
        }
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostDiffCallback(postList, newList));
        postList.clear();
        postList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public void updatePost(Long postId, PostResponseDTO updatedPost) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId().equals(postId)) {
                postList.set(i, updatedPost);
                displayedCommentCounts.put(postId, INITIAL_COMMENT_COUNT);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public int getDisplayedCommentCount(Long postId) {
        return displayedCommentCounts.getOrDefault(postId, INITIAL_COMMENT_COUNT);
    }

    public void setDisplayedCommentCount(Long postId, int count) {
        displayedCommentCounts.put(postId, count);
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView, commentAvatarInput;
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
                    oldPost.getContent().equals(newPost.getContent()) &&
                    oldPost.isLikedByUser() == newPost.isLikedByUser();
        }
    }
}