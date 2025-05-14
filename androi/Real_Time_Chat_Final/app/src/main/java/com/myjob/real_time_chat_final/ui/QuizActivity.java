package com.myjob.real_time_chat_final.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
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
    private String roomId, userId, categoryId, categoryName, hostId;
    private QuizWebSocketService webSocketService;
    private CountDownTimer countDownTimer;
    private boolean isAnswerSubmitted = false;
    private static final long QUESTION_TIME_MS = 10000;
    private List<GameRoomPlayer> finalRankings = new ArrayList<>();
    private boolean isGameEnded = false;
    private int localScore = 0;
    private boolean isBackPressed = false;

    private boolean isRankingDialogShown = false;

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

        // Nhận dữ liệu từ Intent
        roomId = getIntent().getStringExtra("ROOM_ID");
        userId = getIntent().getStringExtra("USER_ID");
        categoryId = getIntent().getStringExtra("CATEGORY_ID");
        categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        hostId = getIntent().getStringExtra("HOST_ID");
        String questionsJson = getIntent().getStringExtra("QUESTIONS");

        Log.d(TAG, "Nhận Intent - ROOM_ID: " + roomId + ", USER_ID: " + userId +
                ", CATEGORY_ID: " + categoryId + ", QUESTIONS: " + questionsJson);

        // Khởi tạo bảng xếp hạng mặc định
        GameRoomPlayer player = new GameRoomPlayer();
        User user = new User();
        user.setId(userId != null ? Integer.parseInt(userId) : 0);
        user.setUsername("Bạn");
        user.setStatus(true);
        player.setUser(user);
        player.setScore(0L);
        finalRankings.add(player);
        rankingAdapter.updateRankings(finalRankings);
        Log.d(TAG, "Khởi tạo bảng xếp hạng: " + new Gson().toJson(finalRankings));

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
            webSocketService.subscribeToGameEnd(roomId);
            Log.d(TAG, "Đã đăng ký topic xếp hạng: /topic/game.ranking." + roomId);
            Log.d(TAG, "Đã đăng ký topic kết thúc: /topic/game.end." + roomId);
        }

        btnBack.setOnClickListener(v -> {
            isBackPressed = true;
            if (!isGameEnded) {
                webSocketService.endGame(roomId, userId);
                Log.d(TAG, "Nhấn back, gửi yêu cầu kết thúc trò chơi");
            }
            Toast.makeText(this, "Đang thoát trò chơi...", Toast.LENGTH_SHORT).show();
            showRankingDialog();
        });

    }

    private void navigateToRoomActivity() {
        if (isFinishing() || isDestroyed()) {
            Log.d(TAG, "Bỏ qua navigateToRoomActivity vì Activity đã kết thúc");
            return;
        }
        Intent intent = new Intent(this, QuizListActivity.class);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("CATEGORY_ID", categoryId);
        intent.putExtra("CATEGORY_NAME", categoryName);
        startActivity(intent);
        Log.d(TAG, "Chuyển đến QuizListActivity");
    }

    private void loadQuestion() {
        if (currentQuestionIndex < totalQuestions && !isGameEnded) {
            Question question = questionList.get(currentQuestionIndex);
            isAnswerSubmitted = false;

            // Cập nhật UI
            tvQuestionCount.setText("Câu " + (currentQuestionIndex + 1) + "/" + totalQuestions);
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

            // Kích hoạt nút
            btnOption1.setEnabled(true);
            btnOption1.setVisibility(View.VISIBLE);
            btnOption2.setEnabled(true);
            btnOption2.setVisibility(View.VISIBLE);
            btnOption3.setEnabled(true);
            btnOption3.setVisibility(View.VISIBLE);
            btnOption4.setEnabled(true);
            btnOption4.setVisibility(View.VISIBLE);
            Log.d(TAG, "Kích hoạt nút đáp án cho câu " + (currentQuestionIndex + 1));

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
                if (!isFinishing()) {
                    tvTimer.setText(String.valueOf(millisUntilFinished / 1000));
                }
            }

            @Override
            public void onFinish() {
                if (!isAnswerSubmitted && !isFinishing() && !isGameEnded) {
                    Question question = questionList.get(currentQuestionIndex);
                    webSocketService.submitAnswer(roomId, String.valueOf(question.getId()), "");
                    Toast.makeText(QuizActivity.this, "Hết thời gian!", Toast.LENGTH_SHORT).show();
                    currentQuestionIndex++;
                    loadQuestion();
                }
            }
        }.start();
    }

    private void submitAnswer(Question question, String selectedAnswer) {
        if (isAnswerSubmitted || isGameEnded) {
            return;
        }

        isAnswerSubmitted = true;
        if (roomId != null && userId != null) {
            webSocketService.submitAnswer(roomId, String.valueOf(question.getId()), selectedAnswer);
            Log.d(TAG, "Gửi đáp án cho câu " + question.getId() + ": " + selectedAnswer);
            if (selectedAnswer.equals(question.getCorrectAnswer())) {
                localScore++;
                Toast.makeText(this, "Chính xác!", Toast.LENGTH_SHORT).show();
                updateLocalRanking();
            } else {
                Toast.makeText(this, "Sai rồi!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Room ID hoặc User ID rỗng, không gửi được đáp án");
            Toast.makeText(this, "Lỗi: Không gửi được đáp án", Toast.LENGTH_SHORT).show();
        }

        btnOption1.setEnabled(false);
        btnOption2.setEnabled(false);
        btnOption3.setEnabled(false);
        btnOption4.setEnabled(false);
        Log.d(TAG, "Tắt nút đáp án sau khi trả lời câu " + question.getId());

        currentQuestionIndex++;
        loadQuestion();
    }

    private void updateLocalRanking() {
        for (GameRoomPlayer player : finalRankings) {
            if (player.getUser().getId() == Integer.parseInt(userId)) {
                player.setScore((long) localScore);
                break;
            }
        }
        finalRankings.sort((p1, p2) -> Long.compare(p2.getScore(), p1.getScore()));
        rankingAdapter.updateRankings(finalRankings);
        Log.d(TAG, "Cập nhật bảng xếp hạng cục bộ: " + new Gson().toJson(finalRankings));
    }

    private void hideAnswerButtons() {
        btnOption1.setVisibility(View.GONE);
        btnOption1.setEnabled(false);
        btnOption2.setVisibility(View.GONE);
        btnOption2.setEnabled(false);
        btnOption3.setVisibility(View.GONE);
        btnOption3.setEnabled(false);
        btnOption4.setVisibility(View.GONE);
        btnOption4.setEnabled(false);
        Log.d(TAG, "Ẩn và tắt nút đáp án");
    }

    private void finishQuiz() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isGameEnded = true;
        hideAnswerButtons();
        if (roomId != null && userId != null) {
            webSocketService.endGame(roomId, userId);
            Log.d(TAG, "Gửi yêu cầu kết thúc trò chơi cho phòng: " + roomId);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (!isFinishing() && !isDestroyed()) {
                    Log.d(TAG, "Hiển thị bảng xếp hạng qua fallback");
                    showRankingDialog();
                } else {
                    Log.d(TAG, "Bỏ qua fallback vì Activity đang kết thúc");
                    navigateToRoomActivity();
                    finish();
                }
            }, 5000);
        } else {
            showRankingDialog();
        }
    }

    private void showRankingDialog() {
        if (isFinishing() || isDestroyed()) {
            Log.d(TAG, "Activity đã kết thúc, không hiển thị dialog, isFinishing: " + isFinishing());
            if (!isBackPressed) {
                navigateToRoomActivity();
                finish();
            }
            return;
        }
        isRankingDialogShown = true;
        Log.d(TAG, "showRankingDialog gọi, isFinishing: " + isFinishing() + ", isBackPressed: " + isBackPressed);
        Toast.makeText(this, "Trò chơi kết thúc!", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kết thúc trò chơi - Bảng xếp hạng");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ranking, null);
        RecyclerView rvDialogRanking = dialogView.findViewById(R.id.rv_dialog_ranking);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);

        rvDialogRanking.setLayoutManager(new LinearLayoutManager(this));
        RankingAdapter dialogRankingAdapter = new RankingAdapter();
        rvDialogRanking.setAdapter(dialogRankingAdapter);

        // Đảm bảo có ít nhất người chơi hiện tại
        if (finalRankings.isEmpty()) {
            GameRoomPlayer player = new GameRoomPlayer();
            User user = new User();
            user.setId(userId != null ? Integer.parseInt(userId) : 0);
            user.setUsername("Bạn");
            user.setStatus(true);
            player.setUser(user);
            player.setScore((long) localScore);
            finalRankings.add(player);
        }
        finalRankings.sort((p1, p2) -> Long.compare(p2.getScore(), p1.getScore()));
        dialogRankingAdapter.updateRankings(finalRankings);
        Log.d(TAG, "Cập nhật dialog xếp hạng với " + finalRankings.size() + " người chơi: " + new Gson().toJson(finalRankings));

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            navigateToRoomActivity();
            finish();
            Log.d(TAG, "Nhấn OK, chuyển hướng và finish QuizActivity");
        });

        dialog.setCancelable(false);
        try {
            if (!isFinishing() && !isDestroyed()) {
                dialog.show();
                Log.d(TAG, "Hiển thị dialog xếp hạng thành công");
            } else {
                Log.d(TAG, "Không hiển thị dialog vì Activity đã kết thúc");
                if (!isBackPressed) {
                    navigateToRoomActivity();
                    finish();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi hiển thị dialog: " + e.getMessage());
            Toast.makeText(this, "Lỗi hiển thị bảng xếp hạng", Toast.LENGTH_SHORT).show();
            if (!isBackPressed) {
                navigateToRoomActivity();
                finish();
            }
        }
    }

    @Override
    public void onRankingUpdate(String rankingData) {
        runOnUiThread(() -> {
            Log.d(TAG, "Nhận dữ liệu xếp hạng: " + rankingData);
            try {
                List<GameRoomPlayer> rankings = new Gson().fromJson(rankingData, new TypeToken<List<GameRoomPlayer>>(){}.getType());
                if (rankings != null) {
                    finalRankings = new ArrayList<>(rankings);
                    finalRankings.sort((p1, p2) -> Long.compare(p2.getScore(), p1.getScore()));
                    for (GameRoomPlayer player : finalRankings) {
                        if (player.getUser() != null && player.getUser().getId() == Integer.parseInt(userId)) {
                            localScore = (int) player.getScore();
                            Toast.makeText(this, "Điểm của bạn: " + player.getScore(), Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    rankingAdapter.updateRankings(finalRankings);
                    Log.d(TAG, "Cập nhật bảng xếp hạng với " + finalRankings.size() + " người chơi: " + new Gson().toJson(finalRankings));
                    rvRanking.setVisibility(View.VISIBLE);
                } else {
                    Log.w(TAG, "Dữ liệu xếp hạng rỗng");
                }
            } catch (Exception e) {
                Log.e(TAG, "Lỗi parse dữ liệu xếp hạng: " + e.getMessage());
                Toast.makeText(this, "Lỗi cập nhật bảng xếp hạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onGameEnd(String roomId) {
        runOnUiThread(() -> {
            Log.d(TAG, "Nhận tín hiệu kết thúc cho phòng: " + roomId + ", isFinishing: " + isFinishing());
            if (isFinishing() || isDestroyed() || isGameEnded) {
                Log.d(TAG, "Bỏ qua onGameEnd vì activity kết thúc hoặc trò chơi đã kết thúc");
                return;
            }
            isGameEnded = true;
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            hideAnswerButtons();
            showRankingDialog();
        });
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> {
            Log.e(TAG, "Lỗi WebSocket: " + error);
            Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (roomId != null) {
            webSocketService.unsubscribeFromRanking(roomId);
            webSocketService.unsubscribeFromGameEnd(roomId);
        }
        webSocketService.removeListener(this);
        Log.d(TAG, "onStop gọi, hủy đăng ký WebSocket");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        Log.d(TAG, "onDestroy gọi, isFinishing: " + isFinishing());
    }
}