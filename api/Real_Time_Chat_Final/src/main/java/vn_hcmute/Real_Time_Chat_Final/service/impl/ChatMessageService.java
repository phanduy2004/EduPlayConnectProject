package vn_hcmute.Real_Time_Chat_Final.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn_hcmute.Real_Time_Chat_Final.entity.Conversation;
import vn_hcmute.Real_Time_Chat_Final.entity.Message;
import vn_hcmute.Real_Time_Chat_Final.repository.ChatMessageRepository;
import vn_hcmute.Real_Time_Chat_Final.repository.ConversationRepository;
import vn_hcmute.Real_Time_Chat_Final.service.IChatMessageService;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ChatMessageService implements IChatMessageService {
    @Autowired
    private  ChatMessageRepository chatMessageRepository;
    @Autowired
    private ConversationRepository conversationRepository;

    @Override
    public Message save(Message chatMessage) {
        Conversation conversation = conversationRepository.findById((long) chatMessage.getConversation().getId())
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        chatMessage.setConversation(conversation);
        return chatMessageRepository.save(chatMessage);
    }

    @Override
    public Page<Message> findChatMessagesByConversationId(int conversationId, Pageable pageable) {
        return chatMessageRepository.findByConversationId((long) conversationId, pageable);
    }
    @Override
    public List<Message> findChatMessagesBySender(int conversationId, int senderId) {
        return chatMessageRepository.findByConversationIdAndSenderId(conversationId, senderId);
    }

}
