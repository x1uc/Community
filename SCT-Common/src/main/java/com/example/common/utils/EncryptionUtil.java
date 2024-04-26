package com.example.common.utils;

import cn.hutool.crypto.digest.DigestUtil;

public class EncryptionUtil {
    private static String SALT = "conversion";
    public static String passwordEncryption(String password) {
        password = DigestUtil.md5Hex(password);
        password = DigestUtil.md5Hex(password + SALT);
        return password;
    }
}
