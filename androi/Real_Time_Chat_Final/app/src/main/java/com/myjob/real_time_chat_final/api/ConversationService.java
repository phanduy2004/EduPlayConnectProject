package com.myjob.real_time_chat_final.api;

import com.myjob.real_time_chat_final.model.Conversation;
import com.myjob.real_time_chat_final.model.ConversationMember;
import com.myjob.real_time_chat_final.modelDTO.ContactDTO;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    @GET("api/conversations/contacts")
    Call<List<ContactDTO>> getContacts(@Query("user_id") int userId);

    @Multipart
    @POST("api/conversations/uploadGroupAvatar")
    Call<ResponseBody> uploadGroupAvatar(@Part MultipartBody.Part avatar);
}