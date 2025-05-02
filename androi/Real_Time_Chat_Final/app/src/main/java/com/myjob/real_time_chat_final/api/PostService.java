package com.myjob.real_time_chat_final.api;

import android.os.IInterface;

import com.myjob.real_time_chat_final.model.ImageResponse;
import com.myjob.real_time_chat_final.model.Post;
import com.myjob.real_time_chat_final.modelDTO.PostRequestDTO;
import com.myjob.real_time_chat_final.modelDTO.PostResponseDTO;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface PostService {
    @Multipart
    @POST("api/posts/upload")
    Call<List<ImageResponse>> uploadImages(@Part List<MultipartBody.Part> files);

    @POST("api/posts/create")
    Call<PostResponseDTO> createPost(@Body PostRequestDTO request);


    @GET("api/posts")
    Call<List<PostResponseDTO>> getPosts(
            @Query("page") int page,
            @Query("size") int size,
            @Query("userId") Long userId
    );
    // API để Like một bài đăng
    @POST("likes/post/{postId}/user/{userId}")
    Call<ResponseBody> likePost(
            @Path("postId") Long postId,
            @Path("userId") Long userId
    );

    // API để tạo bình luận
   /* @POST("comments/post/{postId}/user/{userId}")
    @FormUrlEncoded
    Call<PostResponseDTO> createComment(
            @Path("postId") Long postId,
            @Path("userId") Long userId,
            @Field("content") String content,
            @Field("parentCommentId") Long parentCommentId
    );*/
}
