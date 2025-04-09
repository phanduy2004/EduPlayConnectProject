package com.myjob.real_time_chat_final.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.ListChat;

import java.util.List;

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.UserViewHolder> {
    private List<ListChat> chatList;
    private OnItemClickListener listener;

    public void updateData(List<ListChat> body) {
        if (body != null) {
            this.chatList.clear(); // Xóa danh sách cũ
            this.chatList.addAll(body); // Thêm danh sách mới
            notifyDataSetChanged(); // Cập nhật RecyclerView
        }
    }


    public interface OnItemClickListener {
        void onItemClick(ListChat user);
    }

    public ListChatAdapter(List<ListChat> userList, OnItemClickListener listener) {
        this.chatList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        ListChat user = chatList.get(position);
        holder.userName.setText(user.getUsername()); // LỖI NẾU userName == null

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, lastMessage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name); // PHẢI CHẮC CHẮN ID NÀY TỒN TẠI TRONG item_user.xml
            lastMessage = itemView.findViewById(R.id.last_message);
        }
    }
}
