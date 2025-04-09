package com.myjob.real_time_chat_final.api;

import com.myjob.real_time_chat_final.model.GameRoom;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GameRoomService {
    @POST("/api/rooms/create")
    Call<GameRoom> createRoom(@Body GameRoom newRoom);

    @GET("/api/rooms/{roomId}")
    Call<GameRoom> getRoomDetails(@Path("roomId") Long roomId);

    @GET("/api/rooms/exists/{roomId}")
    Call<Boolean> roomExists(@Path("roomId") String roomId);

    @GET("/api/rooms")
    Call<List<GameRoom>> getAvailableRooms();
}
