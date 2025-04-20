package com.myjob.real_time_chat_final.api;

import com.myjob.real_time_chat_final.model.WordResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DictionaryApi {

    @GET("https://api.dictionaryapi.dev/api/v2/entries/en/{word}")
    Call<List<WordResult>> getMeaning(@Path("word") String word);

}
