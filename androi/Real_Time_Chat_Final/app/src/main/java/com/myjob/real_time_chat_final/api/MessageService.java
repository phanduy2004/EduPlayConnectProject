package com.myjob.real_time_chat_final.api;

import com.myjob.real_time_chat_final.model.Message;
import com.myjob.real_time_chat_final.modelDTO.PageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessageService {
    @GET("/api/message/conversation/{conversationId}")
    Call<PageResponse<Message>> getMessagesByConversationId(
            @Path("conversationId") long conversationId,
            @Query("page") int page,
            @Query("size") int size
    );
}
