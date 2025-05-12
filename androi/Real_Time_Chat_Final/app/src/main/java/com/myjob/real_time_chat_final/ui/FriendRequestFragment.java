package com.myjob.real_time_chat_final.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class FriendRequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendRequestAdapter adapter;
    private List<Friendship> friendRequests = new ArrayList<>();
    private List<Friendship> filteredRequests = new ArrayList<>();
    private WebSocketManager webSocketManager;
    private TextView emptyView;
    private FriendRequestListener listener; // Thêm listener
    private FriendListFragment FriendListFragment;
    public void setFriendRequestListener(FriendRequestListener listener) {
        this.listener = listener;
    }

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
        if (recyclerView == null) {
            Log.e("FriendRequestFragment", "RecyclerView is null - check layout file");
            Toast.makeText(requireContext(), "Lỗi: Không tìm thấy RecyclerView", Toast.LENGTH_LONG).show();
            return;
        }
        emptyView = view.findViewById(R.id.empty_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Khởi tạo adapter
        adapter = new FriendRequestAdapter(filteredRequests, this::handleFriendRequestAction);
        recyclerView.setAdapter(adapter);

        // Khởi tạo WebSocketManager
        webSocketManager = WebSocketManager.getInstance();

        // Lấy danh sách yêu cầu kết bạn từ server
        if (getArguments() != null) {
            int userId = getArguments().getInt("USER_ID", -1);
            if (userId != -1) {
                loadFriendRequests(userId);
            } else {
                Log.e("FriendRequestFragment", "Invalid User ID");
                Toast.makeText(requireContext(), "Lỗi: Không có ID người dùng", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("FriendRequestFragment", "No User ID received");
            Toast.makeText(requireContext(), "Lỗi: Không nhận được ID người dùng", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFriendRequests(int userId) {
        FriendshipService friendshipService = RetrofitClient.getInstance().create(FriendshipService.class);
        Call<List<Friendship>> call = friendshipService.getFriendRequestsReceived(userId);

        call.enqueue(new retrofit2.Callback<List<Friendship>>() {
            @Override
            public void onResponse(Call<List<Friendship>> call, retrofit2.Response<List<Friendship>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    friendRequests.clear();
                    filteredRequests.clear();
                    friendRequests.addAll(response.body());
                    filteredRequests.addAll(friendRequests);
                    updateEmptyView();
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "Không thể lấy yêu cầu kết bạn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Friendship>> call, Throwable t) {
                Log.e("FriendRequestFragment", "Error loading friend requests: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối với server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addFriendRequest(Friendship friendship) {
        if (friendship != null) {
            friendRequests.add(friendship);
            filteredRequests.add(friendship);
            updateEmptyView();
            adapter.notifyDataSetChanged();
        }
    }

    public void filterRequests(String query) {
        filteredRequests.clear();
        if (query.isEmpty()) {
            filteredRequests.addAll(friendRequests);
        } else {
            for (Friendship request : friendRequests) {
                if (request.getSenderId() != null &&
                        request.getSenderId().getUsername().toLowerCase().contains(query.toLowerCase())) {
                    filteredRequests.add(request);
                }
            }
        }
        updateEmptyView();
        adapter.notifyDataSetChanged();
    }

    private void updateEmptyView() {
        if (filteredRequests.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void handleFriendRequestAction(Friendship friendship, boolean isAccepted) {
        friendship.setCreatedAt(null);
        friendship.setStatus(isAccepted ? "Accepted" : "Rejected");
        sendFriendRequestResponse(friendship, isAccepted);

        int position = filteredRequests.indexOf(friendship);
        if (position != -1) {
            friendRequests.remove(friendship);
            filteredRequests.remove(position);
            updateEmptyView();
            adapter.notifyItemRemoved(position);

            String message = isAccepted
                    ? "Đã chấp nhận yêu cầu kết bạn từ " + friendship.getSenderId().getUsername()
                    : "Đã từ chối yêu cầu kết bạn từ " + friendship.getSenderId().getUsername();

            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            if (isAccepted && listener != null) {
                listener.onFriendRequestAccepted(getArguments().getInt("USER_ID", -1));
            }
        }
    }

    private void sendFriendRequestResponse(Friendship friendship, boolean isAccepted) {
        String endpoint = isAccepted ? "/app/acceptFriendRequest" : "/app/rejectFriendRequest";
        webSocketManager.sendRequest(new Gson().toJson(friendship), endpoint);
    }

}
