package com.myjob.real_time_chat_final.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.myjob.real_time_chat_final.R;
import java.util.ArrayList;
import java.util.List;

public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.ImageViewHolder> {
    private final Context context;
    private final List<Uri> imageUris;
    private final OnRemoveImageListener removeImageListener;

    public interface OnRemoveImageListener {
        void onRemoveImage(int position);
    }

    public SelectedImagesAdapter(Context context, List<Uri> imageUris, OnRemoveImageListener listener) {
        this.context = context;
        this.imageUris = imageUris;
        this.removeImageListener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_selected_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        Glide.with(context)
                .load(imageUri)
                .thumbnail(0.25f)
                .override(100, 100) // Kích thước nhỏ cho preview
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(holder.imageView);

        holder.removeButton.setOnClickListener(v -> {
            if (removeImageListener != null) {
                removeImageListener.onRemoveImage(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton removeButton;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.selected_image);
            removeButton = itemView.findViewById(R.id.remove_image_button);
        }
    }
}