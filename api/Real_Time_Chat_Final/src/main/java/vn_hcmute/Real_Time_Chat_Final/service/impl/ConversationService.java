package vn_hcmute.Real_Time_Chat_Final.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import vn_hcmute.Real_Time_Chat_Final.entity.Conversation;
import vn_hcmute.Real_Time_Chat_Final.entity.ConversationMember;
import vn_hcmute.Real_Time_Chat_Final.repository.ConversationRepository;
import vn_hcmute.Real_Time_Chat_Final.service.IConversationService;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConversationService implements IConversationService {
    @Autowired
    private final ConversationRepository conversationRepository;

    @Autowired
    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    /**
     * ✅ Tạo cuộc trò chuyện mới
     */
    @Override
    public Conversation createConversation(boolean isGroup, String name) {
        Conversation conversation = new Conversation();
        conversation.setGroup(isGroup);
        conversation.setName(name);
        conversation.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return conversationRepository.save(conversation);
    }

    /**
     * ✅ Lấy thông tin cuộc trò chuyện theo ID
     */
    @Override
    public Optional<Conversation> getConversationById(Long conversationId) {
        return conversationRepository.findById(conversationId);
    }

    /**
     * ✅ Lấy ID của cuộc trò chuyện, nếu chưa tồn tại có thể tạo mới
     */
    @Override
    public Optional<Long> getChatRoomId(Long conversationId, boolean createNewRoomIfNotExists) {
        Optional<Long> existingChatRoomId = conversationRepository.getChatRoomId(conversationId);

        if (existingChatRoomId.isPresent()) {
            return existingChatRoomId;
        } else if (createNewRoomIfNotExists) {
            Conversation newConversation = createConversation(false, "New Chat");
            return Optional.of((long) newConversation.getId());
        }

        return Optional.empty();
    }
    @Override
    public List<ConversationMember> findListChat(int userId){
        return conversationRepository.findListChat(userId);
    }

}
