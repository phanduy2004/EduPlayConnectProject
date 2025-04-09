package vn_hcmute.Real_Time_Chat_Final.service;

import org.springframework.data.repository.query.Param;
import vn_hcmute.Real_Time_Chat_Final.entity.Conversation;
import vn_hcmute.Real_Time_Chat_Final.entity.ConversationMember;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IConversationService {
    Conversation createConversation(boolean isGroup, String name);

    Optional<Conversation> getConversationById(Long conversationId);

    Optional<Long> getChatRoomId(Long conversationId, boolean createNewRoomIfNotExists);

    List<ConversationMember> findListChat(int userId);
}
