package com.github.bingoohuang.utils.crypto;

import com.github.bingoohuang.utils.codec.Base64;
import com.github.bingoohuang.utils.codec.Base64.Format;
import com.github.bingoohuang.utils.lang.Closer;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static com.github.bingoohuang.utils.codec.Base64.base64;
import static com.github.bingoohuang.utils.codec.Base64.unBase64;
import static com.github.bingoohuang.utils.codec.Bytes.bytes;

public class RSA {
    public static final String KEY_ALGORITHMS = "RSA";
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    public static String sign(String plainText, String privateKey) {
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(generatePrivateKey(privateKey));
            signature.update(bytes(plainText));
            return base64(signature.sign(), Format.Standard);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verify(String plainText, String sign, String publicKey) {
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(generatePublicKey(publicKey));
            signature.update(bytes(plainText));
            return signature.verify(unBase64(sign));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String cipherText, String privateKey, String cipherTextCharset) {
        InputStream ins = null;
        ByteArrayOutputStream writer = null;
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHMS);
            cipher.init(Cipher.DECRYPT_MODE, generatePrivateKey(privateKey));

            ins = new ByteArrayInputStream(unBase64(cipherText));
            writer = new ByteArrayOutputStream();

            //rsa解密的字节大小最多是128，将需要解密的内容，按128位拆开解密
            byte[] buf = new byte[128];
            int bufl;

            while ((bufl = ins.read(buf)) != -1) {
                byte[] block = null;

                if (buf.length == bufl) {
                    block = buf;
                } else {
                    block = new byte[bufl];
                    for (int i = 0; i < bufl; i++) {
                        block[i] = buf[i];
                    }
                }

                writer.write(cipher.doFinal(block));
            }
            return new String(writer.toByteArray(), cipherTextCharset);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Closer.closeQuietly(ins, writer);
        }
    }

    public static PrivateKey generatePrivateKey(String privateKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(KEY_ALGORITHMS).generatePrivate(
                new PKCS8EncodedKeySpec(unBase64(privateKey)));
    }

    public static PublicKey generatePublicKey(String publicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(KEY_ALGORITHMS).generatePublic(
                new X509EncodedKeySpec(unBase64(publicKey)));
    }

    public static String parseKeyAsBase64String(Key key) {
        return Base64.base64(key.getEncoded(), Format.Standard);
    }
}
