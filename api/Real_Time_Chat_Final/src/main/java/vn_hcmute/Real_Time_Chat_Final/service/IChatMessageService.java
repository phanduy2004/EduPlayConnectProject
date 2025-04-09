package vn_hcmute.Real_Time_Chat_Final.service;

import vn_hcmute.Real_Time_Chat_Final.entity.Message;

import java.util.List;

public interface IChatMessageService {
    Message save(Message chatMessage);


    List<Message> findChatMessagesByConversationId(int conversationId);


    List<Message> findChatMessagesBySender(int conversationId, int senderId);
}
