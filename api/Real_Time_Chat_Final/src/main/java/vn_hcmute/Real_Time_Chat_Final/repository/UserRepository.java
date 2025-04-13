package vn_hcmute.Real_Time_Chat_Final.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    @Query(value = """
    SELECT DISTINCT c.id as conversation_id, c.name as conversation_name,u.id as user_id, u.username as username, u.email as email FROM user u
    JOIN conversation_members cm ON u.id = cm.user_id
    JOIN messages m ON cm.conversation_id = m.conversation_id
    WHERE cm.conversation_id IN (
        SELECT cm2.conversation_id FROM conversation_members cm2 WHERE cm2.user_id = :userId
    ) AND u.id != :userId
    """, nativeQuery = true)
    List<Map<String, Object>> findChatContacts(@Param("userId") int userId);

    List<User> findAllByStatus(boolean status);

}
