package com.myjob.real_time_chat_final.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.LeaderboardAdapter;
import com.myjob.real_time_chat_final.api.ScoreService;
import com.myjob.real_time_chat_final.modelDTO.ScoreDTO;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MathActivity extends AppCompatActivity {

    private TextView questionText, scoreText, timerText, resultText;
    private Button answerButton1, answerButton2, answerButton3, answerButton4;
    private GridLayout answerGrid;
    private int correctAnswer, score = 0;
    private CountDownTimer gameTimer;
    private RecyclerView leaderboardRecyclerView;
    private LeaderboardAdapter leaderboardAdapter;
    private ArrayList<ScoreDTO> highScores = new ArrayList<>();

    private static final String TAG = "MathActivity";

    private ScoreService scoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math);

        // Khởi tạo các view
        questionText = findViewById(R.id.questionText);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);
        resultText = findViewById(R.id.resultText);
        answerButton1 = findViewById(R.id.answerButton1);
        answerButton2 = findViewById(R.id.answerButton2);
        answerButton3 = findViewById(R.id.answerButton3);
        answerButton4 = findViewById(R.id.answerButton4);
        answerGrid = findViewById(R.id.answerGrid);
        leaderboardRecyclerView = findViewById(R.id.leaderboardRecyclerView);

        // Sử dụng RetrofitClient để tạo ScoreService
        scoreService = RetrofitClient.getApiScoreService();

        // Cấu hình toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Xử lý nút back
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Cấu hình RecyclerView
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        leaderboardAdapter = new LeaderboardAdapter(highScores);
        leaderboardRecyclerView.setAdapter(leaderboardAdapter);

        // Hiển thị bảng xếp hạng ngay từ đầu
        showLeaderboard();

        // Tải dữ liệu bảng xếp hạng ban đầu
        loadTop5Scores();

        // Khởi động game timer 50s
        startGameTimer();

        // Tạo phép tính ngẫu nhiên
        generateQuestion();

        // Gán sự kiện nhấn cho các nút đáp án
        setAnswerButtonClickListener(answerButton1);
        setAnswerButtonClickListener(answerButton2);
        setAnswerButtonClickListener(answerButton3);
        setAnswerButtonClickListener(answerButton4);
    }

    private void generateQuestion() {
        Random random = new Random();
        int num1 = random.nextInt(50) + 1;
        int num2 = random.nextInt(50) + 1;
        int operation = random.nextInt(2);
        String operationSymbol = "";
        int result = 0;

        switch (operation) {
            case 0:
                result = num1 + num2;
                operationSymbol = " + ";
                break;
            case 1:
                result = num1 - num2;
                operationSymbol = " - ";
                break;
        }

        correctAnswer = result;
        questionText.setText(num1 + operationSymbol + num2);

        int wrongAnswer1 = correctAnswer + random.nextInt(5) + 1;
        int wrongAnswer2 = correctAnswer - random.nextInt(3) - 1;
        int wrongAnswer3 = correctAnswer + random.nextInt(7) + 1;

        int[] answers = {correctAnswer, wrongAnswer1, wrongAnswer2, wrongAnswer3};
        for (int i = 0; i < answers.length; i++) {
            int randomIndex = random.nextInt(answers.length);
            int temp = answers[i];
            answers[i] = answers[randomIndex];
            answers[randomIndex] = temp;
        }

        answerButton1.setText(String.valueOf(answers[0]));
        answerButton2.setText(String.valueOf(answers[1]));
        answerButton3.setText(String.valueOf(answers[2]));
        answerButton4.setText(String.valueOf(answers[3]));

        resetButtonStates();
    }

    private void setAnswerButtonClickListener(Button answerButton) {
        answerButton.setOnClickListener(v -> {
            int selectedAnswer = Integer.parseInt(answerButton.getText().toString());
            if (selectedAnswer == correctAnswer) {
                score += 10;
                resultText.setText("Đúng rồi!");
                resultText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                answerButton.setSelected(true);
            } else {
                score = Math.max(0, score - 5);
                resultText.setText("Sai rồi! Đáp án đúng là: " + correctAnswer);
                resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                answerButton.setBackgroundResource(R.drawable.button_background_incorrect);
                answerButton.invalidate();
                markCorrectAnswer();
            }
            updateScoreText();
            generateQuestion();
        });
    }

    private void markCorrectAnswer() {
        if (Integer.parseInt(answerButton1.getText().toString()) == correctAnswer) answerButton1.setSelected(true);
        else if (Integer.parseInt(answerButton2.getText().toString()) == correctAnswer) answerButton2.setSelected(true);
        else if (Integer.parseInt(answerButton3.getText().toString()) == correctAnswer) answerButton3.setSelected(true);
        else if (Integer.parseInt(answerButton4.getText().toString()) == correctAnswer) answerButton4.setSelected(true);
    }

    private void resetButtonStates() {
        answerButton1.setSelected(false);
        answerButton1.setBackgroundResource(R.drawable.button_background);
        answerButton2.setSelected(false);
        answerButton2.setBackgroundResource(R.drawable.button_background);
        answerButton3.setSelected(false);
        answerButton3.setBackgroundResource(R.drawable.button_background);
        answerButton4.setSelected(false);
        answerButton4.setBackgroundResource(R.drawable.button_background);
        answerButton1.invalidate();
        answerButton2.invalidate();
        answerButton3.invalidate();
        answerButton4.invalidate();
    }

    private void startGameTimer() {
        gameTimer = new CountDownTimer(50000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText((millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                timerText.setText("0s");
                resultText.setText("Hết giờ!");
                disableButtons();

                // Lưu điểm số trước khi hiển thị dialog
                saveScoreToBackend();

                // Hiển thị custom dialog
                showScoreDialog();
            }
        }.start();
    }

    private void showScoreDialog() {
        // Tạo và cấu hình dialog
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Ẩn tiêu đề mặc định
        dialog.setContentView(R.layout.dialog_score);

        // Ánh xạ các view trong dialog
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        Button okButton = dialog.findViewById(R.id.dialog_ok_button);

        // Thiết lập nội dung
        dialogTitle.setText("Hoàn thành!");
        dialogMessage.setText("Số điểm của bạn: " + score);

        // Xử lý nút OK
        okButton.setOnClickListener(v -> {
            dialog.dismiss(); // Đóng dialog
            // Cập nhật bảng xếp hạng sau khi người dùng nhấn OK
            loadTop5Scores();
        });

        // Hiển thị dialog
        dialog.show();

        // Đặt kích thước và vị trí cho dialog
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(androidx.appcompat.widget.LinearLayoutCompat.LayoutParams.MATCH_PARENT, androidx.appcompat.widget.LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void updateScoreText() {
        scoreText.setText("Score: " + score);
    }

    private void saveScoreToBackend() {
        long userId = LoginActivity.userid;
        if (userId == 0) {
            resultText.setText("Lỗi: User ID không hợp lệ");
            Log.e(TAG, "User ID is invalid");
            return;
        }
        scoreService.saveScore(userId, score).enqueue(new Callback<ScoreDTO>() {
            @Override
            public void onResponse(Call<ScoreDTO> call, Response<ScoreDTO> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Score saved successfully");
                } else {
                    Log.e(TAG, "Failed to save score: " + response.code());
                    resultText.setText("Lỗi lưu điểm: HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ScoreDTO> call, Throwable t) {
                resultText.setText("Lỗi kết nối khi lưu điểm: " + t.getMessage());
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    private void loadTop5Scores() {
        scoreService.getTop5Scores().enqueue(new Callback<List<ScoreDTO>>() {
            @Override
            public void onResponse(Call<List<ScoreDTO>> call, Response<List<ScoreDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    highScores.clear();
                    highScores.addAll(response.body());
                    Log.d(TAG, "Loaded " + highScores.size() + " scores");
                } else {
                    Log.e(TAG, "Failed to load scores: " + response.code());
                    resultText.setText("Lỗi tải bảng xếp hạng: HTTP " + response.code());
                }
                ensureFiveItems();
                leaderboardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ScoreDTO>> call, Throwable t) {
                resultText.setText("Lỗi tải bảng xếp hạng: " + t.getMessage());
                Log.e(TAG, "API call failed: " + t.getMessage());
                ensureFiveItems();
                leaderboardAdapter.notifyDataSetChanged();
            }
        });
    }

    private void ensureFiveItems() {
        while (highScores.size() < 5) {
            ScoreDTO dummy = new ScoreDTO();
            dummy.username = "Unknown";
            dummy.score = 0;
            highScores.add(dummy);
        }
    }

    private void showLeaderboard() {
        resultText.setVisibility(View.VISIBLE);
        leaderboardRecyclerView.setVisibility(View.VISIBLE);
    }

    private void disableButtons() {
        answerButton1.setEnabled(false);
        answerButton2.setEnabled(false);
        answerButton3.setEnabled(false);
        answerButton4.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameTimer != null) {
            gameTimer.cancel();
        }
    }
}