package com.myjob.real_time_chat_final.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.Message;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
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
            return 0; // Giả sử tin nhắn này là từ người khác nếu không xác định được sender
        }

        int senderId = message.getSender().getId();
        Log.d("MessageAdapter", "senderId: " + senderId + "Message"+message.getMessage() + " - currentUserId: " + currentUserId);
        return (senderId == currentUserId) ? 1 : 0;
    }





    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == 1) ? R.layout.item_message_sent : R.layout.item_message_received;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageText.setText(message.getMessage());
        holder.messageTime.setText(formatTime(message.getTimestamp())); // Format lại thời gian
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



    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ✅ Thêm phương thức addMessage để cập nhật tin nhắn mới
    public void addMessage(Message newMessage) {
        messageList.add(newMessage);
        notifyItemInserted(messageList.size() - 1);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, messageTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            messageTime = itemView.findViewById(R.id.message_time);
        }
    }
}
