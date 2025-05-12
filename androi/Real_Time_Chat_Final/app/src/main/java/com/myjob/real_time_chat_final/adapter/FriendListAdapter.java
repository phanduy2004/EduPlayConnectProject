package com.myjob.real_time_chat_final.adapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.User;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendViewHolder> {

    private List<User> friends;
    private final OnFriendClickListener clickListener;
    private final OnFriendDeleteListener deleteListener;

    public interface OnFriendClickListener {
        void onFriendClick(User friend);
    }

    public interface OnFriendDeleteListener {
        void onFriendDelete(User friend);
    }

    public FriendListAdapter(List<User> friends, OnFriendClickListener clickListener, OnFriendDeleteListener deleteListener) {
        this.friends = friends;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        User friend = friends.get(position);
        holder.friendName.setText(friend.getUsername());
        //holder.friendStatus.setText(friend.getStatus() != null ? friend.getStatus() : "Ngoại tuyến");
        String avatarUrl = friend.getAvatarUrl() != null && !friend.getAvatarUrl().isEmpty()
                ? RetrofitClient.getBaseUrl() + friend.getAvatarUrl() : null;
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
                    .into(holder.avatar);
        } else {
            Log.w(TAG, "URL avatar không hợp lệ: " + avatarUrl);
            holder.avatar.setImageResource(R.drawable.ic_user);
        }
        holder.btnMessage.setOnClickListener(v -> clickListener.onFriendClick(friend));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onFriendDelete(friend));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        TextView friendStatus;
        ImageButton btnMessage;
        ImageButton btnDelete;
        ImageView avatar;

        FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friend_name);
            friendStatus = itemView.findViewById(R.id.friend_status);
            btnMessage = itemView.findViewById(R.id.btn_message);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            avatar =itemView.findViewById(R.id.avatar);
        }
    }
}