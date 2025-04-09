package com.myjob.real_time_chat_final.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.FriendRequestAdapter;
import com.myjob.real_time_chat_final.api.FriendshipService;
import com.myjob.real_time_chat_final.config.WebSocketManager;
import com.myjob.real_time_chat_final.model.Friendship;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class FriendRequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendRequestAdapter adapter;
    private List<Friendship> friendRequests = new ArrayList<>();
    private WebSocketManager webSocketManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Khởi tạo adapter
        adapter = new FriendRequestAdapter(friendRequests, this::handleFriendRequestAction);
        recyclerView.setAdapter(adapter);

        // Khởi tạo WebSocketManager
        webSocketManager = WebSocketManager.getInstance();

        // Lấy danh sách yêu cầu kết bạn từ server (thực hiện trong ứng dụng thực tế)
        if (getArguments() != null) {
            int userId = getArguments().getInt("USER_ID", -1);
            if (userId != -1) {
                loadFriendRequests(userId);
            } else {
                Log.e("FriendListFragment", "Invalid User ID");
                // For testing, you can use a default ID
                loadFriendRequests(userId);
            }
        } else {
            Log.e("FriendListFragment", "No User ID received");
            // For testing, you can use a default ID
        }
    }

    private void loadFriendRequests(int userId) {
          // Bạn cần thay thế bằng ID thực tế của người dùng đang đăng nhập

        // Gọi API thông qua Retrofit
        FriendshipService friendshipService = RetrofitClient.getInstance().create(FriendshipService.class);
        Call<List<Friendship>> call = friendshipService.getFriendRequestsReceived(userId);

        // Thực hiện call và xử lý kết quả trả về
        call.enqueue(new retrofit2.Callback<List<Friendship>>() {
            @Override
            public void onResponse(Call<List<Friendship>> call, retrofit2.Response<List<Friendship>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Cập nhật danh sách yêu cầu kết bạn vào RecyclerView
                    friendRequests.clear();
                    friendRequests.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    // Xử lý khi không nhận được phản hồi đúng
                    Toast.makeText(requireContext(), "Không thể lấy yêu cầu kết bạn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Friendship>> call, Throwable t) {
                // Xử lý lỗi nếu có
                Log.e("FriendRequestFragment", "Error loading friend requests: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối với server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addFriendRequest(Friendship friendship) {
        if (friendship != null) {
            friendRequests.add(friendship);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void handleFriendRequestAction(Friendship friendship, boolean isAccepted) {
        // Xử lý hành động chấp nhận hoặc từ chối yêu cầu kết bạn
        if (isAccepted) {
            // Chấp nhận yêu cầu kết bạn
            friendship.setStatus("Accepted");
            sendFriendRequestResponse(friendship, true);
        } else {
            // Từ chối yêu cầu kết bạn
            friendship.setStatus("Rejected");
            sendFriendRequestResponse(friendship, false);
        }

        // Cập nhật UI
        int position = friendRequests.indexOf(friendship);
        if (position != -1) {
            friendRequests.remove(position);
            adapter.notifyItemRemoved(position);

            // Hiển thị thông báo
            String message = isAccepted
                    ? "Đã chấp nhận yêu cầu kết bạn từ " + friendship.getSenderId().getUsername()
                    : "Đã từ chối yêu cầu kết bạn từ " + friendship.getSenderId().getUsername();
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendFriendRequestResponse(Friendship friendship, boolean isAccepted) {
        // Gửi phản hồi về server thông qua WebSocket
        String endpoint = isAccepted ? "/app/acceptFriendRequest" : "/app/rejectFriendRequest";
        webSocketManager.sendRequest(new Gson().toJson(friendship), endpoint);
    }
}