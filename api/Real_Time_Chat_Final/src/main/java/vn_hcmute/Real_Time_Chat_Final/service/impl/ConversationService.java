package vn_hcmute.Real_Time_Chat_Final.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn_hcmute.Real_Time_Chat_Final.entity.Conversation;
import vn_hcmute.Real_Time_Chat_Final.entity.ConversationMember;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import vn_hcmute.Real_Time_Chat_Final.repository.ConversationRepository;
import vn_hcmute.Real_Time_Chat_Final.service.IConversationService;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationService implements IConversationService {

    private final ConversationRepository conversationRepository;
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public ConversationService(ConversationRepository conversationRepository, UserServiceImpl userServiceImpl) {
        this.conversationRepository = conversationRepository;
        this.userServiceImpl = userServiceImpl;
    }

    @Transactional
    public Conversation createOrGetConversation(int userIdInt, int friendIdInt) {
        long userId = userIdInt;
        long friendId = friendIdInt;

        if (userId == friendId) {
            throw new IllegalArgumentException("userId và friendId không được trùng");
        }

        System.out.println("Kiểm tra cuộc trò chuyện: userId=" + userId + ", friendId=" + friendId);

        List<Conversation> existingConversations = conversationRepository.findIndividualConversationBetweenUsers(userId, friendId);
        System.out.println("Tìm thấy " + existingConversations.size() + " cuộc trò chuyện hiện có");
        if (!existingConversations.isEmpty()) {
            System.out.println("Tái sử dụng conversationId: " + existingConversations.get(0).getId());
            return existingConversations.get(0);
        }

        Conversation conversation = new Conversation();
        conversation.setGroup(false);
        conversation.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // Kiểm tra members
        if (conversation.getMembers() == null) {
            conversation.setMembers(new HashSet<>());
            System.out.println("Khởi tạo members mới cho conversation");
        }

        try {
            conversation = conversationRepository.save(conversation);
            System.out.println("Tạo conversation mới: id=" + conversation.getId());
        } catch (Exception e) {
            System.err.println("Lỗi lưu conversation: " + e.getMessage());
            throw e;
        }

        Optional<User> user1Opt = userServiceImpl.findByUserId(userId);
        Optional<User> user2Opt = userServiceImpl.findByUserId(friendId);

        if (user1Opt.isEmpty() || user2Opt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy user: " + (user1Opt.isEmpty() ? userId : friendId));
        }

        User user1 = user1Opt.get();
        User user2 = user2Opt.get();

        ConversationMember member1 = new ConversationMember();
        member1.setConversation(conversation);
        member1.setUser(user1);

        ConversationMember member2 = new ConversationMember();
        member2.setConversation(conversation);
        member2.setUser(user2);

        // Thêm thành viên
        try {
            conversation.getMembers().add(member1);
            conversation.getMembers().add(member2);
            System.out.println("Thêm members thành công: size=" + conversation.getMembers().size());
        } catch (Exception e) {
            System.err.println("Lỗi thêm members: " + e.getMessage());
            throw e;
        }

        try {
            conversation = conversationRepository.save(conversation);
            System.out.println("Thêm thành viên: userId=" + userId + ", friendId=" + friendId);
        } catch (Exception e) {
            System.err.println("Lỗi lưu members: " + e.getMessage());
            throw e;
        }

        return conversation;
    }

    @Override
    public Conversation createConversation(boolean isGroup, String name) {
        Conversation conversation = new Conversation();
        conversation.setGroup(isGroup);
        conversation.setName(name);
        conversation.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return conversationRepository.save(conversation);
    }

    @Override
    public Optional<Conversation> getConversationById(Long conversationId) {
        return conversationRepository.findById(conversationId);
    }

    @Override
    public Optional<Long> getChatRoomId(Long conversationId, boolean createNewRoomIfNotExists) {
        Optional<Long> existingChatRoomId = conversationRepository.getChatRoomId(conversationId);

        if (existingChatRoomId.isPresent()) {
            return existingChatRoomId;
        } else if (createNewRoomIfNotExists) {
            Conversation newConversation = createConversation(false, "New Chat");
            return Optional.of(newConversation.getId());
        }

        return Optional.empty();
    }

    @Override
    public List<ConversationMember> findListChat(int userId) {
        return conversationRepository.findListChat(userId);
    }
}