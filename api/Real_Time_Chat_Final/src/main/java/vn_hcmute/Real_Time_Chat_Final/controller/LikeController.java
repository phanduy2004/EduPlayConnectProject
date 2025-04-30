package vn_hcmute.Real_Time_Chat_Final.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn_hcmute.Real_Time_Chat_Final.service.impl.LikeService;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/post/{postId}/user/{userId}")
    public void likePost(@PathVariable Long postId, @PathVariable Long userId) {
        likeService.likePost(postId, userId);
    }
}