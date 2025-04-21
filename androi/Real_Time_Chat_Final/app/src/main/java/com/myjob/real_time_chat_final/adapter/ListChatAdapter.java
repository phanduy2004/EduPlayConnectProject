package com.myjob.real_time_chat_final.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.ListChat;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.UserViewHolder> {
    private List<ListChat> chatList;
    private OnItemClickListener listener;

    public ListChatAdapter(List<ListChat> chatList, OnItemClickListener listener) {
        this.chatList = chatList != null ? chatList : new ArrayList<>();
        this.listener = listener;
    }

    public void updateData(List<ListChat> body) {
        if (body != null) {
            this.chatList.clear();
            this.chatList.addAll(body);
            notifyDataSetChanged();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ListChat user);
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

        // Hiển thị tên người dùng
        holder.userName.setText(user.getUsername() != null ? user.getUsername() : "Unknown");

        // Hiển thị trạng thái online/offline
        holder.userStatus.setVisibility(user.getStatus() ? View.VISIBLE : View.GONE);

        // Tải ảnh đại diện bằng Glide
        String baseUrl ="http://10.0.2.2:8686"; // Ví dụ: http://10.0.2.2:8686/
        String avatarUrl = user.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            String fullAvatarUrl = baseUrl + avatarUrl; // Ghép baseUrl với avatarUrl
            Glide.with(holder.itemView.getContext())
                    .load(fullAvatarUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_user) // Ảnh placeholder trong khi tải
                    .error(R.drawable.ic_user) // Ảnh mặc định nếu tải thất bại
                    .into(holder.avatar);
        } else {
            // Hiển thị ảnh mặc định nếu không có avatarUrl
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.ic_user)
                    .circleCrop()
                    .into(holder.avatar);
        }

        // Xử lý click vào item
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
        TextView userName, lastMessage, lastMessageTime;
        ShapeableImageView avatar;
        View userStatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            userName = itemView.findViewById(R.id.user_name);
            lastMessage = itemView.findViewById(R.id.last_message);
            lastMessageTime = itemView.findViewById(R.id.last_message_time);
            userStatus = itemView.findViewById(R.id.user_status);
        }
    }
}