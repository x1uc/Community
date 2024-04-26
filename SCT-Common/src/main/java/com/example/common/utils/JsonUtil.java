package com.example.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * @author daxiong
 * @date 2021/8/16 下午10:12
 */
@Slf4j
public class JsonUtil {

    static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T parseObject(String jsonStr, Class<T> tClass) {  
        try {
            return objectMapper.readValue(jsonStr, tClass);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static <T> List<T> parseList(String text, Class<T> clazz) {
        objectMapper.registerModule(new JavaTimeModule());
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
        try {
            return objectMapper.readValue(text, javaType);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * json序列化
     *
     * @param obj 需要序列化的对象
     * @return
     */
    public static String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

}