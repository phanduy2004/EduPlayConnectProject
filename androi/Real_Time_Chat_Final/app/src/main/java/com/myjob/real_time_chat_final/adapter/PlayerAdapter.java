package com.myjob.real_time_chat_final.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.GameRoomPlayer;
import com.myjob.real_time_chat_final.model.User;

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

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerAvatar = itemView.findViewById(R.id.playerAvatar);
            playerName = itemView.findViewById(R.id.playerName);
            playerStatus = itemView.findViewById(R.id.playerStatus);
        }
    }
}
