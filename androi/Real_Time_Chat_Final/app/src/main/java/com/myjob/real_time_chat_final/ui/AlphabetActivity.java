package com.myjob.real_time_chat_final.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.AlphabetAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AlphabetActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextToSpeech textToSpeech;

    private List<String> alphabetList = Arrays.asList(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    );

    private boolean isTtsReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alphabet);

        recyclerView = findViewById(R.id.recyclerViewAlphabet);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Ngôn ngữ không được hỗ trợ hoặc thiếu dữ liệu!");
                    isTtsReady = false;
                } else {
                    isTtsReady = true;
                    Log.d("TTS", "TextToSpeech đã sẵn sàng!");
                }
            } else {
                Log.e("TTS", "Khởi tạo TextToSpeech thất bại!");
                isTtsReady = false;
            }
        });

        AlphabetAdapter adapter = new AlphabetAdapter(alphabetList, this::showLetterDialog);
        recyclerView.setAdapter(adapter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hiển thị nút quay lại
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showLetterDialog(String letter) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_letter_info);
        dialog.setCancelable(true);

        TextView textViewLetter = dialog.findViewById(R.id.textViewLetter);
        TextView textViewDescription = dialog.findViewById(R.id.textViewDescription);
        ImageView imageViewLetter = dialog.findViewById(R.id.imageViewLetter);

        String description = getLetterDescription(letter);

        textViewLetter.setText(letter);
        textViewDescription.setText(description);
        imageViewLetter.setImageResource(getLetterImage(letter));

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        dialog.findViewById(R.id.dialogLayout).startAnimation(fadeIn);

        // Đọc cả chữ cái và mô tả
        speakText(letter + ". " + description);

        dialog.show();
    }


    private void speakText(String text) {
        if (isTtsReady && textToSpeech != null) {
            int result = textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            if (result == TextToSpeech.ERROR) {
                Log.e("TTS", "Lỗi khi gọi speak()");
            }
        } else {
            Log.e("TTS", "TextToSpeech chưa sẵn sàng hoặc bị lỗi!");
        }
    }

    private String getLetterDescription(String letter) {
        switch (letter) {
            case "A": return "A is for Apple 🍎";
            case "B": return "B is for Ball ⚽";
            case "C": return "C is for Cat 🐱";
            case "D": return "D is for Dog 🐶";
            case "E": return "E is for Elephant 🐘";
            case "F": return "F is for Fish 🐠";
            case "G": return "G is for Giraffe 🦒";
            case "H": return "H is for Horse 🐴";
            case "I": return "I is for Ice Cream 🍦";
            case "J": return "J is for Jellyfish 🪼";
            case "K": return "K is for Kangaroo 🦘";
            case "L": return "L is for Lion 🦁";
            case "M": return "M is for Monkey 🐵";
            case "N": return "N is for Nest 🏡";
            case "O": return "O is for Octopus 🐙";
            case "P": return "P is for Penguin 🐧";
            case "Q": return "Q is for Queen 👑";
            case "R": return "R is for Rabbit 🐰";
            case "S": return "S is for Sun ☀️";
            case "T": return "T is for Turtle 🐢";
            case "U": return "U is for Umbrella ☔";
            case "V": return "V is for Violin 🎻";
            case "W": return "W is for Whale 🐋";
            case "X": return "X is for Xylophone 🎶";
            case "Y": return "Y is for Yacht ⛵";
            case "Z": return "Z is for Zebra 🦓";
            default: return "This is the letter " + letter;
        }
    }

    private int getLetterImage(String letter) {
        switch (letter) {
            case "A": return R.drawable.a;
            case "B": return R.drawable.b;
            case "C": return R.drawable.c;
            default: return R.drawable.placeholder;
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
