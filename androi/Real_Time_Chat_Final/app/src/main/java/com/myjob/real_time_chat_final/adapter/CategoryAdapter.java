package com.myjob.real_time_chat_final.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categories;
    private OnGameModeClickListener onGameModeClickListener;
    private int expandedPosition = -1; // Lưu trạng thái mở rộng

    public CategoryAdapter(List<Category> categories, OnGameModeClickListener onGameModeClickListener) {
        this.categories = categories;
        this.onGameModeClickListener = onGameModeClickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        Log.d("CategoryAdapter", "Category: " + category.getName() + ", Image: " + category.getImage());

        // Set tên danh mục
        holder.categoryName.setText(category.getName());

        // Load hình ảnh bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(category.getImage()) // Đảm bảo `getImageUrl()` trả về đúng URL
                .placeholder(R.drawable.ic_launcher_background) // Ảnh tạm khi đang load
                .error(R.drawable.ic_launcher_background) // Ảnh khi lỗi
                .into(holder.categoryImage);

        // Kiểm tra xem mục có đang mở rộng không
        boolean isExpanded = position == expandedPosition;
        holder.gameModeContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // Xử lý sự kiện click để mở rộng/thu gọn danh mục
        holder.itemView.setOnClickListener(v -> {
            int oldExpandedPosition = expandedPosition;
            expandedPosition = (expandedPosition == position) ? -1 : position;

            // Cập nhật item cũ và mới
            notifyItemChanged(oldExpandedPosition);
            notifyItemChanged(expandedPosition);
        });

        // Xử lý sự kiện cho các nút chế độ chơi
        holder.btnSinglePlayer.setOnClickListener(v -> {
            if (onGameModeClickListener != null) {
                onGameModeClickListener.onSinglePlayerSelected(category);
            }
        });

        holder.btnMultiPlayer.setOnClickListener(v -> {
            if (onGameModeClickListener != null) {
                onGameModeClickListener.onMultiPlayerSelected(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (categories != null) ? categories.size() : 0;
    }

    // Cập nhật danh sách danh mục mà không cần tạo Adapter mới
    public void setCategories(List<Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName;
        LinearLayout gameModeContainer;
        Button btnSinglePlayer, btnMultiPlayer;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryName = itemView.findViewById(R.id.categoryName);
            gameModeContainer = itemView.findViewById(R.id.gameModeContainer);
            btnSinglePlayer = itemView.findViewById(R.id.btnSinglePlayer);
            btnMultiPlayer = itemView.findViewById(R.id.btnMultiPlayer);
        }
    }

    public interface OnGameModeClickListener {
        void onSinglePlayerSelected(Category category);
        void onMultiPlayerSelected(Category category);
    }
}
