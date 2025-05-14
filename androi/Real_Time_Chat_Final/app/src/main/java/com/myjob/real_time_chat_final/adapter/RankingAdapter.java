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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.GameRoomPlayer;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {
    private List<GameRoomPlayer> rankings = new ArrayList<>();

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        GameRoomPlayer player = rankings.get(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvPlayerName.setText(player.getUser() != null ? player.getUser().getUsername() : "Unknown");
        String avatarUrl = player.getUser().getAvatarUrl() != null && !player.getUser().getAvatarUrl().isEmpty()
                ? RetrofitClient.getBaseUrl() + player.getUser().getAvatarUrl() : null;
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
                    .into(holder.ivAvatar);
        } else {
            Log.w(TAG, "URL avatar không hợp lệ: " + avatarUrl);
            holder.ivAvatar.setImageResource(R.drawable.ic_user);
        }
        holder.tvScore.setText(String.valueOf(player.getScore()));
        holder.tvStatus.setText(player.getUser() != null && player.getUser().isStatus() ? "Online" : "Offline");
    }

    @Override
    public int getItemCount() {
        return rankings.size();
    }

    public void updateRankings(List<GameRoomPlayer> newRankings) {
        this.rankings = new ArrayList<>(newRankings);
        notifyDataSetChanged();
    }

    static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvPlayerName, tvScore, tvStatus;
        ImageView ivAvatar;

        RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvPlayerName = itemView.findViewById(R.id.tv_player_name);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvStatus = itemView.findViewById(R.id.tv_status);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
        }
    }
}