package com.myjob.real_time_chat_final.adapter;

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
import java.util.ArrayList;
import java.util.List;
import android.graphics.drawable.Drawable;

public class PostImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "PostImagesAdapter";
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_SEE_MORE = 2;
    private List<String> images;
    private final int displayImageCount;
    private OnSeeMoreClickListener seeMoreClickListener;

    public interface OnSeeMoreClickListener {
        void onSeeMoreClick();
    }

    public PostImagesAdapter(List<String> images, OnSeeMoreClickListener listener) {
        this.images = images != null ? new ArrayList<>(images) : new ArrayList<>();
        this.seeMoreClickListener = listener;
        this.displayImageCount = Math.min(this.images.size(), 6);
    }

    public void updateImages(List<String> newImages) {
        this.images = newImages != null ? new ArrayList<>(newImages) : new ArrayList<>();
        notifyDataSetChanged();
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
        if (images.size() <= 6) {
            return images.size();
        } else {
            return displayImageCount + 1; // 6 ảnh + 1 "See more"
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

                int imageCount = Math.min(images.size(), 6);
                int width = 300;
                int height = 300;
                if (imageCount == 1) {
                    width = 600;
                    height = (int) (width * 9.0 / 16.0);
                } else if (imageCount == 3 && position == 0) {
                    width = 400;
                    height = 400;
                } else if (imageCount == 3 && position > 0) {
                    width = 200;
                    height = 200;
                } else if (imageCount == 5 && position < 2) {
                    width = 300;
                    height = 300;
                } else if (imageCount == 5 && position >= 2) {
                    width = 200;
                    height = 200;
                }

                Glide.with(imageHolder.imageView.getContext())
                        .load(fullImageUrl)
                        .thumbnail(0.25f)
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