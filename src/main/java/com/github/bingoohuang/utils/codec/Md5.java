package com.github.bingoohuang.utils.codec;

import java.security.MessageDigest;

public class Md5 {
    public static String md5(String info) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(Bytes.bytes(info));
            return Base64.base64(digest, Base64.Format.Standard);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String md5(String info, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Bytes.bytes(salt));
            byte[] digest = md.digest(Bytes.bytes(info));
            return Base64.base64(digest, Base64.Format.Standard);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String md5Hex(String info) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(Bytes.bytes(info));
            return Hex.hex(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String md5Hex(String info, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Bytes.bytes(salt));
            byte[] digest = md.digest(Bytes.bytes(info));
            return Hex.hex(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
