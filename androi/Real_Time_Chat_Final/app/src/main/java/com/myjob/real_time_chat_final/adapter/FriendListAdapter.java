package com.myjob.real_time_chat_final.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.User;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private List<User> friends;
    private OnFriendClickListener listener;

    public interface OnFriendClickListener {
        void onFriendClick(User friend);
    }

    public FriendListAdapter(List<User> friends) {
        this.friends = friends;
    }

    public FriendListAdapter(List<User> friends, OnFriendClickListener listener) {
        this.friends = friends;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (friends != null && position < friends.size()) {
            holder.bind(friends.get(position), listener);
        }
    }

    @Override
    public int getItemCount() {
        return friends != null ? friends.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView statusTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.friend_name);
            statusTextView = itemView.findViewById(R.id.friend_status);
        }

        public void bind(User friend, OnFriendClickListener listener) {
            if (friend == null) {
                nameTextView.setText("Unknown");
                statusTextView.setText("No status");
                return;
            }

            nameTextView.setText(friend.getUsername());
            statusTextView.setText(friend.isStatus() ? "Online" : "Offline");
            statusTextView.setTextColor(friend.isStatus() ? 0xFF00FFAA : 0xFFFF0000); // Xanh cho Online, đỏ cho Offline
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFriendClick(friend);
                }
            });
        }
    }
}