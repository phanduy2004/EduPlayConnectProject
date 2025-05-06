package com.myjob.real_time_chat_final.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.CategoryAdapter;
import com.myjob.real_time_chat_final.adapter.TopicAdapter;
import com.myjob.real_time_chat_final.api.CategoryService;
import com.myjob.real_time_chat_final.api.TopicService;
import com.myjob.real_time_chat_final.model.Category;
import com.myjob.real_time_chat_final.model.Topic;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicStudyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_topic_study);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fetchTopic();
    }
    private void fetchTopic() {
        TopicService topicService = RetrofitClient.getApiTopicService();
        Call<List<Topic>> call = topicService.getTopics();
        call.enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Topic> topicList = response.body();
                    Log.d("topic", topicList.toString());
                   RecyclerView recyclerView = findViewById(R.id.recyclerViewTopic);

//                    // Tạo Adapter và thiết lập onClick cho mỗi topic
                    TopicAdapter adapter = new TopicAdapter(topicList, topic -> {
                        Intent intent = new Intent(TopicStudyActivity.this, VideoActivity.class);
                        intent.putExtra("topic", topic);  // Truyền ID topic
                        startActivity(intent);
                  });
//
//                    // Thiết lập GridLayoutManager với 2 cột
                   recyclerView.setLayoutManager(new GridLayoutManager(TopicStudyActivity.this, 2));
                   recyclerView.setAdapter(adapter);
                } else {
                    Log.e(TAG, "Failed to load topics: " + response.message());
                    Toast.makeText(TopicStudyActivity.this, "Failed to load topics", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Topic>> call, Throwable t) {
                Log.e(TAG, "Error fetching topics", t);
                Toast.makeText(TopicStudyActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}