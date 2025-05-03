package com.myjob.real_time_chat_final.api;

import com.myjob.real_time_chat_final.model.Topic;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TopicService {

    @GET("/api/topic")
    Call<List<Topic>> getTopics();
}
