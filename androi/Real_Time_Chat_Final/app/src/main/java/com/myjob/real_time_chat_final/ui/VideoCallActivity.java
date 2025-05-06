package com.myjob.real_time_chat_final.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.myjob.real_time_chat_final.R;

/*
import live.videosdk.rtc.android.Meeting;
import live.videosdk.rtc.android.Participant;
import live.videosdk.rtc.android.Stream;
import live.videosdk.rtc.android.VideoSDK;
import live.videosdk.rtc.android.listeners.MeetingEventListener;
import live.videosdk.rtc.android.listeners.ParticipantEventListener;
*/


public class VideoCallActivity extends AppCompatActivity {
    /*private Meeting meeting;*/
    private FrameLayout localVideoView, remoteVideoView;
    private ImageButton btnToggleCamera, btnToggleMic, btnEndCall;
    private boolean isMicEnabled = true;
    private boolean isCameraEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

       /* localVideoView = findViewById(R.id.localVideoView);
        remoteVideoView = findViewById(R.id.remoteVideoView);
        btnToggleCamera = findViewById(R.id.btnToggleCamera);
        btnToggleMic = findViewById(R.id.btnToggleMic);
        btnEndCall = findViewById(R.id.btnEndCall);

        if (!checkCameraHardware() || !checkMicrophoneHardware()) {
            Toast.makeText(this, "Thiết bị không hỗ trợ camera hoặc micro", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String meetingId = getIntent().getStringExtra("meeting_id");
        String token = getIntent().getStringExtra("token");

        if (meetingId == null || token == null) {
            Toast.makeText(this, "Không tìm thấy thông tin cuộc họp", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo Video SDK
        VideoSDK.initialize(getApplicationContext());
        meeting = VideoSDK.initMeeting(this, meetingId, token, "User Name", true, true, null);

        // Tham gia phòng họp
        meeting.join();

        // Thiết lập luồng video cục bộ (local participant)
        setupLocalVideo();

        // Lắng nghe sự kiện của Meeting
        meeting.addEventListener(new MeetingEventListener() {
            @Override
            public void onParticipantJoined(Participant participant) {
                // Lắng nghe sự kiện luồng video của người tham gia từ xa
                participant.addEventListener(new ParticipantEventListener() {
                    @Override
                    public void onStreamEnabled(Stream stream) {
                        if ("video".equals(stream.getKind())) {
                            // Hiển thị luồng video từ xa
                            stream.addToView(remoteVideoView);
                        }
                    }

                    @Override
                    public void onStreamDisabled(Stream stream) {
                        if ("video".equals(stream.getKind())) {
                            stream.removeFromView();
                        }
                    }
                });
            }

            @Override
            public void onParticipantLeft(Participant participant) {
                Toast.makeText(VideoCallActivity.this, "Người dùng đã rời cuộc gọi", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMeetingLeft() {
                finish();
            }
        });

        // Xử lý nút điều khiển
        btnToggleCamera.setOnClickListener(v -> {
            isCameraEnabled = !isCameraEnabled;
            meeting.toggleWebcam();
            btnToggleCamera.setImageResource(isCameraEnabled ? R.drawable.ic_camera_on : R.drawable.ic_camera_off);
        });

        btnToggleMic.setOnClickListener(v -> {
            isMicEnabled = !isMicEnabled;
            meeting.toggleMic();
            btnToggleMic.setImageResource(isMicEnabled ? R.drawable.ic_mic_on : R.drawable.ic_mic_off);
        });

        btnEndCall.setOnClickListener(v -> {
            meeting.leave();
        });
    }

    private void setupLocalVideo() {
        Participant localParticipant = meeting.getLocalParticipant();
        if (localParticipant != null) {
            localParticipant.addEventListener(new ParticipantEventListener() {
                @Override
                public void onStreamEnabled(Stream stream) {
                    if ("video".equals(stream.getKind())) {
                        // Hiển thị luồng video cục bộ
                        stream.addToView(localVideoView);
                    }
                }

                @Override
                public void onStreamDisabled(Stream stream) {
                    if ("video".equals(stream.getKind())) {
                        stream.removeFromView();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (meeting != null) {
            meeting.leave();
        }
    }

    private boolean checkCameraHardware() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private boolean checkMicrophoneHardware() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }*/
    }
}