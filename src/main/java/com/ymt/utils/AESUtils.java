package com.ymt.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;

import java.util.Map;

public class AESUtils {
    private static final byte[] key = new byte[]{1, 65, 90, -58, 127, -11, 71, 110, 20, 123, -93, 34, -85, 123, -40, 74};
    private static final SymmetricCrypto aes;

    static {
        aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
    }

    public static String encrypt(String token) {
        return aes.encryptHex(token, CharsetUtil.CHARSET_UTF_8);

    }

    public static String decrypt(String encrypt) {
        return aes.decryptStr(encrypt, CharsetUtil.CHARSET_UTF_8);
    }

    public static String creatJwt(Map<String, Object> map){
        return JWTUtil.createToken(map, key);
    }

    public static String creatJwt(String token){
        JWT jwt = JWTUtil.parseToken(token);
        String sub = jwt.getPayload("sub").toString();
        return sub;
    }

}
