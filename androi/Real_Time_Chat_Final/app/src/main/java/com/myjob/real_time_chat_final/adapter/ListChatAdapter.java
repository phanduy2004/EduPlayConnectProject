package com.myjob.real_time_chat_final.adapter;

import android.util.Log;
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
import com.myjob.real_time_chat_final.modelDTO.ContactDTO;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;
import com.myjob.real_time_chat_final.ui.LoginActivity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.UserViewHolder> {
    private List<ContactDTO> chatList;
    private OnItemClickListener listener;

    public ListChatAdapter(List<ContactDTO> chatList, OnItemClickListener listener) {
        this.chatList = chatList != null ? chatList : new ArrayList<>();
        this.listener = listener;
    }

    public void updateData(List<ContactDTO> body) {
        if (body != null) {
            this.chatList.clear();
            this.chatList.addAll(body);
            notifyDataSetChanged();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ContactDTO user);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        ContactDTO conversations = chatList.get(position);

        // Hiển thị tên người dùng
        holder.userName.setText(conversations.getUsername() != null ? conversations.getUsername() : "Unknown");

        // Hiển thị trạng thái online/offline
        holder.userStatus.setVisibility(conversations.isStatus() ? View.VISIBLE : View.GONE);
        if (conversations.getLastMessageSenderId() != LoginActivity.userid) {
            String senderName = conversations.getLastMessageSenderName() != null ? conversations.getLastMessageSenderName() + ": " : "";
            holder.lastMessageSender.setText(senderName);
        } else {
            holder.lastMessageSender.setText("You: ");
        }
        // Hiển thị tin nhắn cuối cùng
        holder.lastMessage.setText(conversations.getLastMessage() != null ? conversations.getLastMessage() : "Chưa có tin nhắn");
        Log.d("LastMessageTime", "Time got from backend: " + conversations.getLastMessageTime()); // Kiểm tra giá trị thô
        // Hiển thị thời gian tin nhắn cuối cùng
        Timestamp lastMessageTime = conversations.getLastMessageTime();
        if (lastMessageTime != null) {
            long timeMillis = lastMessageTime.getTime() - 7*60*60*1000;
            Log.d("LastMessageTime", "Time: " + timeMillis); // Kiểm tra giá trị thô
            holder.lastMessageTime.setText(getRelativeTime(timeMillis));
        } else {
            holder.lastMessageTime.setText("Chưa có tin nhắn");
        }

        // Tải ảnh đại diện bằng Glide
        String baseUrl = RetrofitClient.getBaseUrl(); // Ví dụ: http://10.0.2.2:8686/
        String avatarUrl = conversations.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            String fullAvatarUrl = baseUrl + avatarUrl;
            Glide.with(holder.itemView.getContext())
                    .load(fullAvatarUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(holder.avatar);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.ic_user)
                    .circleCrop()
                    .into(holder.avatar);
        }

        // Xử lý click vào item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(conversations);
            }
        });
    }

    private String getRelativeTime(long timeMillis) {
        long now = System.currentTimeMillis();
        long diff = now - timeMillis;

        // In ra log để kiểm tra giá trị diff và thời gian hiện tại
        Log.d("RelativeTime", "Current time: " + now);
        Log.d("RelativeTime", "Time difference (ms): " + diff);

        if (diff < 0) {
            // Thời gian trong tương lai (có thể do sai lệch đồng hồ)
            Log.d("RelativeTime", "Vừa xong (thời gian tương lai)");
            return "Vừa xong";
        } else if (diff < 60 * 1000) { // dưới 1 phút
            Log.d("RelativeTime", "Vừa xong");
            return "Vừa xong";
        } else if (diff < 60 * 60 * 1000) { // dưới 1 giờ
            long minutes = diff / (60 * 1000);
            Log.d("RelativeTime", minutes + " phút trước");
            return minutes + " phút trước";
        } else if (diff < 24 * 60 * 60 * 1000) { // dưới 24 giờ
            long hours = diff / (60 * 60 * 1000);
            Log.d("RelativeTime", hours + " giờ trước");
            return hours + " giờ trước";
        } else if (diff < 48 * 60 * 60 * 1000) { // dưới 48 giờ => hôm qua
            Log.d("RelativeTime", "Hôm qua");
            return "Hôm qua";
        } else {
            // Hiển thị ngày cụ thể thay vì "X ngày trước"
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = sdf.format(timeMillis);
            Log.d("RelativeTime", "Ngày: " + formattedDate);
            return formattedDate;
        }
    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, lastMessage, lastMessageTime,lastMessageSender;
        ShapeableImageView avatar;
        View userStatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            userName = itemView.findViewById(R.id.user_name);
            lastMessage = itemView.findViewById(R.id.last_message);
            lastMessageTime = itemView.findViewById(R.id.last_message_time);
            userStatus = itemView.findViewById(R.id.user_status);
            lastMessageSender = itemView.findViewById(R.id.last_message_sender);
        }
    }
}