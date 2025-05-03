package vn_hcmute.Real_Time_Chat_Final.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import vn_hcmute.Real_Time_Chat_Final.service.impl.LikeService;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping(value = "/post/{postId}/user/{userId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String likePost(@PathVariable Long postId, @PathVariable Long userId) {
        return likeService.likePost(postId, userId);
    }
}