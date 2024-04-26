package com.example.common.utils;

import cn.hutool.core.util.StrUtil;
import com.example.common.domain.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author daxiong
 * @date 2021/8/16 下午10:15
 */
public class FeignUtil {

    public static <T> T formatClass(R r, Class<T> tClass) {
        String jsonString = getResultJson(r);
        if (StrUtil.isEmpty(jsonString)) {
            return null;
        }
        return JsonUtil.parseObject(jsonString, tClass);
    }

    private static String getResultJson(R r) {
        if (!r.success()) {
            return null;
        }
        return JsonUtil.toJsonString(r.getData());
    }

    public static <T> List<T> formatListClass(R r, Class<T> tClass) {
        String jsonString = getResultJson(r);
        if (StrUtil.isEmpty(jsonString)) {
            return new ArrayList<>();
        }
        return JsonUtil.parseList(jsonString, tClass);
    }
}