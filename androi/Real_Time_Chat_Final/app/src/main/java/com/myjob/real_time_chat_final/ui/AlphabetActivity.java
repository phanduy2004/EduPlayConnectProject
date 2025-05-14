package com.myjob.real_time_chat_final.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.AlphabetAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AlphabetActivity extends AppCompatActivity {

    private static final String TAG = "AlphabetActivity";
    private RecyclerView recyclerView;
    private TextToSpeech textToSpeech;
    private boolean isTtsReady = false;
    private boolean isSoundEnabled = true; // For sound toggle
    private Dialog currentDialog; // Track dialog to prevent leaks

    private final List<String> alphabetList = Arrays.asList(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alphabet);

        recyclerView = findViewById(R.id.recyclerViewAlphabet);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Language not supported or missing data!");
                    isTtsReady = false;
                } else {
                    isTtsReady = true;
                    Log.d(TAG, "TextToSpeech initialized successfully!");
                }
            } else {
                Log.e(TAG, "TextToSpeech initialization failed!");
                isTtsReady = false;
            }
        });

        AlphabetAdapter adapter = new AlphabetAdapter(alphabetList, this::showLetterDialog);
        recyclerView.setAdapter(adapter);

        // Configure toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Handle back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Handle sound toggle FAB
        FloatingActionButton fabSound = findViewById(R.id.fabSound);
        fabSound.setOnClickListener(v -> {
            isSoundEnabled = !isSoundEnabled;
            fabSound.setImageResource(isSoundEnabled ? R.drawable.ic_sound : R.drawable.ic_sound_off);
            Toast.makeText(this, isSoundEnabled ? "Sound ON" : "Sound OFF", Toast.LENGTH_SHORT).show();
        });
    }

    private void showLetterDialog(String letter) {
        if (isFinishing() || isDestroyed()) {
            Log.d(TAG, "Activity is finishing, skip showing dialog");
            return;
        }

        Dialog dialog = new Dialog(this);
        currentDialog = dialog; // Track dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_letter_info);
        dialog.setCancelable(true);

        // Customize dialog window
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.75f;
            windowParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
            window.setAttributes(windowParams);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setWindowAnimations(R.style.DialogAnimation);
        }

        TextView textViewLetter = dialog.findViewById(R.id.textViewLetter);
        TextView textViewDescription = dialog.findViewById(R.id.textViewDescription);
        ImageView imageViewLetter = dialog.findViewById(R.id.imageViewLetter);
        MaterialButton btnClose = dialog.findViewById(R.id.btnClose);
        FloatingActionButton fabReplaySound = dialog.findViewById(R.id.fabReplaySound);

        String description = getLetterDescription(letter);
        textViewLetter.setText(letter);
        textViewDescription.setText(description);
        imageViewLetter.setImageResource(getLetterImage(letter));

        // Apply animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        dialog.findViewById(R.id.dialogLayout).startAnimation(fadeIn);

        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_in);
        imageViewLetter.startAnimation(scaleAnimation);

        // Speak letter and description
        speakText(letter + ". " + description);

        // Handle close button
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Handle replay sound button
        fabReplaySound.setOnClickListener(v -> speakText(letter + ". " + description));

        // Clear dialog reference when dismissed
        dialog.setOnDismissListener(d -> currentDialog = null);

        try {
            dialog.show();
            Log.d(TAG, "Letter dialog shown for: " + letter);
        } catch (Exception e) {
            Log.e(TAG, "Error showing dialog: " + e.getMessage());
            currentDialog = null;
        }
    }

    private void speakText(String text) {
        if (isSoundEnabled && isTtsReady && textToSpeech != null) {
            int result = textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            if (result == TextToSpeech.ERROR) {
                Log.e(TAG, "Error in TextToSpeech speak()");
            }
        } else if (!isSoundEnabled) {
            Log.d(TAG, "Sound is disabled, skipping TextToSpeech");
        } else {
            Log.e(TAG, "TextToSpeech not ready or initialized!");
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
            case "D": return R.drawable.d;
            case "E": return R.drawable.e;
            case "F": return R.drawable.f;
            case "G": return R.drawable.g;
            case "H": return R.drawable.h;
            case "I": return R.drawable.i;
            case "J": return R.drawable.j;
            case "K": return R.drawable.k;
            case "L": return R.drawable.l;
            case "M": return R.drawable.m;
            case "N": return R.drawable.n;
            case "O": return R.drawable.o;
            case "P": return R.drawable.p;
            case "Q": return R.drawable.q;
            case "R": return R.drawable.r;
            case "S": return R.drawable.s;
            case "T": return R.drawable.t;
            case "U": return R.drawable.u;
            case "V": return R.drawable.v;
            case "W": return R.drawable.w;
            case "X": return R.drawable.x;
            case "Y": return R.drawable.y;
            case "Z": return R.drawable.z;
            default: return R.drawable.placeholder;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Dismiss dialog to prevent window leak
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
            currentDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
        Log.d(TAG, "Activity destroyed");
    }
}