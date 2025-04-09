package com.myjob.real_time_chat_final.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.Question;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;
import com.myjob.real_time_chat_final.api.CategoryService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity {
    private TextView tvQuestionCount, tvQuestionTitle;
    private ImageView imgQuestion;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;
    private ProgressBar progressBar;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int totalQuestions;
    private int score = 0; // Điểm số của người chơi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Ánh xạ view
        tvQuestionCount = findViewById(R.id.tv_question_count);
        tvQuestionTitle = findViewById(R.id.tv_question_title);
        imgQuestion = findViewById(R.id.img_question);
        progressBar = findViewById(R.id.progress_bar);
        btnOption1 = findViewById(R.id.btn_option1);
        btnOption2 = findViewById(R.id.btn_option2);
        btnOption3 = findViewById(R.id.btn_option3);
        btnOption4 = findViewById(R.id.btn_option4);

        // Nhận category ID từ Intent
        long categoryId = getIntent().getLongExtra("CATEGORY_ID", -1);

        if (categoryId != -1) {
            fetchQuestions(categoryId);
        } else {
            Toast.makeText(this, "Không có danh mục hợp lệ!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchQuestions(long categoryId) {
        CategoryService categoryService = RetrofitClient.getApiCategoryService();
        Call<List<Question>> call = categoryService.getQuestionsByCategory(categoryId);

        call.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questionList = response.body();
                    totalQuestions = questionList.size();
                    if (totalQuestions > 0) {
                        loadQuestion();
                    } else {
                        Toast.makeText(QuizActivity.this, "Không có câu hỏi nào!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(QuizActivity.this, "Không tải được câu hỏi!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                Log.e("QuizActivity", "Lỗi khi lấy câu hỏi", t);
                Toast.makeText(QuizActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadQuestion() {
        if (currentQuestionIndex < totalQuestions) {
            Question question = questionList.get(currentQuestionIndex);

            // Cập nhật UI
            tvQuestionCount.setText("Q" + (currentQuestionIndex + 1) + "/" + totalQuestions);
            tvQuestionTitle.setText(question.getQuestion());
            progressBar.setProgress((currentQuestionIndex + 1) * 100 / totalQuestions);

            // Hiển thị hình ảnh (nếu có)
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

            // Xử lý sự kiện chọn đáp án
            btnOption1.setOnClickListener(v -> checkAnswer(question, question.getOptionA()));
            btnOption2.setOnClickListener(v -> checkAnswer(question, question.getOptionB()));
            btnOption3.setOnClickListener(v -> checkAnswer(question, question.getOptionC()));
            btnOption4.setOnClickListener(v -> checkAnswer(question, question.getOptionD()));
        } else {
            finishQuiz();
        }
    }

    private void checkAnswer(Question question, String selectedAnswer) {
        if (selectedAnswer.equals(question.getCorrectAnswer())) {
            score++;
            Toast.makeText(this, "Chính xác!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sai rồi!", Toast.LENGTH_SHORT).show();
        }

        // Chuyển sang câu hỏi tiếp theo
        currentQuestionIndex++;
        loadQuestion();
    }

    private void finishQuiz() {
        Toast.makeText(this, "Hoàn thành! Điểm số: " + score + "/" + totalQuestions, Toast.LENGTH_LONG).show();
        finish(); // Đóng activity hoặc chuyển hướng sang màn hình kết quả
    }
}
