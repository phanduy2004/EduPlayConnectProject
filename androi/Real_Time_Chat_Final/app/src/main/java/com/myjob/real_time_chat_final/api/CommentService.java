package com.myjob.real_time_chat_final.api;

import com.myjob.real_time_chat_final.modelDTO.CommentDTO;
import com.myjob.real_time_chat_final.modelDTO.CommentRequestDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CommentService {
    @POST("api/comments")
    Call<CommentDTO> createComment(@Body CommentRequestDTO commentRequestDTO);
}
