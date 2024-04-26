package com.example.message.messageQueue;


import com.example.api.dto.LikeDTO;
import com.example.common.utils.JsonUtil;
import com.example.message.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LikeConsumer {
    private final String TOPIC = "topic-like";
    private final MessageService messageService;

    @KafkaListener(topics = TOPIC)
    public void likeConsumer(String likeDetail) {
        messageService.addMessageLike(JsonUtil.parseObject(likeDetail, LikeDTO.class));
    }


}
