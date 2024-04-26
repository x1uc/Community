package com.example.message.controller;


import com.example.common.domain.PageDTO;
import com.example.common.domain.R;
import com.example.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/like")
    public R MsgLike(@RequestBody PageDTO pageDTO) {
        return messageService.MsgLike(pageDTO);
    }

    @PostMapping("/comment")
    public R MsgComment(@RequestBody PageDTO pageDTO) {
        return messageService.MsgService(pageDTO);
    }

    @GetMapping("/unReadLike")
    public R unReadLike() {
        return messageService.unReadLike();
    }

    @GetMapping("/unReadComment")
    public R unReadComment() {
        return messageService.unReadComment();
    }


    @GetMapping("/allUnRead")
    public R allUnRead() {
        return messageService.AllUnRead();
    }


}
