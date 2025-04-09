package com.myjob.real_time_chat_final.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.FriendListAdapter;
import com.myjob.real_time_chat_final.adapter.FriendRequestAdapter;
import com.myjob.real_time_chat_final.config.WebSocketManager;
import com.myjob.real_time_chat_final.model.Friendship;
import com.myjob.real_time_chat_final.model.User;

import java.util.ArrayList;
import java.util.List;
public class FriendListActivity extends AppCompatActivity {

    private WebSocketManager webSocketManager;
    private User user;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        userID = getIntent().getIntExtra("USER_ID", -1);

        // Khởi tạo WebSocketManager và kết nối
        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.connect();
        user = new User();
        user.setId(userID);

        webSocketManager.subscribeToRequest(response -> runOnUiThread(() -> {
            Log.d("FriendRequest", "Nhận phản hồi: " + response);

            // Chuyển đổi response thành đối tượng Friendship
            Friendship newFriendRequest = new Gson().fromJson(response, Friendship.class);

            // Kiểm tra xem ID người gửi có phải là ID của người dùng hiện tại không
            if (newFriendRequest.getSenderId() != null &&
                    newFriendRequest.getSenderId().getId() != userID) {

                Toast.makeText(this, "Có yêu cầu kết bạn mới!", Toast.LENGTH_SHORT).show();

                // Thêm yêu cầu kết bạn vào FriendRequestFragment
                FriendRequestFragment fragment = (FriendRequestFragment) getSupportFragmentManager().findFragmentByTag("f1");
                if (fragment != null) {
                    fragment.addFriendRequest(newFriendRequest);
                }
            }
        }));
        // Khởi tạo TabLayout và ViewPager2
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Cài đặt adapter cho ViewPager2
        FragmentStateAdapter adapter = new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Bundle args = new Bundle();
                args.putInt("USER_ID", userID); // Truyền userID vào Fragment

                switch (position) {
                    case 0:
                        FriendListFragment friendListFragment = new FriendListFragment();
                        friendListFragment.setArguments(args);
                        return friendListFragment;
                    case 1:
                    default:
                        FriendRequestFragment friendRequestFragment = new FriendRequestFragment();
                        friendRequestFragment.setArguments(args);
                        return friendRequestFragment;
                }
            }


            @Override
            public int getItemCount() {
                return 2; // Tổng số tab
            }
        };

        viewPager.setAdapter(adapter);

        // Kết nối TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Bạn bè");
                    break;
                case 1:
                    tab.setText("Yêu cầu kết bạn");
                    break;
            }
        }).attach();

        // Thiết lập sự kiện click cho nút thêm bạn
        findViewById(R.id.btn_add_friend).setOnClickListener(v -> showAddFriendDialog());
    }

    private void showAddFriendDialog() {
        final EditText input = new EditText(this);
        input.setHint("Nhập tên bạn");

        new AlertDialog.Builder(this)
                .setTitle("Thêm bạn mới")
                .setMessage("Nhập tên bạn để kết bạn")
                .setView(input)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        sendFriendRequest(name);
                    } else {
                        Toast.makeText(FriendListActivity.this, "Vui lòng nhập tên bạn", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void sendFriendRequest(String friendName) {
        Friendship friendship = new Friendship(user, friendName, "Pending");
        webSocketManager.sendRequest(new Gson().toJson(friendship), "/app/sendFriendRequest");
        Toast.makeText(this, "Đã gửi yêu cầu kết bạn cho " + friendName, Toast.LENGTH_SHORT).show();
    }
}
