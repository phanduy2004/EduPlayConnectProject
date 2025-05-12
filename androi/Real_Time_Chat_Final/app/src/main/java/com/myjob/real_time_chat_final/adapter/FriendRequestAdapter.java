package com.myjob.real_time_chat_final.adapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.Friendship;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
        private TextView nameTextView, requestTimeTexView;
        private Button acceptButton;
        private Button declineButton;
        private ImageView avatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.request_sender_name);
            acceptButton = itemView.findViewById(R.id.btn_accept);
            declineButton = itemView.findViewById(R.id.btn_reject);
            avatar = itemView.findViewById(R.id.avatar);
            requestTimeTexView = itemView.findViewById(R.id.request_time);
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
        public void bind(Friendship friendship, OnFriendRequestActionListener listener) {
            // Hiển thị tên người gửi yêu cầu kết bạn
            nameTextView.setText(friendship.getSenderId().getUsername());
            String avatarUrl = friendship.getSenderId().getAvatarUrl() != null && !friendship.getSenderId().getAvatarUrl().isEmpty()
                    ? RetrofitClient.getBaseUrl() + friendship.getSenderId().getAvatarUrl() : null;
            Log.d(TAG, "Tải URL avatar: " + avatarUrl);
            if (avatarUrl != null && avatarUrl.contains("/uploads/")) {
                Glide.with(itemView.getContext())
                        .load(avatarUrl)
                        .thumbnail(0.25f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_user)
                        .circleCrop()
                        .override(100, 100)
                        .error(R.drawable.ic_user)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e,
                                                        Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e(TAG, "Tải avatar thất bại: " + avatarUrl, e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                           com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                Log.d(TAG, "Tải avatar thành công: " + avatarUrl);
                                return false;
                            }
                        })
                        .into(avatar);
            } else {
                Log.w(TAG, "URL avatar không hợp lệ: " + avatarUrl);
                avatar.setImageResource(R.drawable.ic_user);
            }
            long timeMillis = friendship.getCreatedAt().getTime();

            requestTimeTexView.setText(getRelativeTime(timeMillis));
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