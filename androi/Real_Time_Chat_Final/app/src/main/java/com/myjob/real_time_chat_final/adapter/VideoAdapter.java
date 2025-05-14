package com.myjob.real_time_chat_final.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.Video;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private static final String TAG = "VideoAdapter";
    private final List<Video> videoList;
    private final OnVideoClickListener listener;

    public interface OnVideoClickListener {
        void onVideoClick(Video video);
    }

    public VideoAdapter(List<Video> videoList, OnVideoClickListener listener) {
        this.videoList = videoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video_card, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);

        // Bind title
        holder.textTitle.setText(video.getTitle() != null ? video.getTitle() : "Untitled");

        // Hide description since duration is not available in Video model
        holder.textDescription.setVisibility(View.GONE);

        // Load thumbnail from background field
        if (video.getBackground() != null && !video.getBackground().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(video.getBackground())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.ic_video_default)
                    .into(holder.imageMovie);
        } else {
            holder.imageMovie.setImageResource(R.drawable.ic_video_default);
            Log.w(TAG, "No background image for video at position " + position);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVideoClick(video);
            }
        });

        // Apply animation
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.scale_in);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return videoList != null ? videoList.size() : 0;
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageMovie;
        TextView textTitle;
        TextView textDescription;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMovie = itemView.findViewById(R.id.imageMovie);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
        }
    }
}