package com.myjob.real_time_chat_final.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.VideoAdapter;
import com.myjob.real_time_chat_final.model.Topic;
import com.myjob.real_time_chat_final.model.Video;

import java.util.List;

public class VideoActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Topic topic = (Topic) getIntent().getSerializableExtra("topic");
        List<Video> videoList = topic.getVideoList();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewVideo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        VideoAdapter adapter = new VideoAdapter(videoList, video -> {
            Intent intent = new Intent(VideoActivity.this, FullScreenVideoActivity.class);
            intent.putExtra("url", video.getUrl());  // Truyền ID topic
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        TextView textTopicName = findViewById(R.id.textTopicName);

        textTopicName.setText(topic.getName());
    }
}