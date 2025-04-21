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
import com.myjob.real_time_chat_final.model.Message;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 0;

    private final List<Message> messageList;
    private final int currentUserId; // ID của người dùng hiện tại

    public MessageAdapter(List<Message> messageList, int currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);

        if (message.getSender() == null) {
            Log.e("MessageAdapter", "Lỗi: sender bị null ở vị trí " + position);
            return VIEW_TYPE_RECEIVED; // Giả sử tin nhắn nhận nếu sender null
        }

        int senderId = message.getSender().getId();
        Log.d("MessageAdapter", "senderId: " + senderId + ", Message: " + message.getMessage() + ", currentUserId: " + currentUserId);
        return (senderId == currentUserId) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // Thêm phương thức addMessage để cập nhật tin nhắn mới
    public void addMessage(Message newMessage) {
        messageList.add(newMessage);
        notifyItemInserted(messageList.size() - 1);
    }

    private String formatTime(Object timestamp) {
        if (timestamp == null) return "N/A";

        try {
            SimpleDateFormat inputFormat;
            String timestampStr = timestamp.toString();

            if (timestampStr.contains("T")) {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            } else {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            }

            Date date = inputFormat.parse(timestampStr);
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Lỗi thời gian";
        }
    }

    // ViewHolder cho tin nhắn gửi
    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, messageTime;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            messageTime = itemView.findViewById(R.id.message_time);
        }

        public void bind(Message message) {
            messageText.setText(message.getMessage() != null ? message.getMessage() : "");
            messageTime.setText(formatTime(message.getTimestamp()));
        }

        private String formatTime(Object timestamp) {
            if (timestamp == null) return "N/A";

            try {
                SimpleDateFormat inputFormat;
                String timestampStr = timestamp.toString();

                if (timestampStr.contains("T")) {
                    inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                } else {
                    inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                }

                Date date = inputFormat.parse(timestampStr);
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return "Lỗi thời gian";
            }
        }
    }

    // ViewHolder cho tin nhắn nhận
    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, messageTime;
        ShapeableImageView avatar;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            messageTime = itemView.findViewById(R.id.message_time);
            avatar = itemView.findViewById(R.id.avatar);
        }

        public void bind(Message message) {
            messageText.setText(message.getMessage() != null ? message.getMessage() : "");
            messageTime.setText(formatTime(message.getTimestamp()));

            // Tải ảnh đại diện
            if (message.getSender() != null && message.getSender().getAvatarUrl() != null && !message.getSender().getAvatarUrl().isEmpty()) {
                String baseUrl = RetrofitClient.getBaseUrl(); // Ví dụ: http://10.0.2.2:8686/
                String avatarUrl = message.getSender().getAvatarUrl();
                if (avatarUrl.startsWith("/")) {
                    avatarUrl = avatarUrl.substring(1);
                }
                String fullAvatarUrl = baseUrl + avatarUrl;
                Log.d("MessageAdapter", "Loading avatar URL: " + fullAvatarUrl);
                Glide.with(itemView.getContext())
                        .load(fullAvatarUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(avatar);
            } else {
                Log.d("MessageAdapter", "No avatar URL for message: " + message.getMessage());
                Glide.with(itemView.getContext())
                        .load(R.drawable.ic_user)
                        .circleCrop()
                        .into(avatar);
            }
        }

        private String formatTime(Object timestamp) {
            if (timestamp == null) return "N/A";

            try {
                SimpleDateFormat inputFormat;
                String timestampStr = timestamp.toString();

                if (timestampStr.contains("T")) {
                    inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                } else {
                    inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                }

                Date date = inputFormat.parse(timestampStr);
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return "Lỗi thời gian";
            }
        }
    }
}