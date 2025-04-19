package com.myjob.real_time_chat_final.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.User;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private List<User> friends;
    private OnFriendClickListener clickListener;
    private OnFriendDeleteListener deleteListener;

    public interface OnFriendClickListener {
        void onFriendClick(User friend);
    }

    public interface OnFriendDeleteListener {
        void onFriendDelete(User friend);
    }

    public FriendListAdapter(List<User> friends) {
        this.friends = friends;
    }

    public FriendListAdapter(List<User> friends, OnFriendClickListener clickListener, OnFriendDeleteListener deleteListener) {
        this.friends = friends;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
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
            holder.bind(friends.get(position), clickListener, deleteListener);
        }
    }

    @Override
    public int getItemCount() {
        return friends != null ? friends.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView statusTextView;
        private ImageButton btnMessage;
        private ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.friend_name);
            statusTextView = itemView.findViewById(R.id.friend_status);
            btnMessage = itemView.findViewById(R.id.btn_message);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(User friend, OnFriendClickListener clickListener, OnFriendDeleteListener deleteListener) {
            if (friend == null) {
                nameTextView.setText("Unknown");
                statusTextView.setText("No status");
                btnMessage.setEnabled(false);
                btnDelete.setEnabled(false);
                return;
            }

            nameTextView.setText(friend.getUsername());
            statusTextView.setText(friend.isStatus() ? "Online" : "Offline");
            statusTextView.setTextColor(friend.isStatus() ? 0xFF00FFAA : 0xFFFF0000);

            btnMessage.setOnClickListener(v -> {
                Log.d("FriendListAdapter", "Nhấn nhắn tin, friendId: " + friend.getId());
                if (clickListener != null) {
                    clickListener.onFriendClick(friend);
                }
            });

            btnDelete.setOnClickListener(v -> {
                Log.d("FriendListAdapter", "Nhấn xóa, friendId: " + friend.getId());
                if (deleteListener != null) {
                    deleteListener.onFriendDelete(friend);
                } else {
                    Log.e("FriendListAdapter", "deleteListener null");
                }
            });
        }
    }
}