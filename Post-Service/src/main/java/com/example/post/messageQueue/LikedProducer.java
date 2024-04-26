package com.example.post.messageQueue;


import com.example.api.dto.LikeDTO;
import com.example.common.utils.JsonUtil;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@AllArgsConstructor
public class LikedProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String TOPIC = "topic-like";

    public void sendLikeMessage(LikeDTO likeDetail) {
        kafkaTemplate.send(TOPIC, JsonUtil.toJsonString(likeDetail));
    }
}
