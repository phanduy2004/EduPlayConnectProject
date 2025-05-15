package vn_hcmute.Real_Time_Chat_Final.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn_hcmute.Real_Time_Chat_Final.entity.Conversation;
import vn_hcmute.Real_Time_Chat_Final.entity.ConversationMember;
import vn_hcmute.Real_Time_Chat_Final.entity.Message;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import vn_hcmute.Real_Time_Chat_Final.model.ContactDTO;
import vn_hcmute.Real_Time_Chat_Final.repository.ChatMessageRepository;
import vn_hcmute.Real_Time_Chat_Final.repository.ConversationMemberRepository;
import vn_hcmute.Real_Time_Chat_Final.repository.ConversationRepository;
import vn_hcmute.Real_Time_Chat_Final.service.IConversationService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConversationService implements IConversationService {

    private final ConversationRepository conversationRepository;
    private final UserServiceImpl userServiceImpl;
    private final ChatMessageRepository chatMessageRepository;
    @Value("${file.upload-dir}")
    private String uploadDir;
    @Autowired
    public ConversationService(ConversationRepository conversationRepository, UserServiceImpl userServiceImpl,ChatMessageRepository chatMessageRepository) {
        this.conversationRepository = conversationRepository;
        this.userServiceImpl = userServiceImpl;
        this.chatMessageRepository = chatMessageRepository;
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

    @Transactional
    @Override
    public Conversation createConversation(boolean isGroup, String name, String avatarUrl) {
        Conversation conversation = new Conversation();
        conversation.setGroup(true);
        conversation.setName(name);
        conversation.setAvatarUrl(avatarUrl); // Gán avatarUrl
        conversation.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return conversationRepository.save(conversation);
    }

    @Transactional
    public void addMembersToConversation(Long conversationId, List<ConversationMember> members) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        if (conversationOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy cuộc trò chuyện với ID: " + conversationId);
        }

        Conversation conversation = conversationOpt.get();
        if (!conversation.isGroup()) {
            throw new IllegalArgumentException("Cuộc trò chuyện không phải là nhóm");
        }

        if (conversation.getMembers() == null) {
            conversation.setMembers(new HashSet<>());
        }

        for (ConversationMember member : members) {
            member.setConversation(conversation);
            conversation.getMembers().add(member);
        }

        try {
            conversationRepository.save(conversation);
            System.out.println("Thêm " + members.size() + " thành viên vào nhóm: conversationId=" + conversationId);
        } catch (Exception e) {
            System.err.println("Lỗi lưu thành viên: " + e.getMessage());
            throw e;
        }
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
            Conversation newConversation = createConversation(false, "New Chat",null);
            return Optional.of(newConversation.getId());
        }

        return Optional.empty();
    }
    @Override
    public List<ConversationMember> findListChat(int userId) {
        return conversationRepository.findListChat(userId);
    }
    public List<ContactDTO> getContacts(int userId) {
        List<Conversation> conversations = conversationRepository.findAll();

        return conversations.stream()
                .filter(convo -> convo.getMembers().stream().anyMatch(cm -> cm.getUser().getId() == userId))
                .map(conversation -> {
                    Optional<Message> lastMessageOpt = chatMessageRepository.findLastMessageByConversationId(conversation.getId());
                    String lastMessage = lastMessageOpt.map(Message::getMessage).orElse("Chưa có tin nhắn");
                    Timestamp lastMessageTime = lastMessageOpt.map(Message::getTimestamp).orElse(null);
                    String lastMessageSenderName = lastMessageOpt.map(m -> m.getSender().getUsername()).orElse(null);
                    Long lastMessageSenderId = lastMessageOpt.map(m -> m.getSender().getId()).orElse(null);
                    User displayUser = null;
                    String displayAvatarUrl = null;
                    String displayName;

                    if (conversation.isGroup()) {
                        displayName = conversation.getName(); // ✅ lấy tên nhóm
                        // bạn có thể chọn một thành viên làm đại diện avatar nếu muốn
                        displayAvatarUrl = conversation.getAvatarUrl();
                    } else {
                        // ✅ lấy người còn lại (khác với userId)
                        Optional<ConversationMember> otherMemberOpt = conversation.getMembers()
                                .stream()
                                .filter(cm -> cm.getUser().getId() != userId)
                                .findFirst();
                        displayUser = otherMemberOpt.map(ConversationMember::getUser).orElse(null);
                        displayAvatarUrl = displayUser.getAvatarUrl();
                        displayName = displayUser != null ? displayUser.getUsername() : "Không xác định";
                    }

                    return new ContactDTO(
                            displayUser != null ? (int) displayUser.getId() : 0,
                            displayName,
                            displayUser != null ? displayUser.getEmail() : "",
                            displayUser != null && displayUser.isActive(),
                            displayUser != null && displayUser.getCreatedAt() != null ? displayUser.getCreatedAt().toString() : "",
                            displayUser != null && displayUser.isStatus(),
                            (int) conversation.getId(),
                            displayAvatarUrl,
                            lastMessage,
                            lastMessageTime,
                            lastMessageSenderName,
                            lastMessageSenderId

                    );
                }).collect(Collectors.toList());
    }
    @Transactional
    public String uploadAvatarGroup( MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File ảnh không được để trống");
        }

        // Kiểm tra loại tệp
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ được upload tệp ảnh (jpg, png, v.v.)");
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());
        String avatarUrl = "/uploads/" + fileName;
        return avatarUrl;
    }


}