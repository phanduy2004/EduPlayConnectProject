package com.myjob.real_time_chat_final.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.User;

import java.util.ArrayList;
import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {
    private final List<User> userList; // Danh sách đầy đủ
    private final List<User> filteredUserList; // Danh sách đã lọc
    private final List<User> selectedUsers;
    private final OnUserSelectedListener listener;

    public MembersAdapter(List<User> userList, List<User> selectedUsers, OnUserSelectedListener listener) {
        this.userList = userList;
        this.filteredUserList = new ArrayList<>(userList); // Khởi tạo danh sách lọc giống danh sách gốc
        this.selectedUsers = selectedUsers;
        this.listener = listener;
    }

    public interface OnUserSelectedListener {
        void onUserSelected(User user);
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_select, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        User user = filteredUserList.get(position);
        holder.userName.setText(user.getUsername() != null ? user.getUsername() : "Unknown");
        holder.userCheckbox.setChecked(selectedUsers.contains(user));
        holder.itemView.setOnClickListener(v -> {
            holder.userCheckbox.setChecked(!holder.userCheckbox.isChecked());
            listener.onUserSelected(user);
        });
        holder.userCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onUserSelected(user));
    }

    @Override
    public int getItemCount() {
        return filteredUserList.size();
    }

    // Phương thức để lọc danh sách người dùng dựa trên từ khóa
    public void filter(String query) {
        filteredUserList.clear();
        if (query.isEmpty()) {
            filteredUserList.addAll(userList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (User user : userList) {
                if (user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerQuery)) {
                    filteredUserList.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CheckBox userCheckbox;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userCheckbox = itemView.findViewById(R.id.user_checkbox);
        }
    }
}