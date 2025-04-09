package vn_hcmute.Real_Time_Chat_Final.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn_hcmute.Real_Time_Chat_Final.entity.GameRoom;

import java.util.Optional;

public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {
    @Query("SELECT g FROM GameRoom g JOIN FETCH g.category c JOIN FETCH c.questions WHERE g.roomId = :roomId")
    GameRoom findByIdWithCategoryAndQuestions(@Param("roomId") Long roomId);
    @Query("SELECT gr FROM GameRoom gr JOIN FETCH gr.host WHERE gr.roomId = :roomId")
    Optional<GameRoom> findByIdWithHost(Long roomId);


}
