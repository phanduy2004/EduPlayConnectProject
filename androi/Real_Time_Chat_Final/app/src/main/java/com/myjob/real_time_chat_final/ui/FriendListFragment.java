package com.myjob.real_time_chat_final.ui;

import android.app.Service;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.FriendListAdapter;
import com.myjob.real_time_chat_final.api.FriendshipService;
import com.myjob.real_time_chat_final.config.WebSocketManager;
import com.myjob.real_time_chat_final.model.User;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendListAdapter adapter;
    private List<User> friends = new ArrayList<>();
    private FriendshipService friendshipService;
    private WebSocketManager webSocketManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize adapter
        adapter = new FriendListAdapter(friends);
        recyclerView.setAdapter(adapter);

        // Initialize WebSocketManager
        webSocketManager = WebSocketManager.getInstance();
        friendshipService = RetrofitClient.getApiFriendshipService();

        if (getArguments() != null) {
            int userId = getArguments().getInt("USER_ID", -1);
            if (userId != -1) {
                loadFriendList(userId);
            } else {
                Log.e("FriendListFragment", "Invalid User ID");
                // For testing, you can use a default ID
                loadFriendList(userId);
            }
        } else {
            Log.e("FriendListFragment", "No User ID received");
            // For testing, you can use a default ID
        }
    }

    private void loadFriendList(int userId) {
        friendshipService.getFriendList(userId).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    friends.clear();
                    friends.addAll(response.body());
                    Log.d("FriendList", "Received " + friends.size() + " friends");
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("FriendList", "API error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("FriendList", "API call failed: " + t.getMessage());
            }
        });
    }

    // Friend click handler
    public void handleFriendClick(User friend) {
        // Handle friend click (e.g., open chat)
        if (getActivity() != null) {
            // Intent intent = new Intent(getActivity(), ChatActivity.class);
            // intent.putExtra("FRIEND_ID", friend.getId());
            // intent.putExtra("FRIEND_NAME", friend.getUsername());
            // startActivity(intent);
        }
    }

    // This could be used to add new friends to the list
    public void addFriend(User friend) {
        if (friend != null) {
            friends.add(friend);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
}