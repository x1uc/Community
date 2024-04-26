package com.example.common.utils;

public class UserContext {
    private static final ThreadLocal<Long> tl = new ThreadLocal<>();

    /**
     * @param userId
     *
     */
    public static void setUser(Long userId) {
        tl.set(userId);
    }

    /**
     * 获取当前登录用户信息
     * @return 用户id
     */
    public static Long getUser() {
        return tl.get();
    }
    /**
     * 移除当前登录用户信息
     */
    public static void removeUser() {
        tl.remove();
    }
}
