package com.myjob.real_time_chat_final.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.GameRoomPlayer;

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