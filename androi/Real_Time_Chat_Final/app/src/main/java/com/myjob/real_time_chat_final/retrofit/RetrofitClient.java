package com.myjob.real_time_chat_final.retrofit;

import com.myjob.real_time_chat_final.api.CategoryService;
import com.myjob.real_time_chat_final.api.CommentService;
import com.myjob.real_time_chat_final.api.ConversationService;
import com.myjob.real_time_chat_final.api.DictionaryApi;
import com.myjob.real_time_chat_final.api.FriendshipService;
import com.myjob.real_time_chat_final.api.GameRoomService;
import com.myjob.real_time_chat_final.api.MessageService;
import com.myjob.real_time_chat_final.api.ScoreService;
import com.myjob.real_time_chat_final.api.TopicService;
import com.myjob.real_time_chat_final.api.PostService;
import com.myjob.real_time_chat_final.api.UserService;
import com.myjob.real_time_chat_final.modelDTO.ScoreDTO;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    // Dùng địa chỉ IP của máy tính chạy server trên mạng cục bộ
    private static final String BASE_URL = "http://10.0.2.2:8686/";
    private static final String BASE_URL1 = "http://10.0.2.2:8686"; // Loại bỏ dấu / cuối cho thống nhất

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Các phương thức khác giữ nguyên
    public static UserService getApiUserService() {
        return getInstance().create(UserService.class);
    }
    public static MessageService getApiMessageService() {
        return getInstance().create(MessageService.class);
    }
    public static CategoryService getApiCategoryService() {
        return getInstance().create(CategoryService.class);
    }
    public static FriendshipService getApiFriendshipService() {
        return getInstance().create(FriendshipService.class);
    }
    public static GameRoomService getApiGameRoomService() {
        return getInstance().create(GameRoomService.class);
    }
    public static ConversationService getApiConversationService() {
        return getInstance().create(ConversationService.class);
    }
    public static String getBaseUrl() {
        return BASE_URL1;
    }
    public static DictionaryApi getApiDictionaryService() {
        return getInstance().create(DictionaryApi.class);
    }

    public static TopicService getApiTopicService(){
        return getInstance().create(TopicService.class);
    }

    public static PostService getApiPostService() {
        return getInstance().create(PostService.class);
    }
    public static CommentService getApiCommentService() {
        return getInstance().create(CommentService.class);
    }
    public static ScoreService getApiScoreService() {
        return getInstance().create(ScoreService.class);
    }
}