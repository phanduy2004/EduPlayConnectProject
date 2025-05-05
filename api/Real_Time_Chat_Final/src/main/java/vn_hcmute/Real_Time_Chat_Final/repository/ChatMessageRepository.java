package vn_hcmute.Real_Time_Chat_Final.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn_hcmute.Real_Time_Chat_Final.entity.Message;

import java.util.List;
@Repository
public interface ChatMessageRepository extends JpaRepository<Message, Long> {
    List<Message> findById(int id);

    List<Message> findByConversationIdAndSenderId(long conversation_id, int sender_id);

    Page<Message> findByConversationId(Long conversationId, Pageable pageable);
}
