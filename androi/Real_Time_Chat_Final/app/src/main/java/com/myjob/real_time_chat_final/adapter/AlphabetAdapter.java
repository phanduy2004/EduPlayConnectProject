package com.myjob.real_time_chat_final.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;

import java.util.List;

public class AlphabetAdapter extends RecyclerView.Adapter<AlphabetAdapter.ViewHolder> {

    private final List<String> alphabetList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String letter);
    }

    public AlphabetAdapter(List<String> alphabetList, OnItemClickListener listener) {
        this.alphabetList = alphabetList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_letter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String letter = alphabetList.get(position);
        holder.textView.setText(letter);

        // Áp dụng hiệu ứng động khi mục xuất hiện
        Context context = holder.itemView.getContext();
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_animation);
        holder.itemView.startAnimation(animation);

        // Hiệu ứng nhấn
        holder.itemView.setOnClickListener(v -> {
            holder.textView.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).withEndAction(() ->
                    holder.textView.animate().scaleX(1f).scaleY(1f).setDuration(200)
            );
            listener.onItemClick(letter);
        });
    }

    @Override
    public int getItemCount() {
        return alphabetList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewLetter);
        }
    }
}