package com.github.bingoohuang.utils.crypto;

import com.github.bingoohuang.utils.codec.Base64;
import com.github.bingoohuang.utils.codec.Bytes;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;

public class AesIV {
    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    public static String encrypt(String value, String key) {
        String iv = randomIV();
        String encrypt = encrypt(value, key, iv);
        return iv + ":" + encrypt;
    }

    public static String decrypt(String value, String key) {
        String iv = StringUtils.substringBefore(value, ":");
        String content = StringUtils.substringAfter(value, ":");
        return decrypt(content, key, iv);
    }


    public static String encrypt(String value, String key, String iv) {
        SecretKeySpec skeySpec = new SecretKeySpec(Base64.unBase64(key), KEY_ALGORITHM);
        return encrypt(value, skeySpec, iv);
    }

    public static String decrypt(String value, String key, String iv) {
        SecretKeySpec skeySpec = new SecretKeySpec(Base64.unBase64(key), KEY_ALGORITHM);
        return decrypt(value, skeySpec, iv);
    }

    public static String randomIV() {
        // build the initialization vector (randomly).
        byte iv[] = new byte[16]; // generate random 16 byte IV AES is always 16bytes
        new SecureRandom().nextBytes(iv);
        return Base64.base64(iv);
    }

    @SneakyThrows
    public static String decrypt(String value, Key key, String iv) {
        val ivParameterSpec = new IvParameterSpec(Base64.unBase64(iv));
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        byte[] decrypted = cipher.doFinal(Base64.unBase64(value));
        return Bytes.string(decrypted);

    }

    @SneakyThrows
    public static String encrypt(String value, Key key, String iv) {
        val ivParameterSpec = new IvParameterSpec(Base64.unBase64(iv));
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(Bytes.bytes(value));
        return Base64.base64(encrypted);
    }

    public static void main(String[] args) {
        // System.out.println(Aes.generateKey());
        String key = "uzTnFINhcCOYmzwdI9VkXA";
        System.out.println(encrypt("hello world, it's me, bingoo huang", key));
        System.out.println(encrypt("hello world, it's me, holly wolf", key));

        System.out.println(decrypt("Bv-RMDpEL-h9C9tYSDBavA:KZpF4UjfrHjxQ_321-P59Yn46ATCJ54hyf_BTlgotbfSKDD7TiXoRonf36XlutCJ", key));
        System.out.println(decrypt("osVFk4Ct14StOhrgF-UJdg:LJVndkjwCmj7pD1mANPejNMG0qL4eXeMg7w0ofH_7zMPsKnQbykci6MZEO-tlDVR", key));
    }
}
