package com.myjob.real_time_chat_final.ui;

import android.content.Intent;
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

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.FriendListAdapter;
import com.myjob.real_time_chat_final.api.ConversationService;
import com.myjob.real_time_chat_final.api.FriendshipService;
import com.myjob.real_time_chat_final.config.WebSocketManager;
import com.myjob.real_time_chat_final.model.Conversation;
import com.myjob.real_time_chat_final.model.User;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class FriendListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendListAdapter adapter;
    private List<User> friends = new ArrayList<>();
    private List<User> filteredFriends = new ArrayList<>();
    private FriendshipService friendshipService;
    private ConversationService conversationService;
    private WebSocketManager webSocketManager;
    private int userId;
    private TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.empty_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        webSocketManager = WebSocketManager.getInstance();
        friendshipService = RetrofitClient.getApiFriendshipService();
        conversationService = RetrofitClient.getApiConversationService();

        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1);
            if (userId != -1) {
                adapter = new FriendListAdapter(filteredFriends, this::handleFriendClick, this::handleFriendDelete);
                recyclerView.setAdapter(adapter);
                loadFriendList(userId);
            } else {
                Log.e("FriendListFragment", "Invalid User ID");
                Toast.makeText(requireContext(), "Lỗi: Không có ID người dùng", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("FriendListFragment", "No User ID received");
            Toast.makeText(requireContext(), "Lỗi: Không nhận được ID người dùng", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadFriendList(int userId) {
        Log.d("FriendListFragment", "Loading friend list for userId: " + userId);
        friendshipService.getFriendList(userId).enqueue(new retrofit2.Callback<List<User>>() {
            @Override
            public void onResponse(retrofit2.Call<List<User>> call, retrofit2.Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    friends.clear();
                    filteredFriends.clear();
                    friends.addAll(response.body());
                    filteredFriends.addAll(friends);
                    Log.d("FriendListFragment", "Loaded " + friends.size() + " friends: " + friends);
                    updateEmptyView();
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("FriendListFragment", "API error: " + response.code() + " - " + response.message());
                    Toast.makeText(requireContext(), "Lỗi tải danh sách bạn bè: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<User>> call, Throwable t) {
                Log.e("FriendListFragment", "API call failed: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void filterFriends(String query) {
        filteredFriends.clear();
        if (query.isEmpty()) {
            filteredFriends.addAll(friends);
        } else {
            for (User friend : friends) {
                if (friend.getUsername().toLowerCase().contains(query.toLowerCase())) {
                    filteredFriends.add(friend);
                }
            }
        }
        updateEmptyView();
        adapter.notifyDataSetChanged();
    }

    private void updateEmptyView() {
        if (filteredFriends.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void handleFriendClick(User friend) {
        if (getActivity() == null) {
            Log.e("FriendListFragment", "Activity null");
            return;
        }
        Log.d("FriendListFragment", "Tạo cuộc trò chuyện với friendId: " + friend.getId());
        conversationService.createConversation(userId, friend.getId()).enqueue(new retrofit2.Callback<Conversation>() {
            @Override
            public void onResponse(retrofit2.Call<Conversation> call, retrofit2.Response<Conversation> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Conversation conversation = response.body();
                    Intent intent = new Intent(getActivity(), ChattingActivity.class);
                    intent.putExtra("chat_sender_id", userId);
                    intent.putExtra("conversation_id", (int) conversation.getId());
                    intent.putExtra("chat_user_name", friend.getUsername());
                    startActivity(intent);
                    Log.d("FriendListFragment", "Mở ChattingActivity với conversationId: " + conversation.getId());
                } else {
                    Log.e("FriendListFragment", "Lỗi tạo cuộc trò chuyện: " + response.code() + " - " + response.message());
                    Toast.makeText(requireContext(), "Lỗi: Không thể tạo cuộc trò chuyện", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Conversation> call, Throwable t) {
                Log.e("FriendListFragment", "Lỗi gọi API tạo cuộc trò chuyện: " + t.getMessage());
                Toast.makeText(requireContext(), "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFriendDelete(User friend) {
        if (friend == null || getContext() == null) {
            Log.e("FriendListFragment", "Friend hoặc Context null");
            return;
        }
        Log.d("FriendListFragment", "Bắt đầu xóa friendId: " + friend.getId() + ", userId: " + userId);
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Xóa bạn bè")
                .setMessage("Bạn có chắc muốn xóa " + friend.getUsername() + " khỏi danh sách bạn bè?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    Log.d("FriendListFragment", "Gọi API DELETE api/friendship/" + userId + "/" + friend.getId());
                    friendshipService.deleteFriend(userId, friend.getId()).enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                            if (response.isSuccessful()) {
                                friends.remove(friend);
                                filteredFriends.remove(friend);
                                updateEmptyView();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(requireContext(), "Đã xóa " + friend.getUsername() + " khỏi danh sách bạn bè", Toast.LENGTH_SHORT).show();
                                Log.d("FriendListFragment", "Xóa bạn thành công, friendId: " + friend.getId());
                            } else {
                                Log.e("FriendListFragment", "Lỗi API xóa bạn: " + response.code() + ", message: " + response.message());
                                Toast.makeText(requireContext(), "Không thể xóa bạn, có thể bạn chưa là bạn bè", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                            Log.e("FriendListFragment", "Lỗi gọi API xóa bạn: " + t.getMessage());
                            Toast.makeText(requireContext(), "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}