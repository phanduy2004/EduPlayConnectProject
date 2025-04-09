package com.myjob.real_time_chat_final.api;

import com.myjob.real_time_chat_final.model.Category;
import com.myjob.real_time_chat_final.model.Question;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CategoryService {
    @GET("api/category")
    Call<List<Category>> getAllCategory();
    @GET("api/category/questions")
    Call<List<Question>> getQuestionsByCategory(@Query("categoryId") Long categoryId);
}
