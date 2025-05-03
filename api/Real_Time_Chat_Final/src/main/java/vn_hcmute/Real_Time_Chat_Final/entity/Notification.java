package vn_hcmute.Real_Time_Chat_Final.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import vn_hcmute.Real_Time_Chat_Final.entity.Post;
import vn_hcmute.Real_Time_Chat_Final.entity.User;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Notification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_notification_user"))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user; // Người nhận thông báo

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", foreignKey = @ForeignKey(name = "FK_notification_actor"))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User actor; // Người thực hiện hành động (ví dụ: người like, comment)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(name = "FK_notification_post"))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Post post; // Bài viết liên quan (nếu có)

    @Column(nullable = false)
    private String type; // LIKE, COMMENT, REPLY

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // Nội dung thông báo, ví dụ: "User1 liked your post"

    @Column
    private String userAvatarUrl; // Avatar của actor

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }
}
