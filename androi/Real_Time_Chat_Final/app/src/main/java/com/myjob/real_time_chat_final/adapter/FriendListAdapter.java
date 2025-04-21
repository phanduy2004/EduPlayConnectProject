package com.myjob.real_time_chat_final.adapter;

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

        FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friend_name);
            friendStatus = itemView.findViewById(R.id.friend_status);
            btnMessage = itemView.findViewById(R.id.btn_message);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}