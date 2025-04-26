package com.myjob.real_time_chat_final.api;

import android.os.IInterface;

import com.myjob.real_time_chat_final.model.ImageResponse;
import com.myjob.real_time_chat_final.model.Post;
import com.myjob.real_time_chat_final.modelDTO.PostRequestDTO;
import com.myjob.real_time_chat_final.modelDTO.PostResponseDTO;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface PostService {
    @Multipart
    @POST("api/upload")
    Call<ImageResponse> uploadImage(@Part MultipartBody.Part image);

    @POST("api/posts/create")
    Call<PostResponseDTO> createPost(@Body PostRequestDTO request);
}
