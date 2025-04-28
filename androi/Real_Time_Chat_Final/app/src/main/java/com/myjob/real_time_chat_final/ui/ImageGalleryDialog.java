package com.myjob.real_time_chat_final.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myjob.real_time_chat_final.R;
import com.myjob.real_time_chat_final.adapter.PostImagesAdapter;
import java.util.ArrayList;

public class ImageGalleryDialog extends DialogFragment {

    private static final String ARG_IMAGE_URLS = "image_urls";
    private ArrayList<String> imageUrls;

    public static ImageGalleryDialog newInstance(ArrayList<String> imageUrls) {
        ImageGalleryDialog dialog = new ImageGalleryDialog();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_IMAGE_URLS, imageUrls);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrls = getArguments().getStringArrayList(ARG_IMAGE_URLS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_image_gallery, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.image_gallery_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        PostImagesAdapter adapter = new PostImagesAdapter(imageUrls, null);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}