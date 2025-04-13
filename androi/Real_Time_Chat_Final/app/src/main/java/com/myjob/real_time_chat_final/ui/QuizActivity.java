package com.myjob.real_time_chat_final.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.RankingAdapter;
import com.myjob.real_time_chat_final.config.QuizWebSocketService;
import com.myjob.real_time_chat_final.model.GameRoomPlayer;
import com.myjob.real_time_chat_final.model.Question;
import com.myjob.real_time_chat_final.model.User;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity implements QuizWebSocketService.QuizWebSocketListener {
    private static final String TAG = "QuizActivity";
    private TextView tvQuestionCount, tvQuestionTitle, tvTimer;
    private ImageView imgQuestion;
    private ProgressBar progressBar;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;
    private ImageButton btnBack;
    private RecyclerView rvRanking;
    private RankingAdapter rankingAdapter;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int totalQuestions;
    private String roomId, userId, categoryId, categoryName;
    private QuizWebSocketService webSocketService;
    private CountDownTimer countDownTimer;
    private boolean isAnswerSubmitted = false;
    private static final long QUESTION_TIME_MS = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Ánh xạ view
        btnBack = findViewById(R.id.btn_back);
        tvQuestionCount = findViewById(R.id.tv_question_count);
        tvQuestionTitle = findViewById(R.id.tv_question_title);
        imgQuestion = findViewById(R.id.img_question);
        progressBar = findViewById(R.id.progress_bar);
        btnOption1 = findViewById(R.id.btn_option1);
        btnOption2 = findViewById(R.id.btn_option2);
        btnOption3 = findViewById(R.id.btn_option3);
        btnOption4 = findViewById(R.id.btn_option4);
        tvTimer = findViewById(R.id.tv_timer);
        rvRanking = findViewById(R.id.rv_ranking);

        // Khởi tạo RecyclerView
        rankingAdapter = new RankingAdapter();
        rvRanking.setLayoutManager(new LinearLayoutManager(this));
        rvRanking.setAdapter(rankingAdapter);

        // Khởi tạo bảng xếp hạng mặc định
        List<GameRoomPlayer> initialRankings = new ArrayList<>();
        GameRoomPlayer player = new GameRoomPlayer();
        User user = new User();
        user.setId((int) (userId != null ? Long.parseLong(userId) : 0L));
        user.setUsername("You");
        user.setStatus(true);
        player.setUser(user);
        player.setScore(0);
        initialRankings.add(player);
        rankingAdapter.updateRankings(initialRankings);
        Log.d(TAG, "Initial rankings set: " + initialRankings);

        // Nhận dữ liệu từ Intent
        roomId = getIntent().getStringExtra("ROOM_ID");
        userId = getIntent().getStringExtra("USER_ID");
        categoryId = getIntent().getStringExtra("CATEGORY_ID");
        categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        String questionsJson = getIntent().getStringExtra("QUESTIONS");

        Log.d(TAG, "Received Intent - ROOM_ID: " + roomId + ", USER_ID: " + userId +
                ", CATEGORY_ID: " + categoryId + ", QUESTIONS: " + questionsJson);

        // Parse danh sách câu hỏi
        if (questionsJson != null && !questionsJson.isEmpty()) {
            questionList = new Gson().fromJson(questionsJson, new TypeToken<List<Question>>(){}.getType());
            totalQuestions = questionList.size();
            if (totalQuestions > 0) {
                loadQuestion();
            } else {
                Toast.makeText(this, "Không có câu hỏi nào!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Không nhận được danh sách câu hỏi!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Khởi tạo WebSocket
        webSocketService = QuizWebSocketService.getInstance();
        webSocketService.initialize(userId);
        webSocketService.addListener(this);
        if (roomId != null) {
            webSocketService.subscribeToRanking(roomId);
            Log.d(TAG, "Subscribed to ranking topic: /topic/game.ranking." + roomId);
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadQuestion() {
        if (currentQuestionIndex < totalQuestions) {
            Question question = questionList.get(currentQuestionIndex);
            isAnswerSubmitted = false;

            // Cập nhật UI
            tvQuestionCount.setText("Q" + (currentQuestionIndex + 1) + "/" + totalQuestions);
            tvQuestionTitle.setText(question.getQuestion());
            progressBar.setProgress((currentQuestionIndex + 1) * 100 / totalQuestions);

            // Hiển thị hình ảnh
            if (question.getImage() != null && !question.getImage().isEmpty()) {
                Glide.with(this).load(question.getImage()).into(imgQuestion);
                imgQuestion.setVisibility(View.VISIBLE);
            } else {
                imgQuestion.setVisibility(View.GONE);
            }

            // Cập nhật đáp án
            btnOption1.setText(question.getOptionA());
            btnOption2.setText(question.getOptionB());
            btnOption3.setText(question.getOptionC());
            btnOption4.setText(question.getOptionD());

            // Đặt lại trạng thái nút
            btnOption1.setEnabled(true);
            btnOption2.setEnabled(true);
            btnOption3.setEnabled(true);
            btnOption4.setEnabled(true);

            // Xử lý sự kiện chọn đáp án
            btnOption1.setOnClickListener(v -> submitAnswer(question, question.getOptionA()));
            btnOption2.setOnClickListener(v -> submitAnswer(question, question.getOptionB()));
            btnOption3.setOnClickListener(v -> submitAnswer(question, question.getOptionC()));
            btnOption4.setOnClickListener(v -> submitAnswer(question, question.getOptionD()));

            // Bắt đầu timer
            startTimer();
        } else {
            finishQuiz();
        }
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(QUESTION_TIME_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                if (!isAnswerSubmitted) {
                    Question question = questionList.get(currentQuestionIndex);
                    webSocketService.submitAnswer(roomId, String.valueOf(question.getId()), "");
                    Toast.makeText(QuizActivity.this, "Hết thời gian!", Toast.LENGTH_SHORT).show();
                }
                currentQuestionIndex++;
                loadQuestion();
            }
        }.start();
    }

    private void submitAnswer(Question question, String selectedAnswer) {
        if (isAnswerSubmitted) {
            return;
        }

        isAnswerSubmitted = true;
        if (roomId != null && userId != null) {
            webSocketService.submitAnswer(roomId, String.valueOf(question.getId()), selectedAnswer);
            Log.d(TAG, "Submitted answer for question " + question.getId() + ": " + selectedAnswer);
            // Hiển thị kết quả tạm thời
            if (selectedAnswer.equals(question.getCorrectAnswer())) {
                Toast.makeText(this, "Chính xác!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sai rồi!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Room ID or User ID is null, cannot submit answer");
            Toast.makeText(this, "Error: Cannot submit answer", Toast.LENGTH_SHORT).show();
        }

        // Vô hiệu hóa nút
        btnOption1.setEnabled(false);
        btnOption2.setEnabled(false);
        btnOption3.setEnabled(false);
        btnOption4.setEnabled(false);
    }

    private void finishQuiz() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        Toast.makeText(this, "Hoàn thành! Điểm số sẽ được cập nhật từ server", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onRankingUpdate(String rankingData) {
        runOnUiThread(() -> {
            Log.d(TAG, "Received ranking data: " + rankingData);
            try {
                List<GameRoomPlayer> rankings = new Gson().fromJson(rankingData, new TypeToken<List<GameRoomPlayer>>(){}.getType());
                rankings.sort((p1, p2) -> Long.compare(p2.getScore(), p1.getScore()));
                for (GameRoomPlayer player : rankings) {
                    if (player.getUser() != null && player.getUser().getId() == Long.parseLong(userId)) {
                        Toast.makeText(this, "Your score: " + player.getScore(), Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                rankingAdapter.updateRankings(rankings);
                Log.d(TAG, "Ranking adapter updated with " + rankings.size() + " players");
                rvRanking.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing ranking data: " + e.getMessage());
                Toast.makeText(this, "Error updating ranking", Toast.LENGTH_SHORT).show();
            }
        });
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
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (roomId != null) {
            webSocketService.unsubscribeFromRanking(roomId);
        }
        webSocketService.removeListener(this);
        Log.d(TAG, "QuizActivity destroyed, WebSocket listener removed");
    }
}