package vn_hcmute.Real_Time_Chat_Final.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn_hcmute.Real_Time_Chat_Final.entity.GameRoom;
import vn_hcmute.Real_Time_Chat_Final.entity.GameRoomPlayer;
import vn_hcmute.Real_Time_Chat_Final.entity.User;

import java.util.List;

public interface GameRoomPlayerRepository extends JpaRepository<GameRoomPlayer, Long> {
    boolean existsByGameRoomAndUser(GameRoom gameRoom, User user);
    long countByGameRoom(GameRoom gameRoom);
    void deleteByGameRoomAndUser(GameRoom gameRoom, User user);
    List<GameRoomPlayer> findByGameRoom(GameRoom gameRoom);
    GameRoomPlayer findByGameRoom_RoomIdAndUser_Id(Long gameRoomId, Long userId);

    GameRoomPlayer findByUserId(long userId);
}
