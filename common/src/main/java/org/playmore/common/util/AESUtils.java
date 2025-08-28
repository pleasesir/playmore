package org.playmore.common.util;

import cn.hutool.core.util.RandomUtil;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @ClassName AESUtils
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/28 23:33
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/28 23:33
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class AESUtils {
    public static String createSecret() {
        return RandomUtil.randomString(16);
    }

    public static byte[] decode(String secret, byte[] content) {
        try {
            SecretKey key = generateKey(secret);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(content);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            LogUtil.error(e);
        }
        return null;
    }

    public static byte[] encode(String secret, byte[] content) {
        try {
            SecretKey key = generateKey(secret);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(content);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            LogUtil.error(e);
        }
        return null;
    }

    public static SecretKey generateKey(String secret) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(secret.getBytes());
        keyGen.init(128, secureRandom);
        SecretKey originalKey = keyGen.generateKey();
        byte[] raw = originalKey.getEncoded();
        return new SecretKeySpec(raw, "AES");
    }
}
