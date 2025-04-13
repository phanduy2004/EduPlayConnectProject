package com.myjob.real_time_chat_final.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.GameRoomPlayer;

import java.util.ArrayList;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {
    private List<GameRoomPlayer> rankings = new ArrayList<>();

    public void updateRankings(List<GameRoomPlayer> rankings) {
        this.rankings = rankings;
        notifyDataSetChanged();
    }

    @Override
    public RankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RankingViewHolder holder, int position) {
        GameRoomPlayer player = rankings.get(position);

        // Bind thứ hạng
        holder.tvRank.setText(String.valueOf(position + 1));

        // Bind tên người chơi
        String username = player.getUser() != null ? player.getUser().getUsername() : "Unknown";
        holder.tvPlayerName.setText(username);

        // Bind điểm số
        holder.tvScore.setText(String.valueOf(player.getScore()));

        // Bind trạng thái (giả định online nếu không có dữ liệu)
        boolean isOnline = player.getUser() != null && player.getUser().isStatus();
        holder.tvStatus.setText(isOnline ? "Online" : "Offline");
        holder.tvStatus.setTextColor(isOnline ? 0xFF00FFAA : 0xFFFF0000); // Xanh cho Online, đỏ cho Offline

        // Bind avatar
        if (player.getUser() != null && player.getUser().getAvatarUrl() != null && !player.getUser().getAvatarUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(player.getUser().getAvatarUrl())
                    .into(holder.ivAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return rankings.size();
    }

    static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvPlayerName, tvStatus, tvScore;
        ImageView ivAvatar;

        RankingViewHolder(View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvPlayerName = itemView.findViewById(R.id.tv_player_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvScore = itemView.findViewById(R.id.tv_score);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
        }
    }
}