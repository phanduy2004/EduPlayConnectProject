    package vn_hcmute.Real_Time_Chat_Final.entity;

    import com.fasterxml.jackson.annotation.JsonBackReference;
    import com.fasterxml.jackson.annotation.JsonIgnore; // Thêm import này
    import jakarta.persistence.*;
    import lombok.*;

    import java.io.Serializable;

    @Entity
    @Table(name = "game_room_players")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class GameRoomPlayer implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Thêm dòng này
        private Long id;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "game_room_id")
        @JsonBackReference
        private GameRoom gameRoom;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "user_id")
        private User user;

        @Column
        private boolean isReady;

        @Override
        public String toString() {
            return "GameRoomPlayer{" +
                    "gameRoom=" + gameRoom +
                    ", id=" + id +
                    ", user=" + user +
                    ", isReady=" + isReady +
                    '}';
        }
    }