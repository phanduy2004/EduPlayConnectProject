package vn_hcmute.Real_Time_Chat_Final.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn_hcmute.Real_Time_Chat_Final.entity.Message;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<Message, Long> {
    List<Message> findById(int id);

    List<Message> findByConversationIdAndSenderId(long conversation_id, int sender_id);

    Page<Message> findByConversationId(Long conversationId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.timestamp DESC LIMIT 1")
    Optional<Message> findLastMessageByConversationId(@Param("conversationId") Long conversationId);
}
