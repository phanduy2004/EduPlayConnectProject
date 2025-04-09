package com.myjob.real_time_chat_final.api;

import com.myjob.real_time_chat_final.model.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MessageService {
    @GET("/api/message/conversation/{conversationId}")
    Call<List<Message>> getMessagesByConversationId(@Path("conversationId") long conversationId);
}
