package com.myjob.real_time_chat_final.adapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.GameRoomPlayer;
import com.myjob.real_time_chat_final.model.User;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<GameRoomPlayer> playerList;
    private String currentUserId;
    private String hostId;

    public PlayerAdapter(List<GameRoomPlayer> playerList, String currentUserId, String hostId) {
        this.playerList = playerList;
        this.currentUserId = currentUserId;
        this.hostId = hostId;
    }

    public void updatePlayers(List<GameRoomPlayer> updatedList, String hostId) {
        this.playerList.clear();
        this.playerList.addAll(updatedList);
        this.hostId = hostId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        GameRoomPlayer gameRoomPlayer = playerList.get(position);
        User user = gameRoomPlayer.getUser();
        if (user == null) return;

        String userId = String.valueOf(user.getId());
        boolean isCurrentUser = userId.equals(currentUserId);
        boolean isHost = userId.equals(hostId);

        StringBuilder displayName = new StringBuilder(user.getUsername());
        if (isCurrentUser) {
            displayName.append(" (You)");
        }
        if (isHost) {
            displayName.append(" (Admin)");
        }
        String avatarUrl = user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()
                ? RetrofitClient.getBaseUrl() + user.getAvatarUrl() : null;
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
                    .into(holder.playerAvatar);
        } else {
            Log.w(TAG, "URL avatar không hợp lệ: " + avatarUrl);
            holder.playerAvatar.setImageResource(R.drawable.ic_user);
        }
        holder.playerName.setText(displayName.toString());

        if (isHost) {
            holder.playerStatus.setText("Room Admin");
            holder.playerStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                    android.R.color.holo_blue_dark));
        } else {
            holder.playerStatus.setText(gameRoomPlayer.isReady() ? "Ready" : "Not Ready");
            holder.playerStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                    gameRoomPlayer.isReady() ? android.R.color.holo_green_dark : android.R.color.holo_red_light));
        }
    }

    @Override
    public int getItemCount() {
        return playerList != null ? playerList.size() : 0;
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        ImageView playerAvatar;
        TextView playerName;
        TextView playerStatus;
        ImageView avatar;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerAvatar = itemView.findViewById(R.id.playerAvatar);
            playerName = itemView.findViewById(R.id.playerName);
            playerStatus = itemView.findViewById(R.id.playerStatus);

        }
    }
}
