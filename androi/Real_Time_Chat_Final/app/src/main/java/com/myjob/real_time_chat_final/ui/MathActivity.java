package com.myjob.real_time_chat_final.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.myjob.real_time_chat_final.R;

import java.util.Random;

public class MathActivity extends AppCompatActivity {

    private TextView questionText;
    private Button answerButton1, answerButton2, answerButton3, answerButton4;
    private TextView resultText;

    private int correctAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math);

        // Khởi tạo các view
        questionText = findViewById(R.id.questionText);
        answerButton1 = findViewById(R.id.answerButton1);
        answerButton2 = findViewById(R.id.answerButton2);
        answerButton3 = findViewById(R.id.answerButton3);
        answerButton4 = findViewById(R.id.answerButton4);
        resultText = findViewById(R.id.resultText);

        // Tạo phép tính ngẫu nhiên
        generateQuestion();

        // Gán sự kiện nhấn cho các nút đáp án để kiểm tra ngay lập tức khi nhấn
        setAnswerButtonClickListener(answerButton1);
        setAnswerButtonClickListener(answerButton2);
        setAnswerButtonClickListener(answerButton3);
        setAnswerButtonClickListener(answerButton4);
    }

    private void generateQuestion() {
        // Chọn mức độ khó (Dễ, Trung bình, Khó)
        String difficulty = "medium"; // "easy", "medium", "hard"

        Random random = new Random();
        int num1 = 0, num2 = 0;
        int operation = random.nextInt(4); // 0: cộng, 1: trừ, 2: nhân, 3: chia
        String operationSymbol = "";

        // Chọn số ngẫu nhiên và phép toán tùy theo độ khó
        switch (difficulty) {
            case "easy":
                num1 = random.nextInt(10) + 1; // Số ngẫu nhiên từ 1 đến 10
                num2 = random.nextInt(10) + 1;
                break;
            case "medium":
                num1 = random.nextInt(50) + 1; // Số ngẫu nhiên từ 1 đến 50
                num2 = random.nextInt(50) + 1;
                break;
            case "hard":
                num1 = random.nextInt(100) + 1; // Số ngẫu nhiên từ 1 đến 100
                num2 = random.nextInt(100) + 1;
                break;
        }

        // Tạo phép toán ngẫu nhiên
        int result = 0;
        switch (operation) {
            case 0: // Cộng
                result = num1 + num2;
                operationSymbol = " + ";
                break;
            case 1: // Trừ
                result = num1 - num2;
                operationSymbol = " - ";
                break;
            case 2: // Nhân
                result = num1 * num2;
                operationSymbol = " * ";
                break;
            case 3: // Chia (với điều kiện chia hết)
                if (num2 == 0) num2 = 1; // Tránh chia cho 0

                // Đảm bảo num1 chia hết cho num2
                num1 = num2 * (random.nextInt(10) + 1);  // Đảm bảo num1 chia hết cho num2

                result = num1 / num2;
                operationSymbol = " / ";
                break;
        }

        // Hiển thị phép tính
        correctAnswer = result;
        questionText.setText(num1 + operationSymbol + num2);

        // Tạo các đáp án sai ngẫu nhiên
        int wrongAnswer1 = correctAnswer + random.nextInt(5) + 1;
        int wrongAnswer2 = correctAnswer - random.nextInt(3) - 1;
        int wrongAnswer3 = correctAnswer + random.nextInt(7) + 1;

        // Đảm bảo một trong các đáp án là đúng
        int[] answers = {correctAnswer, wrongAnswer1, wrongAnswer2, wrongAnswer3};
        // Shuffle đáp án để trộn vị trí
        for (int i = 0; i < answers.length; i++) {
            int randomIndex = random.nextInt(answers.length);
            int temp = answers[i];
            answers[i] = answers[randomIndex];
            answers[randomIndex] = temp;
        }

        // Gán đáp án cho các button
        answerButton1.setText(String.valueOf(answers[0]));
        answerButton2.setText(String.valueOf(answers[1]));
        answerButton3.setText(String.valueOf(answers[2]));
        answerButton4.setText(String.valueOf(answers[3]));
    }


    private void setAnswerButtonClickListener(Button answerButton) {
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy giá trị của đáp án đã chọn từ nút nhấn
                int selectedAnswer = Integer.parseInt(((Button) v).getText().toString());

                // Kiểm tra kết quả
                if (selectedAnswer == correctAnswer) {
                    resultText.setText("Đúng rồi!");
                    resultText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else {
                    resultText.setText("Sai rồi! Đáp án đúng là: " + correctAnswer);
                    resultText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }

                // Tạo lại phép tính mới sau khi kiểm tra
                generateQuestion();
            }
        });
    }
}
