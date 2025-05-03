package vn_hcmute.Real_Time_Chat_Final.service.impl;

import org.springframework.stereotype.Service;
import vn_hcmute.Real_Time_Chat_Final.entity.Post;
import vn_hcmute.Real_Time_Chat_Final.entity.PostMedia;
import vn_hcmute.Real_Time_Chat_Final.repository.PostMediaRepository;

@Service
public class PostMediaService {

    private final PostMediaRepository postMediaRepository;

    public PostMediaService(PostMediaRepository postMediaRepository) {
        this.postMediaRepository = postMediaRepository;
    }

    public void saveMedia(Post post, String mediaUrl, PostMedia.MediaType mediaType, int displayOrder) {
        PostMedia media = PostMedia.builder()
                .post(post)
                .mediaUrl(mediaUrl)
                .mediaType(mediaType)
                .displayOrder(displayOrder)
                .build();
        post.addPostMedia(media); // Duy trì mối quan hệ hai chiều
        postMediaRepository.save(media);
    }
}