package vn_hcmute.Real_Time_Chat_Final.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn_hcmute.Real_Time_Chat_Final.entity.Notification;
import vn_hcmute.Real_Time_Chat_Final.entity.Post;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import vn_hcmute.Real_Time_Chat_Final.model.NotificationDTO;
import vn_hcmute.Real_Time_Chat_Final.repository.NotificationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public List<NotificationDTO> getNotifications(Long userId, int page, int size) {
        Page<Notification> notifications = notificationRepository.findByUserId(userId, PageRequest.of(page, size));
        return notifications.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    public NotificationDTO createNotification(NotificationDTO dto) {
        Notification notification = toEntity(dto);
        notification = notificationRepository.save(notification);
        NotificationDTO savedDto = toDTO(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + dto.getUserId(), savedDto);
        return savedDto;
    }

    private NotificationDTO toDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUser().getId());
        dto.setActorId(notification.getActor() != null ? notification.getActor().getId() : null);
        dto.setPostId(notification.getPost() != null ? notification.getPost().getId() : null);
        dto.setType(notification.getType());
        dto.setContent(notification.getContent());
        dto.setUserAvatarUrl(notification.getUserAvatarUrl());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    private Notification toEntity(NotificationDTO dto) {
        Notification notification = new Notification();
        notification.setType(dto.getType());
        notification.setContent(dto.getContent());
        notification.setUserAvatarUrl(dto.getUserAvatarUrl());
        notification.setRead(dto.isRead());
        // Set User, Actor, Post from repositories
        User user = new User();
        user.setId(dto.getUserId());
        notification.setUser(user);
        if (dto.getActorId() != null) {
            User actor = new User();
            actor.setId(dto.getActorId());
            notification.setActor(actor);
        }
        if (dto.getPostId() != null) {
            Post post = new Post();
            post.setId(dto.getPostId());
            notification.setPost(post);
        }
        return notification;
    }
}