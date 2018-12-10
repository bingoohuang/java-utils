package com.github.bingoohuang.utils.codec;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public enum Digest {

    MD5 {
        @Override
        protected String digestAlgorithm() {
            return "MD5";
        }
    },
    SHA1 {
        @Override
        protected String digestAlgorithm() {
            return "SHA-1";
        }
    };

    protected abstract String digestAlgorithm();

    public byte[] digest(String info) {
        try {
            MessageDigest md = MessageDigest.getInstance(digestAlgorithm());
            return md.digest(Bytes.bytes(info));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] digest(String info, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(digestAlgorithm());
            md.update(Bytes.bytes(salt));
            return md.digest(Bytes.bytes(info));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String digestBase64(String info) {
        return Base64.base64(digest(info), Base64.Format.Standard);
    }

    public String digestBase64(String info, String salt) {
        return Base64.base64(digest(info), Base64.Format.Standard);
    }

    public String digestHex(String info) {
        return Hex.hex(digest(info));
    }

    public String digestHex(String info, String salt) {
        return Hex.hex(digest(info));
    }

}
