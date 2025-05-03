package vn_hcmute.Real_Time_Chat_Final.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn_hcmute.Real_Time_Chat_Final.entity.Topic;
import vn_hcmute.Real_Time_Chat_Final.repository.TopicRepository;

import java.util.List;
@Service
public class TopicService {

    @Autowired
    private TopicRepository topicRepository;

    public List<Topic> getTopics() {
        return topicRepository.findAll();
    }
}
