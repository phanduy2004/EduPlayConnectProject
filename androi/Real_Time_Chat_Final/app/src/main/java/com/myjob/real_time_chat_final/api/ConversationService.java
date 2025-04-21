package com.myjob.real_time_chat_final.api;

import com.myjob.real_time_chat_final.model.Conversation;
import com.myjob.real_time_chat_final.model.ConversationMember;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ConversationService {
    @POST("api/conversations/{userId}/{friendId}")
    Call<Conversation> createConversation(
            @Path("userId") int userId,
            @Path("friendId") int friendId
    );

    @POST("/api/conversations")
    Call<Conversation> createGroupConversation(@Body Conversation conversation);

    @POST("/api/conversations/{conversationId}/members")
    Call<Void> addMembersToConversation(
            @Path("conversationId") Long conversationId,
            @Body List<ConversationMember> members
    );
}