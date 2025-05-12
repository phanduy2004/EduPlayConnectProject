package com.myjob.real_time_chat_final.api;

import com.myjob.real_time_chat_final.modelDTO.ScoreDTO;
import com.myjob.real_time_chat_final.ui.MathActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ScoreService {
    @FormUrlEncoded
    @POST("/api/scores/save")
    Call<ScoreDTO> saveScore(@Field("userId") Long userId, @Field("score") int score);

    @GET("/api/scores/top5")
    Call<List<ScoreDTO>> getTop5Scores();
}
