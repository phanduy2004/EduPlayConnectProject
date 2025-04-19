package com.myjob.real_time_chat_final.api;

import com.myjob.real_time_chat_final.model.Conversation;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ConversationService {
    @POST("api/conversations/{userId}/{friendId}")
    Call<Conversation> createConversation(
            @Path("userId") int userId,
            @Path("friendId") int friendId
    );
}
