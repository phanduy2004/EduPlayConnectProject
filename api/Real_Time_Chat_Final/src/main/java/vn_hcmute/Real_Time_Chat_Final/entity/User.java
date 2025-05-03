package vn_hcmute.Real_Time_Chat_Final.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private String otp;

    @Column(nullable = false)
    private boolean isActive;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column
    private boolean status;

    @Column
    private String avatarUrl;

    private boolean isReady;

    public User() {

    }

    @Override
    public String toString() {
        return "User{" +
                "createdAt=" + createdAt +
                ", id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", otp='" + otp + '\'' +
                ", isActive=" + isActive +
                ", status=" + status +
                '}';
    }
}