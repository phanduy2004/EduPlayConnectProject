package vn_hcmute.Real_Time_Chat_Final.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn_hcmute.Real_Time_Chat_Final.model.ImageResponse;
import vn_hcmute.Real_Time_Chat_Final.model.PostRequestDTO;
import vn_hcmute.Real_Time_Chat_Final.model.PostResponseDTO;
import vn_hcmute.Real_Time_Chat_Final.service.impl.PostService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);
    private final PostService postService;

    @PostMapping("/posts/create")
    public ResponseEntity<?> createPost(@RequestBody PostRequestDTO request) {
        try {
            PostResponseDTO postResponse = postService.createPost(request);
            return ResponseEntity.ok(postResponse);
        } catch (RuntimeException e) {
            log.error("Error creating post: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            log.error("Server error creating post: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/posts")
    public List<PostResponseDTO> getPosts(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("userId") Long userId) {
        log.info("Fetching posts for userId: {}, page: {}, size: {}", userId, page, size);
        return postService.getPosts(page, size, userId);
    }

    @PostMapping("/posts/upload")
    public ResponseEntity<List<ImageResponse>> uploadImages(@RequestParam("file") List<MultipartFile> files) {
        try {
            log.info("Received upload request with {} files", files.size());
            List<String> imageUrls = postService.uploadImages(files);
            List<ImageResponse> responses = imageUrls.stream()
                    .map(url -> {
                        ImageResponse response = new ImageResponse();
                        response.setImageUrl(url);
                        return response;
                    })
                    .collect(Collectors.toList());
            log.info("Successfully uploaded {} images", responses.size());
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            log.error("Invalid file: " + e.getMessage(), e);
            return ResponseEntity.badRequest().body(List.of(new ImageResponse("Error: " + e.getMessage())));
        } catch (Exception e) {
            log.error("Error uploading images: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of(new ImageResponse("Error: " + e.getMessage())));
        }
    }
}