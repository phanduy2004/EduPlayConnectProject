package com.myjob.real_time_chat_final.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.myjob.real_time_chat_final.OnCommentInteractionListener;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.modelDTO.CommentDTO;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.graphics.drawable.Drawable;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<CommentDTO> commentList = new ArrayList<>();
    private final OnCommentInteractionListener interactionListener;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault());
    private static final String TAG = "CommentAdapter";

    public CommentAdapter(OnCommentInteractionListener interactionListener) {
        this.interactionListener = interactionListener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentDTO comment = commentList.get(position);
        int indentLevel = calculateIndentLevel(comment);

        // Thụt vào dựa trên mức độ phân cấp
        holder.itemView.setPadding(indentLevel * 32, 4, 8, 4);

        // Avatar
        String avatarUrl = comment.getAvatarUrl() != null && !comment.getAvatarUrl().isEmpty() ? RetrofitClient.getBaseUrl() + comment.getAvatarUrl() : null;
        Log.d(TAG, "Loading comment avatar URL: " + avatarUrl);
        if (avatarUrl != null && avatarUrl.contains("/uploads/")) {
            Glide.with(holder.itemView.getContext())
                    .load(avatarUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_user)
                    .circleCrop()
                    .override(80, 80)
                    .error(R.drawable.ic_user)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Glide load failed for comment avatar: " + avatarUrl, e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            Log.d(TAG, "Glide load successful for comment avatar: " + avatarUrl);
                            return false;
                        }
                    })
                    .into(holder.avatar);
        } else {
            Log.w(TAG, "Invalid comment avatar URL: " + avatarUrl);
            holder.avatar.setImageResource(R.drawable.ic_user);
        }

        // Username and Content
        holder.username.setText(comment.getUsername() != null ? comment.getUsername() : "Unknown");
        holder.content.setText(comment.getContent() != null ? comment.getContent() : "");

        // Time
        holder.time.setText(comment.getCreatedAt() != null ? sdf.format(comment.getCreatedAt()) : "Unknown");

        // Interaction Buttons
        holder.likeButton.setOnClickListener(v -> interactionListener.onLikeComment(comment));
        holder.replyButton.setOnClickListener(v -> interactionListener.onReplyComment(comment));
        holder.shareButton.setOnClickListener(v -> interactionListener.onShareComment(comment));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    private int calculateIndentLevel(CommentDTO comment) {
        int level = 0;
        CommentDTO current = comment;
        while (current.getParentCommentId() != null) {
            level++;
            current = findCommentById(current.getParentCommentId());
            if (current == null) break;
        }
        return level;
    }

    private CommentDTO findCommentById(Long id) {
        for (CommentDTO comment : commentList) {
            if (comment.getId().equals(id)) {
                return comment;
            }
        }
        return null;
    }

    public void updateComments(List<CommentDTO> newComments) {
        commentList.clear();
        flattenComments(newComments);
        notifyDataSetChanged();
    }

    private void flattenComments(List<CommentDTO> comments) {
        for (CommentDTO comment : comments) {
            commentList.add(comment);
            if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                flattenComments(comment.getReplies());
            }
        }
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView username, content, time, likeButton, replyButton, shareButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.comment_avatar);
            username = itemView.findViewById(R.id.comment_username);
            content = itemView.findViewById(R.id.comment_content);
            time = itemView.findViewById(R.id.comment_time);
            likeButton = itemView.findViewById(R.id.comment_like);
            replyButton = itemView.findViewById(R.id.comment_reply);
            shareButton = itemView.findViewById(R.id.comment_share);
        }
    }
}