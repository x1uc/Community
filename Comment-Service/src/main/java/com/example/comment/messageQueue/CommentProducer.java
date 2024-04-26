package com.example.comment.messageQueue;


import com.example.api.dto.SendCommentDTO;
import com.example.comment.service.CommentService;
import com.example.common.utils.JsonUtil;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String TOPIC = "Comment-topic";

    public void sendComment(SendCommentDTO sendCommentDTO) {
        kafkaTemplate.send(TOPIC, JsonUtil.toJsonString(sendCommentDTO));
    }
}
