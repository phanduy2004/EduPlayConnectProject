package vn_hcmute.Real_Time_Chat_Final.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // Thêm import này
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import vn_hcmute.Real_Time_Chat_Final.config.Status;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    private Status status;

 ;
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