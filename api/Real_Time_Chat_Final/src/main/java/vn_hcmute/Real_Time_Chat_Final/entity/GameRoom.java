package vn_hcmute.Real_Time_Chat_Final.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_rooms")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class GameRoom implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long roomId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;


    @Column(nullable = false)
    private boolean gameStarted;

    @Column(nullable = false)
    private int maxPlayers;

    @OneToMany(mappedBy = "gameRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<GameRoomPlayer> players;

    // Thêm phương thức addPlayer
    public void addPlayer(GameRoomPlayer player) {
        if (players == null) {
            players = new ArrayList<>();
        }
        players.add(player);
        player.setGameRoom(this);
    }
    public GameRoom() {}

    public GameRoom(Category category, User host, boolean gameStarted, int maxPlayers) {
        this.category = category;
        this.host = host;
        this.gameStarted = gameStarted;
        this.maxPlayers = maxPlayers;
    }

    public Long getRoomId() {
        return roomId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public List<GameRoomPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<GameRoomPlayer> players) {
        this.players = players;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
