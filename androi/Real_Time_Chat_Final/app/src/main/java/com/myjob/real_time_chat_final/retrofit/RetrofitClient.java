package com.myjob.real_time_chat_final.retrofit;

import com.myjob.real_time_chat_final.api.CategoryService;
import com.myjob.real_time_chat_final.api.ConversationService;
import com.myjob.real_time_chat_final.api.FriendshipService;
import com.myjob.real_time_chat_final.api.GameRoomService;
import com.myjob.real_time_chat_final.api.MessageService;
import com.myjob.real_time_chat_final.api.UserService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://10.0.2.2:8686/";
    private static final String BASE_URL1 = "http://10.0.2.2:8686";

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Hàm này giúp lấy ApiService
    public static UserService getApiUserService() {
        return getInstance().create(UserService.class);
    }
    public static MessageService getApiMessageService() {
        return getInstance().create(MessageService.class);
    }
    public static CategoryService getApiCategoryService(){
        return getInstance().create(CategoryService.class);
    }
    public static FriendshipService getApiFriendshipService(){
        return getInstance().create(FriendshipService.class);
    }
    public static GameRoomService getApiGameRoomService(){
        return getInstance().create(GameRoomService.class);
    }
    public static ConversationService getApiConversationService(){
        return getInstance().create(ConversationService.class);
    }
    public static String getBaseUrl() {
        return BASE_URL1;
    }
}
