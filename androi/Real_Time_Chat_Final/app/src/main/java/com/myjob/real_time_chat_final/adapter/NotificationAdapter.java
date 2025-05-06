package com.myjob.real_time_chat_final.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.modelDTO.NotificationDTO;
import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<NotificationDTO> notificationList;
    private final Context context;
    private final OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationDTO notification);
    }

    public NotificationAdapter(Context context, OnNotificationClickListener listener) {
        this.context = context;
        this.notificationList = new ArrayList<>();
        this.listener = listener;
    }

    public void updateNotifications(List<NotificationDTO> newNotifications) {
        NotificationDiffCallback diffCallback = new NotificationDiffCallback(notificationList, newNotifications);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        notificationList.clear();
        notificationList.addAll(newNotifications);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationDTO notification = notificationList.get(position);

        // Hiển thị nội dung thông báo
        holder.contentTextView.setText(notification.getContent());

        // Hiển thị thời gian tương đối
        long timeMillis = notification.getCreatedAt().getTime();
        String relativeTime = DateUtils.getRelativeTimeSpanString(
                timeMillis, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
        holder.timeTextView.setText(relativeTime);

        // Hiển thị biểu tượng theo loại thông báo
        switch (notification.getType()) {
            case "LIKE":
                holder.icon.setImageResource(R.drawable.ic_like);
                break;
            case "COMMENT":
                holder.icon.setImageResource(R.drawable.ic_comment);
                break;
            case "REPLY":
                holder.icon.setImageResource(R.drawable.ic_reply);
                break;
            default:
                holder.icon.setImageResource(R.drawable.ic_notifications);
        }

        // Hiển thị avatar người dùng
        String avatarUrl = notification.getUserAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(context)
                    .load(avatarUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .into(holder.userAvatar);
        } else {
            holder.userAvatar.setImageResource(R.drawable.ic_user);
        }

        // Hiển thị dấu chấm chưa đọc
        holder.unreadIndicator.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);

        // Xử lý click vào thông báo
        holder.itemView.setOnClickListener(v -> {
            listener.onNotificationClick(notification);
            if (!notification.isRead()) {
                notification.setRead(true);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        ImageView userAvatar;
        TextView contentTextView;
        TextView timeTextView;
        View unreadIndicator;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.notification_icon);
            userAvatar = itemView.findViewById(R.id.user_avatar);
            contentTextView = itemView.findViewById(R.id.notification_content);
            timeTextView = itemView.findViewById(R.id.notification_time);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);
        }
    }

    static class NotificationDiffCallback extends DiffUtil.Callback {
        private final List<NotificationDTO> oldList;
        private final List<NotificationDTO> newList;

        public NotificationDiffCallback(List<NotificationDTO> oldList, List<NotificationDTO> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            NotificationDTO oldNotification = oldList.get(oldItemPosition);
            NotificationDTO newNotification = newList.get(newItemPosition);
            return oldNotification.getContent().equals(newNotification.getContent())
                    && oldNotification.getType().equals(newNotification.getType())
                    && oldNotification.getCreatedAt().equals(newNotification.getCreatedAt())
                    && oldNotification.isRead() == newNotification.isRead()
                    && (oldNotification.getUserAvatarUrl() != null
                    ? oldNotification.getUserAvatarUrl().equals(newNotification.getUserAvatarUrl())
                    : newNotification.getUserAvatarUrl() == null);
        }
    }
}