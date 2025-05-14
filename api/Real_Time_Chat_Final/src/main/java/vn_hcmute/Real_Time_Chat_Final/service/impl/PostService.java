package vn_hcmute.Real_Time_Chat_Final.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn_hcmute.Real_Time_Chat_Final.entity.Post;
import vn_hcmute.Real_Time_Chat_Final.entity.PostMedia;
import vn_hcmute.Real_Time_Chat_Final.entity.User;
import vn_hcmute.Real_Time_Chat_Final.model.PostRequestDTO;
import vn_hcmute.Real_Time_Chat_Final.model.PostResponseDTO;
import vn_hcmute.Real_Time_Chat_Final.model.CommentDTO;
import vn_hcmute.Real_Time_Chat_Final.repository.LikeRepository;
import vn_hcmute.Real_Time_Chat_Final.repository.PostRepository;
import vn_hcmute.Real_Time_Chat_Final.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMediaService postMediaService;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public PostResponseDTO createPost(PostRequestDTO request) throws IOException {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        Post post = Post.builder()
                .user(user)
                .content(request.getContent())
                .privacy(Post.Privacy.valueOf(String.valueOf(request.getPrivacy())))
                .postMediaList(new ArrayList<>())
                .build();
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                String imageUrl = request.getImageUrls().get(i);
                PostMedia postMedia = PostMedia.builder()
                        .post(post)
                        .mediaUrl(imageUrl)
                        .mediaType(PostMedia.MediaType.IMAGE)
                        .displayOrder(i + 1)
                        .build();
                post.addPostMedia(postMedia);
            }
        }

        Post savedPost = postRepository.save(post);
        messagingTemplate.convertAndSend("/topic/posts", "New post created: " + savedPost.getId());
        return convertToDTO(savedPost, request.getUserId());
    }

    public List<PostResponseDTO> getPosts(int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        return postPage.getContent().stream()
                .map(post -> convertToDTO(post, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<String> uploadImages(List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Files cannot be null or empty");
        }

        return files.stream()
                .map(file -> {
                    try {
                        if (file == null || file.isEmpty()) {
                            throw new IllegalArgumentException("File cannot be empty");
                        }
                        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                        Path filePath = Paths.get(uploadDir, fileName);
                        Files.createDirectories(filePath.getParent());
                        Files.write(filePath, file.getBytes());
                        return "/uploads/" + fileName;
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
                    }
                })
                .collect(Collectors.toList());
    }

    private PostResponseDTO convertToDTO(Post post, Long userId) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setId(post.getId());
        dto.setUserId(post.getUser().getId());
        dto.setUsername(post.getUser().getUsername());
        dto.setAvatarUrl(post.getUser().getAvatarUrl());
        dto.setContent(post.getContent());
        dto.setLikeCount((int) likeRepository.countByPostId(post.getId()));
        dto.setLikedByUser(likeRepository.existsByPostIdAndUserId(post.getId(), userId));

        if (post.getPostMediaList() != null && !post.getPostMediaList().isEmpty()) {
            List<String> imageUrls = post.getPostMediaList().stream()
                    .filter(media -> media.getMediaType() == PostMedia.MediaType.IMAGE)
                    .sorted((m1, m2) -> {
                        Integer order1 = m1.getDisplayOrder() != null ? m1.getDisplayOrder() : Integer.MAX_VALUE;
                        Integer order2 = m2.getDisplayOrder() != null ? m2.getDisplayOrder() : Integer.MAX_VALUE;
                        return order1.compareTo(order2);
                    })
                    .map(PostMedia::getMediaUrl)
                    .collect(Collectors.toList());
            dto.setImageUrl(imageUrls.isEmpty() ? null : imageUrls);
        }

        dto.setPrivacy(post.getPrivacy().toString());
        dto.setCreatedAt(post.getCreatedAt());

        if (post.getComments() != null) {
            dto.setComments(post.getComments().stream()
                    .map(comment -> {
                        CommentDTO commentDTO = new CommentDTO();
                        commentDTO.setId(comment.getId());
                        commentDTO.setUserId(comment.getUser().getId());
                        commentDTO.setPostId(comment.getPost().getId());
                        commentDTO.setAvatarUrl(comment.getUser().getAvatarUrl());
                        commentDTO.setUsername(comment.getUser().getUsername());
                        commentDTO.setContent(comment.getContent());
                        commentDTO.setCreatedAt(comment.getCreatedAt());
                        commentDTO.setParentCommentId(comment.getParentComment() != null ?
                                comment.getParentComment().getId() : null);
                        if (comment.getReplies() != null) {
                            commentDTO.setReplies(comment.getReplies().stream()
                                    .map(reply -> {
                                        CommentDTO replyDTO = new CommentDTO();
                                        replyDTO.setId(reply.getId());
                                        replyDTO.setUserId(reply.getUser().getId());
                                        replyDTO.setUsername(reply.getUser().getUsername());
                                        replyDTO.setAvatarUrl(reply.getUser().getAvatarUrl());
                                        replyDTO.setContent(reply.getContent());
                                        replyDTO.setCreatedAt(reply.getCreatedAt());
                                        replyDTO.setParentCommentId(reply.getParentComment() != null ?
                                                reply.getParentComment().getId() : null);
                                        return replyDTO;
                                    })
                                    .collect(Collectors.toList()));
                        }
                        return commentDTO;
                    })
                    .collect(Collectors.toList()));
        }

        dto.setScore(0.0);
        return dto;
    }
}