package com.github.bingoohuang.utils.crypto;

import com.github.bingoohuang.utils.codec.Bytes;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class Aes {
    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * 生成AES加密密钥。
     *
     * @return AES密钥。
     */
    public static String generateAesSecret() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128);
            SecretKey secretKey = kg.generateKey();
            return Base64.getUrlEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Key getKey(String key) {
        return new SecretKeySpec(Base64.getUrlDecoder().decode(key), KEY_ALGORITHM);
    }

    @SneakyThrows
    public static String decrypt(String value, Key secret) {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secret);
        byte[] decrypted = cipher.doFinal(Base64.getUrlDecoder().decode(value));
        return Bytes.string(decrypted);
    }

    @SneakyThrows
    public static String encrypt(String value, Key secret) {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] encrypted = cipher.doFinal(Bytes.bytes(value));
        return Base64.getUrlEncoder().encodeToString(encrypted);
    }


    /**
     * AES加密。
     *
     * @param value  需要加密的字符串。
     * @param secret 加密密钥。
     * @return 加密后BASE64 URL SAFE编码。
     */
    @SneakyThrows
    public static String encrypt(String value, String secret) {
        return encrypt(value, getKey(secret));
    }

    /**
     * AES 解密。
     *
     * @param value  需要解密的字符串。
     * @param secret 解密密钥。
     * @return 解密后的明文。
     */
    @SneakyThrows
    public static String decrypt(String value, String secret) {
        return decrypt(value, getKey(secret));
    }
}
