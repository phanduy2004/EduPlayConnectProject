package vn_hcmute.Real_Time_Chat_Final.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn_hcmute.Real_Time_Chat_Final.entity.GameRoomPlayer;
import vn_hcmute.Real_Time_Chat_Final.entity.Topic;
import vn_hcmute.Real_Time_Chat_Final.service.impl.TopicService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/topic")
public class TopicController {

    @Autowired
    private  TopicService topicService;

    @GetMapping("")
    public ResponseEntity<?> getTopics() {

        List<Topic> topics=topicService.getTopics();

        return ResponseEntity.ok(topics);
    }
}
