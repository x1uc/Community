package com.example.message.messageQueue;


import com.example.api.dto.SendCommentDTO;
import com.example.common.utils.JsonUtil;
import com.example.message.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentConsumer {
    private final String TOPIC = "Comment-topic";
    private final MessageService messageService;

    @KafkaListener(topics = TOPIC)
    public void receiveComment(String sendCommentDTO) {
        messageService.addMessageComment(JsonUtil.parseObject(sendCommentDTO, SendCommentDTO.class));
    }
}
