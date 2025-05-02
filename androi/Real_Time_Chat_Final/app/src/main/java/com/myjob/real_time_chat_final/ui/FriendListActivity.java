package com.myjob.real_time_chat_final.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.config.WebSocketManager;
import com.myjob.real_time_chat_final.model.Friendship;
import com.myjob.real_time_chat_final.model.User;

public class FriendListActivity extends AppCompatActivity implements FriendRequestListener {

    private WebSocketManager webSocketManager;
    private User user;
    private final int userID = LoginActivity.userid;
    private SearchView searchView;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        // Khởi tạo WebSocketManager và kết nối
        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.connect();
        user = new User();
        user.setId(userID);

        webSocketManager.subscribeToRequest(response -> runOnUiThread(() -> {
            Log.d("FriendRequest", "Nhận phản hồi: " + response);

            Friendship newFriendRequest = new Gson().fromJson(response, Friendship.class);

            if (newFriendRequest.getSenderId() != null &&
                    newFriendRequest.getSenderId().getId() != userID) {

                Toast.makeText(this, "Có yêu cầu kết bạn mới!", Toast.LENGTH_SHORT).show();

                FriendRequestFragment fragment = (FriendRequestFragment) getSupportFragmentManager().findFragmentByTag("f1");
                if (fragment != null) {
                    fragment.addFriendRequest(newFriendRequest);
                }
            }
        }));

        // Thiết lập Toolbar
        setupToolbar();

        // Thiết lập SearchView
        setupSearchView();

        // Thiết lập TabLayout và ViewPager2
        setupViewPager();

        // Thiết lập BottomNavigationView
        setupBottomNavigation();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bạn bè");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationIcon(R.drawable.ic_back_ios);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupSearchView() {
        searchView = findViewById(R.id.search_friends);
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        if (searchPlate != null) {
            searchPlate.setBackgroundResource(android.R.color.transparent);
        }
        int searchTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchText = searchView.findViewById(searchTextId);
        if (searchText != null) {
            searchText.setTextSize(16);
            searchText.setTextColor(Color.BLACK);
            searchText.setHintTextColor(Color.parseColor("#A0A0A0"));
            searchText.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL));
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + viewPager.getCurrentItem());
                if (fragment instanceof FriendListFragment) {
                    ((FriendListFragment) fragment).filterFriends(newText);
                } else if (fragment instanceof FriendRequestFragment) {
                    ((FriendRequestFragment) fragment).filterRequests(newText);
                }
                return true;
            }
        });
    }

    private void setupViewPager() {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        FragmentStateAdapter adapter = new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Bundle args = new Bundle();
                args.putInt("USER_ID", userID);

                switch (position) {
                    case 0:
                        FriendListFragment friendListFragment = new FriendListFragment();
                        friendListFragment.setArguments(args);
                        return friendListFragment;
                    case 1:
                    default:
                        FriendRequestFragment friendRequestFragment = new FriendRequestFragment();
                        friendRequestFragment.setArguments(args);
                        // Gán listener cho FriendRequestFragment
                        friendRequestFragment.setFriendRequestListener(FriendListActivity.this);
                        return friendRequestFragment;
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        };

        viewPager.setAdapter(adapter);

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
    }

    private void setupBottomNavigation() {
        BottomNavigationView navBar = findViewById(R.id.navBar);
        navBar.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_addFriend) {
                return true;
            } else if (itemId == R.id.nav_home) {
                Intent intent = new Intent(FriendListActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_userhome) {
                Intent intent = new Intent(FriendListActivity.this, UserActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_newsfeed) {
                Intent intent = new Intent(FriendListActivity.this, NewsFeedActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_chatmessage) {
                Intent intent = new Intent(FriendListActivity.this, MessageListActivity.class);
                intent.putExtra("USER_ID", userID);
                startActivity(intent);
                return true;
            }
            return false;
        });

        navBar.setSelectedItemId(R.id.nav_addFriend);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friend_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_add_friend) {
            showAddFriendDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddFriendDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_friend, null);
        EditText input = dialogView.findViewById(R.id.edt_friend_name);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btn_submit).setOnClickListener(v -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                sendFriendRequest(name);
                dialog.dismiss();
            } else {
                Toast.makeText(FriendListActivity.this, "Vui lòng nhập tên bạn", Toast.LENGTH_SHORT).show();
            }
        });

        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void sendFriendRequest(String friendName) {
        Friendship friendship = new Friendship(user, friendName, "Pending");
        webSocketManager.sendRequest(new Gson().toJson(friendship), "/app/sendFriendRequest");
        Toast.makeText(this, "Đã gửi yêu cầu kết bạn cho " + friendName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFriendRequestAccepted(int userId) {
        // Tìm FriendListFragment và gọi loadFriendList
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("f0");
        if (fragment instanceof FriendListFragment) {
            ((FriendListFragment) fragment).loadFriendList(userId);
        } else {
            Log.e("FriendListActivity", "FriendListFragment not found or not attached");
        }
    }
}