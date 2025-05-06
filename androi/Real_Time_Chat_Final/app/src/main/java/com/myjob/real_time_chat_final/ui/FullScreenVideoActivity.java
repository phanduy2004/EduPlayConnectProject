package com.myjob.real_time_chat_final.ui;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.myjob.real_time_chat_final.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullScreenVideoActivity extends AppCompatActivity {
    private ExoPlayer player;
    private PlayerView playerView;
    private ImageButton closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);

        String url = getIntent().getStringExtra("url");
        playerView = findViewById(R.id.playerView);
        closeButton = findViewById(R.id.closeButton);
        // Khởi tạo ExoPlayer (như trước)
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        // Tạo và chuẩn bị video
        Uri videoUri = Uri.parse(url);
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.seekTo(0);

        player.play();
        // Đóng video khi nhấn nút
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
                finish();  // Hoặc bạn có thể dùng `onBackPressed()` để quay lại Activity trước
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}

