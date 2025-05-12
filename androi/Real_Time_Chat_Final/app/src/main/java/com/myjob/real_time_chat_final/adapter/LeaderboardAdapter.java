package com.myjob.real_time_chat_final.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.modelDTO.ScoreDTO;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private List<ScoreDTO> scores;

    public LeaderboardAdapter(List<ScoreDTO> scores) {
        this.scores = scores != null ? scores : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard_math, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder.avatar != null) {
            if (position < scores.size()) {
                ScoreDTO score = scores.get(position);
                holder.rank.setText(String.valueOf(position + 1));
                holder.name.setText(score.getUsername() != null ? score.getUsername() : "Unknown");
                holder.score.setText(String.valueOf(score.getScore()));

                // Tải ảnh đại diện từ avatarUrl
                String avatarUrl = score.getAvatarUrl();
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    // Nếu avatarUrl là đường dẫn tương đối, thêm base URL của server
                    String fullAvatarUrl = "http://10.0.2.2:8686" + avatarUrl;
                    Glide.with(holder.itemView.getContext())
                            .load(fullAvatarUrl)
                            .placeholder(R.drawable.ic_user)
                            .error(R.drawable.ic_user)
                            .into(holder.avatar);
                } else {
                    holder.avatar.setImageResource(R.drawable.ic_user);
                }
            } else {
                // Hiển thị hàng giả lập nếu không đủ dữ liệu
                holder.rank.setText(String.valueOf(position + 1));
                holder.avatar.setImageResource(R.drawable.ic_user);
                holder.name.setText("Unknown");
                holder.score.setText("0");
            }
        }
    }

    @Override
    public int getItemCount() {
        return 5; // Luôn hiển thị 5 hàng
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name;
        TextView score;
        TextView rank;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.tv_rank);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            score = itemView.findViewById(R.id.score);
        }
    }
}