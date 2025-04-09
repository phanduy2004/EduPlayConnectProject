package com.myjob.real_time_chat_final.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.Friendship;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    private List<Friendship> friendRequests;
    private OnFriendRequestActionListener listener;

    public interface OnFriendRequestActionListener {
        void onFriendRequestAction(Friendship friendship, boolean isAccepted);
    }

    public FriendRequestAdapter(List<Friendship> friendRequests, OnFriendRequestActionListener listener) {
        this.friendRequests = friendRequests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friendship friendship = friendRequests.get(position);
        holder.bind(friendship, listener);
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private Button acceptButton;
        private Button declineButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.request_sender_name);
            acceptButton = itemView.findViewById(R.id.btn_accept);
            declineButton = itemView.findViewById(R.id.btn_reject);
        }

        public void bind(Friendship friendship, OnFriendRequestActionListener listener) {
            // Hiển thị tên người gửi yêu cầu kết bạn
            nameTextView.setText(friendship.getSenderId().getUsername());

            // Thiết lập sự kiện cho nút chấp nhận
            acceptButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFriendRequestAction(friendship, true);
                }
            });

            // Thiết lập sự kiện cho nút từ chối
            declineButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFriendRequestAction(friendship, false);
                }
            });
        }
    }
}