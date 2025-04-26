package com.myjob.real_time_chat_final.adapter;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.Story;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {
    private List<Story> storyList;

    public StoryAdapter(List<Story> storyList) {
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = storyList.get(position);
        if (story.isCreateStory()) {
            holder.createStoryLayout.setVisibility(View.VISIBLE);
            holder.storyAvatar.setVisibility(View.GONE);
            holder.storyBorder.setVisibility(View.GONE);
            holder.storyImageContainer.setVisibility(View.GONE);
            holder.storyUsername.setVisibility(View.GONE);
        } else {
            holder.createStoryLayout.setVisibility(View.GONE);
            holder.storyAvatar.setVisibility(View.VISIBLE);
            holder.storyBorder.setVisibility(View.VISIBLE);
            holder.storyImageContainer.setVisibility(View.VISIBLE);
            holder.storyUsername.setVisibility(View.VISIBLE);
            holder.storyUsername.setText(story.getUsername());

            if (story.getAvatarResId() != null) {
                holder.storyAvatar.setImageResource(story.getAvatarResId());
            } else {
                holder.storyAvatar.setImageResource(R.drawable.ic_user);
            }

            if (story.getImageResId() != null) {
                holder.storyImage.setImageResource(story.getImageResId());
            } else {
                holder.storyImage.setImageResource(R.drawable.ic_user);
            }

            int borderColor = story.isUnseen() ? Color.GREEN : Color.BLUE;
            holder.storyBorder.setBackgroundTintList(ColorStateList.valueOf(borderColor));
            holder.storyImageBorder.setBackgroundTintList(ColorStateList.valueOf(borderColor));
        }
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    static class StoryViewHolder extends RecyclerView.ViewHolder {
        LinearLayout createStoryLayout;
        FrameLayout storyImageContainer;
        ImageView storyAvatar, storyImage, storyBorder, storyImageBorder;
        TextView storyUsername;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            createStoryLayout = itemView.findViewById(R.id.create_story_layout);
            storyAvatar = itemView.findViewById(R.id.story_avatar);
            storyImage = itemView.findViewById(R.id.story_image);
            storyBorder = itemView.findViewById(R.id.story_border);
            storyImageBorder = itemView.findViewById(R.id.story_image_border);
            storyImageContainer = itemView.findViewById(R.id.story_image_container);
            storyUsername = itemView.findViewById(R.id.story_username);
        }
    }
}