package com.example.message.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.clients.PostClient;
import com.example.api.clients.UserClient;
import com.example.api.dto.LikeDTO;
import com.example.api.dto.SendCommentDTO;
import com.example.common.domain.PageDTO;
import com.example.common.domain.R;
import com.example.common.utils.UserContext;
import com.example.message.domain.po.Message;
import com.example.message.domain.vo.MessageDetail;
import com.example.message.domain.vo.MessageVo;
import com.example.message.mapper.MessageMapper;
import com.example.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageMapperImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    private final MessageMapper messageMapper;
    private final UserClient userClient;
    private final PostClient postClient;

    @Override
    public R MsgLike(PageDTO pageDTO) {
        /**
         * 计算偏移参数
         */
        Long userId = UserContext.getUser();
        Integer Limit = pageDTO.getPageSize();
        Integer Offset = (pageDTO.getCurrentPage() - 1) * Limit;
        /**
         *  计算点赞总数
         */
        Integer count = messageMapper.messageTotal(userId, 1);

        /**
         * 获取评论，改变状态，封装
         */
        List<Message> likeList = messageMapper.MsgGet(userId, Limit, Offset, 1);
        List<Long> messageIdList = new ArrayList<>();
        List<MessageDetail> messageDetails = likeList.stream().map(item -> {
            Long messageId = item.getId();
            if (item.getStatus().equals(0)) messageIdList.add(messageId);

            R r = userClient.transIdToUser(item.getFromId());
            LinkedHashMap data = (LinkedHashMap) r.getData();
            String FromUserName = (String) data.get("userName");

            r = userClient.transIdToUser(item.getToId());
            data = (LinkedHashMap) r.getData();
            String ToUserName = (String) data.get("userName");

            r = postClient.transIdToTitle(item.getEntityId());
            String title = (String) r.getData();


            MessageDetail messageDetail = new MessageDetail();
            messageDetail.setFromUserName(FromUserName);
            messageDetail.setToUserName(ToUserName);
            messageDetail.setContent(item.getContent());
            messageDetail.setTitle(title);
            messageDetail.setDateTime(item.getCreateTime());
            messageDetail.setEntityId(item.getEntityId());

            return messageDetail;

        }).collect(Collectors.toList());


        messageMapper.updateStatus(messageIdList);
        MessageVo messageVo = new MessageVo();
        messageVo.setMessageList(messageDetails);
        messageVo.setCount(count);

        return R.ok(messageVo);
    }

    @Override
    public R MsgService(PageDTO pageDTO) {
        /**
         * 计算偏移参数
         */
        Long userId = UserContext.getUser();
        Integer Limit = pageDTO.getPageSize();
        Integer Offset = (pageDTO.getCurrentPage() - 1) * Limit;
        /**
         *  计算点赞总数
         */
        Integer count = messageMapper.messageTotal(userId, 2);
        /**
         * 获取评论，改变状态，封装
         */
        List<Message> commentList = messageMapper.MsgGet(userId, Limit, Offset, 2); // to_userId = userId
        List<Long> messageIdList = new ArrayList<>();

        List<MessageDetail> commentDetails = commentList.stream().map(item -> {
            /**
             * 收集查询到的数据中未读的
             */
            Long commentId = item.getId();
            if (item.getStatus().equals(0)) {
                messageIdList.add(commentId);
            }
            R r = userClient.transIdToUser(item.getFromId());
            LinkedHashMap data = (LinkedHashMap) r.getData();
            String FromUserName = (String) data.get("userName");

            r = userClient.transIdToUser(item.getToId());
            data = (LinkedHashMap) r.getData();
            String ToUserName = (String) data.get("userName");

            r = postClient.transIdToTitle(item.getEntityId());
            String title = (String) r.getData();


            MessageDetail messageDetail = new MessageDetail();
            messageDetail.setDateTime(item.getCreateTime());
            messageDetail.setFromUserName(FromUserName);
            messageDetail.setToUserName(ToUserName);
            messageDetail.setTitle(title);
            messageDetail.setContent(item.getContent());
            messageDetail.setEntityId(item.getEntityId());


            return messageDetail;
        }).collect(Collectors.toList());

        messageMapper.updateStatus(messageIdList);

        MessageVo messageVo = new MessageVo();

        messageVo.setMessageList(commentDetails);
        messageVo.setCount(count);
        return R.ok(messageVo);
    }

    @Override
    public R unReadLike() {
        Long userId = UserContext.getUser();
        if (userId == null) return R.ok(0);
        Integer count = messageMapper.unReadMessage(userId, 1);
        return R.ok(count);
    }

    @Override
    public R unReadComment() {
        Long userId = UserContext.getUser();
        if (userId == null) return R.ok(0);
        Integer count = messageMapper.unReadMessage(userId, 2);
        return R.ok(count);
    }

    @Override
    public void addMessageLike(LikeDTO likeDetail) {
        Message message = new Message();
        message.setContent("点赞");
        //todo 增加防止重复消费
        message.setToId(likeDetail.getToId());
        message.setFromId(likeDetail.getFromId());
        message.setEntityId(likeDetail.getPostId());
        message.setType(1);
        message.setCreateTime(new Date());

        this.save(message);
    }

    @Override
    public void addMessageComment(SendCommentDTO sendCommentDTO) {
        Message message = new Message();

        message.setToId(sendCommentDTO.getToId());
        message.setContent(sendCommentDTO.getContent());
        message.setFromId(sendCommentDTO.getFromId());
        message.setEntityId(sendCommentDTO.getPostId());
        message.setCreateTime(new Date());
        message.setType(2);


        this.save(message);
    }

    @Override
    public R AllUnRead() {
        Long userId = UserContext.getUser();
        Integer count1 = messageMapper.unReadMessage(userId, 1);
        Integer count2 = messageMapper.unReadMessage(userId, 2);
        return R.ok(count1 + count2);
    }


}
