package com.example.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.message.domain.po.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageMapper extends BaseMapper<Message> {

    List<Message> MsgGet(@Param("userId") Long userId,
                         @Param("limit") Integer limit,
                         @Param("offset") Integer offset,
                         @Param("type") int i);


    void updateStatus(@Param("messageIdList") List<Long> messageIdList);


    Integer messageTotal(@Param("userId") Long userId, @Param("type") int i);

    Integer unReadMessage(@Param("userId") Long userId, @Param("type") int i);

    Long searchMsg(@Param("msgId") Long msgId);

}
