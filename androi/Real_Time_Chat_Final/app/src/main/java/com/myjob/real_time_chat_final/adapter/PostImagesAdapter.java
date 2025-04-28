package com.myjob.real_time_chat_final.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.retrofit.RetrofitClient;
import java.util.List;
import android.graphics.drawable.Drawable;

public class PostImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "PostImagesAdapter";
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_SEE_MORE = 2;
    private final List<String> images;
    private final int displayImageCount; // Số ảnh hiển thị (tối đa 4)
    private OnSeeMoreClickListener seeMoreClickListener;

    // Interface cho sự kiện "Xem thêm"
    public interface OnSeeMoreClickListener {
        void onSeeMoreClick();
    }

    // Constructor nhận List<String> và OnSeeMoreClickListener
    public PostImagesAdapter(List<String> images, OnSeeMoreClickListener listener) {
        this.images = images;
        this.seeMoreClickListener = listener;
        this.displayImageCount = Math.min(images.size(), 4); // Hiển thị tối đa 4 ảnh
    }

    @Override
    public int getItemViewType(int position) {
        if (position < displayImageCount) {
            return TYPE_IMAGE;
        } else {
            return TYPE_SEE_MORE;
        }
    }

    @Override
    public int getItemCount() {
        if (images.size() <= 4) {
            return images.size();
        } else {
            return displayImageCount + 1; // 4 ảnh + 1 "See more"
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_image, parent, false);
            return new ImageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_see_more, parent, false);
            return new SeeMoreViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_IMAGE) {
            ImageViewHolder imageHolder = (ImageViewHolder) holder;
            String imageUrl = images.get(position);
            if (imageUrl != null && !imageUrl.isEmpty() && imageUrl.contains("/uploads/")) {
                String fullImageUrl = RetrofitClient.getBaseUrl() + imageUrl;
                Log.d(TAG, "Loading image URL: " + fullImageUrl);

                // Tùy chỉnh kích thước dựa trên số lượng ảnh
                int imageCount = Math.min(images.size(), 4);
                int width = 300;
                int height = 300;
                if (imageCount == 1) {
                    width = 600; // Ảnh lớn hơn nếu chỉ có 1 ảnh
                    height = (int) (width * 9.0 / 16.0); // Tỷ lệ 16:9
                } else if (imageCount == 3 && position == 0) {
                    width = 400; // Ảnh lớn (2/3 chiều rộng)
                    height = 400;
                } else if (imageCount == 3 && position > 0) {
                    width = 200; // Ảnh nhỏ (1/3 chiều rộng)
                    height = 200;
                }

                Glide.with(imageHolder.imageView.getContext())
                        .load(fullImageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .override(width, height)
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e(TAG, "Glide load failed for image: " + fullImageUrl, e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                Log.d(TAG, "Glide load successful for image: " + fullImageUrl);
                                return false;
                            }
                        })
                        .into(imageHolder.imageView);
            } else {
                Log.w(TAG, "Invalid image URL at position " + position + ": " + imageUrl);
                imageHolder.imageView.setImageResource(R.drawable.ic_user);
            }
        } else {
            SeeMoreViewHolder seeMoreHolder = (SeeMoreViewHolder) holder;
            int remainingImages = images.size() - displayImageCount;
            seeMoreHolder.seeMoreText.setText("+" + remainingImages);
            seeMoreHolder.itemView.setOnClickListener(v -> {
                if (seeMoreClickListener != null) {
                    seeMoreClickListener.onSeeMoreClick();
                }
            });
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item);
        }
    }

    static class SeeMoreViewHolder extends RecyclerView.ViewHolder {
        TextView seeMoreText;

        public SeeMoreViewHolder(@NonNull View itemView) {
            super(itemView);
            seeMoreText = itemView.findViewById(R.id.see_more_text);
        }
    }
}