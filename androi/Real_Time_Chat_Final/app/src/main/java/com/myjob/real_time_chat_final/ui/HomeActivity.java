package com.myjob.real_time_chat_final.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.myjob.real_time_chat_final.R;

public class HomeActivity extends AppCompatActivity {
    ImageButton alphabet, math, qizz, dictionary;
    private final int userid = LoginActivity.userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        if (userid == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy userID", Toast.LENGTH_SHORT).show();
            return;
        }
        // Tìm BottomNavigationView

        alphabet = findViewById(R.id.alphabetBtn);
        math = findViewById(R.id.mathBtn);
        qizz = findViewById(R.id.quizzBtn);
        dictionary = findViewById(R.id.dictionaryBtn);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navBar);
        // Xử lý sự kiện click vào item trên BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_chatmessage) {
                    // Khi nhấn vào icon chat, mở ChatActivity
                    Intent intent = new Intent(HomeActivity.this, MessageListActivity.class);
                    intent.putExtra("USER_ID", userid); // Gửi userId sang MessListActivity
                    startActivity(intent);
                    return true;
                }
                else if(item.getItemId() == R.id.nav_addFriend){
                    Intent intent = new Intent(HomeActivity.this, FriendListActivity.class);
                    intent.putExtra("USER_ID", userid); // Gửi userId sang MessListActivity
                    startActivity(intent);
                    return true;
                }
                else if(item.getItemId()== R.id.nav_findRoom){
                    Intent intent = new Intent(HomeActivity.this, NewsFeedActivity.class);
                    intent.putExtra("USER_ID", userid); // Gửi userId sang MessListActivity
                    startActivity(intent);
                    return true;
                }
                else if(item.getItemId()== R.id.nav_userhome){
                    Intent intent = new Intent(HomeActivity.this, UserActivity.class);
                    intent.putExtra("USER_ID", userid); // Gửi userId sang MessListActivity
                    startActivity(intent);
                    return true;
                }
                else
                    return false;
            }
        });
        alphabet.setOnClickListener(v -> {
            Intent intent  = new Intent(HomeActivity.this, AlphabetActivity.class);
            startActivity(intent);
        });
        qizz.setOnClickListener(v->{
            Intent intent = new Intent(HomeActivity.this, QuizListActivity.class);
            intent.putExtra("USER_ID", userid);
            startActivity(intent);
        });
        math.setOnClickListener(v->{
            Intent intent = new Intent(HomeActivity.this, MathActivity.class);
            startActivity(intent);
        });
        dictionary.setOnClickListener(v->{
            Intent intent = new Intent(HomeActivity.this, DictionaryActivity.class);
            startActivity(intent);
        });
    }
}
