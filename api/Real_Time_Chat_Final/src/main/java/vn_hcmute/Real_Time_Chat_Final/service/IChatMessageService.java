package vn_hcmute.Real_Time_Chat_Final.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn_hcmute.Real_Time_Chat_Final.entity.Message;

import java.util.List;

public interface IChatMessageService {
    Message save(Message chatMessage);


    Page<Message> findChatMessagesByConversationId(int conversationId, Pageable pageable);


    List<Message> findChatMessagesBySender(int conversationId, int senderId);
}
