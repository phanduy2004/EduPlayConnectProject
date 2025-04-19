package com.myjob.real_time_chat_final.api;

import com.myjob.real_time_chat_final.model.Friendship;
import com.myjob.real_time_chat_final.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FriendshipService {
    @GET("/api/friends/{userId}")
    Call<List<User>> getFriendList(@Path("userId") int userId);

    @GET("api/friendRequests/received/{userId}")
    Call<List<Friendship>> getFriendRequestsReceived(@Path("userId") int userId);

    @DELETE("api/friendship/{userId}/{friendId}")
    Call<Void> deleteFriend(@Path("userId") int userId, @Path("friendId") int friendId);
}
