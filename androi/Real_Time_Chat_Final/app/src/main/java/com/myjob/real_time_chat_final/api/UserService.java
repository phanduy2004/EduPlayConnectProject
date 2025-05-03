package com.myjob.real_time_chat_final.api;

import com.myjob.real_time_chat_final.model.ConversationMember;
import com.myjob.real_time_chat_final.model.User;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {
    @GET("api/users/all")
    Call<List<User>> getAllUsers();
    @GET("api/users/{userId}")
    Call<User> getUserById(@Path("userId") Long userId);
    @POST("api/users/login") // Đường dẫn đến API login
    Call<Map<String, Object>> login(@Body Map<String, String> body);
    @GET("api/users/contacts")
    Call<List<ConversationMember>> getContacts(@Query("user_id") int userId);
    @POST("auth/register")
    Call<Map<String, String>> signUpPostForm(@Body User user);
    @POST("auth/verify-code")
    Call<Map<String, String>> verifyCode(@Body Map<String, String> requestBody);
    @POST("api/users/update/{id}")
    Call<User> updateUser(@Path("id") int userId, @Body User user);
    @Multipart
    @POST("api/users/{userId}/avatar")
    Call<User> uploadAvatar(@Path("userId") long userId, @Part MultipartBody.Part avatar);
    @POST("api/users/{userId}/change-password")
    Call<Map<String, Object>> changePassword(@Path("userId") long userId, @Body Map<String, String> request);

}
