package com.myjob.real_time_chat_final.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.myjob.real_time_chat_final.adapter.MeaningAdapter;
import com.myjob.real_time_chat_final.databinding.ActivityDictionaryBinding;
import com.myjob.real_time_chat_final.model.WordResult;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import retrofit2.Response;

public class DictionaryActivity extends AppCompatActivity {

    private ActivityDictionaryBinding binding;
    private MeaningAdapter adapter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDictionaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new MeaningAdapter(Collections.emptyList());
        binding.meaningRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.meaningRecyclerView.setAdapter(adapter);

        binding.searchBtn.setOnClickListener(view -> {
            String word = binding.searchInput.getText().toString().trim();
            if (!word.isEmpty()) {
                getMeaning(word);
            }
        });
    }

    private void getMeaning(String word) {
        setInProgress(true);

        executor.execute(() -> {
            try {
                Response<List<WordResult>> response = RetrofitClient.getApiDictionaryService().getMeaning(word).execute();

                if (response.body() == null || response.body().isEmpty()) {
                    throw new Exception("Empty response");
                }

                WordResult result = response.body().get(0);

                runOnUiThread(() -> {
                    setInProgress(false);
                    setUI(result);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    setInProgress(false);
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    private void setUI(WordResult result) {
        binding.wordTextview.setText(result.getWord());
        binding.phoneticTextview.setText(result.getPhonetic());
        adapter.updateNewData(result.getMeanings());
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            binding.searchBtn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.searchBtn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}