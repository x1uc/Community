package com.example.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.api.dto.LikeDTO;
import com.example.api.dto.SendCommentDTO;
import com.example.common.domain.PageDTO;
import com.example.common.domain.R;
import com.example.message.domain.po.Message;

public interface MessageService extends IService<Message> {
    R MsgLike(PageDTO pageDTO);

    R MsgService(PageDTO pageDTO);

    R unReadLike();


    R unReadComment();

    void addMessageLike(LikeDTO likeDetail);

    void addMessageComment(SendCommentDTO sendCommentDTO);

    R AllUnRead();
}
