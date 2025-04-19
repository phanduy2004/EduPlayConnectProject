package com.myjob.real_time_chat_final.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.CategoryAdapter;
import com.myjob.real_time_chat_final.api.CategoryService;
import com.myjob.real_time_chat_final.config.GameWebSocketService;
import com.myjob.real_time_chat_final.model.Category;
import com.myjob.real_time_chat_final.model.GameRoom;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizListActivity extends AppCompatActivity implements GameWebSocketService.GameWebSocketListener {
    private static final String TAG = "QuizListActivity";
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private int userId = LoginActivity.userid;
    private GameWebSocketService gameWebSocketService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo WebSocket service (vẫn cần để xử lý các sự kiện khác nếu có)
        gameWebSocketService = GameWebSocketService.getInstance();
        gameWebSocketService.initialize(String.valueOf(userId));
        gameWebSocketService.addListener(this);

        fetchCategories();
    }

    private void fetchCategories() {
        CategoryService categoryService = RetrofitClient.getApiCategoryService();
        Call<List<Category>> call = categoryService.getAllCategory();

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    categoryAdapter = new CategoryAdapter(categories, new CategoryAdapter.OnGameModeClickListener() {
                        @Override
                        public void onSinglePlayerSelected(Category category) {
                            Intent intent = new Intent(QuizListActivity.this, QuizActivity.class);
                            intent.putExtra("CATEGORY_ID", String.valueOf(category.getId()));
                            intent.putExtra("CATEGORY_NAME", category.getName());
                            startActivity(intent);
                        }

                        @Override
                        public void onMultiPlayerSelected(Category category) {
                            // Chỉ chuyển sang MultiplayerRoomActivity, không tạo phòng ở đây
                            Intent intent = new Intent(QuizListActivity.this, MultiplayerRoomActivity.class);
                            intent.putExtra("CATEGORY_ID", String.valueOf(category.getId()));
                            intent.putExtra("CATEGORY_NAME", category.getName());
                            intent.putExtra("IS_HOST", true); // Đánh dấu đây là host
                            intent.putExtra("USER_ID", String.valueOf(userId));
                            startActivity(intent);}
                    });

                    recyclerView.setAdapter(categoryAdapter);
                } else {
                    Log.e(TAG, "Failed to load categories: " + response.message());
                    Toast.makeText(QuizListActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e(TAG, "Error fetching categories", t);
                Toast.makeText(QuizListActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRoomUpdate(GameRoom room) {
        // Không cần xử lý ở đây nữa vì tạo phòng đã chuyển sang MultiplayerRoomActivity
    }

    @Override
    public void onGameStart(String gameData) {
        // Không cần xử lý ở đây
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> {
            Log.e(TAG, "WebSocket error: " + error);
            Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameWebSocketService.removeListener(this);
        Log.d(TAG, "Activity destroyed, WebSocket listener removed");
    }
}