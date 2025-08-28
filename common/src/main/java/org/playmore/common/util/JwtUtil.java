package org.playmore.common.util;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import com.alibaba.fastjson2.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.lang.ref.SoftReference;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;

/**
 * @ClassName JwtUtil
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/28 23:32
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/28 23:32
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class JwtUtil {
    private final static byte[] TOKEN_SECRET = "token_treasure#$!#erusaert_nekot".getBytes();

    /**
     * AES相关配置
     */
    private static final String ALGORITHM = "AES/CTR/NoPadding";
    private static final SecretKeySpec KEY_SPEC = new SecretKeySpec(TOKEN_SECRET, "AES");
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final ThreadLocal<SoftReference<Cipher>> CIPHER_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        try {
            return new SoftReference<>(Cipher.getInstance(ALGORITHM));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });

    static {
        // Initialize the key once
        SECURE_RANDOM.nextBytes(TOKEN_SECRET);
    }

    private static byte[] encrypt(byte[] plaintext) throws Exception {
        byte[] nonce = new byte[16];
        SECURE_RANDOM.nextBytes(nonce);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(nonce);
        Cipher cipher = CIPHER_THREAD_LOCAL.get().get();
        if (cipher == null) {
            cipher = Cipher.getInstance(ALGORITHM);
        }
        cipher.init(Cipher.ENCRYPT_MODE, KEY_SPEC, ivParameterSpec);

        byte[] ciphertext = cipher.doFinal(plaintext);

        // Prepend the nonce to the ciphertext to ensure it is available for decryption
        byte[] ciphertextWithNonce = new byte[nonce.length + ciphertext.length];
        System.arraycopy(nonce, 0, ciphertextWithNonce, 0, nonce.length);
        System.arraycopy(ciphertext, 0, ciphertextWithNonce, nonce.length, ciphertext.length);

        return ciphertextWithNonce;
    }

    private static byte[] decrypt(byte[] ciphertext) throws Exception {
        // Extract the nonce from the beginning of the ciphertext
        byte[] nonce = new byte[16];
        System.arraycopy(ciphertext, 0, nonce, 0, nonce.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(nonce);
        Cipher cipher = CIPHER_THREAD_LOCAL.get().get();
        if (cipher == null) {
            cipher = Cipher.getInstance(ALGORITHM);
        }
        cipher.init(Cipher.ENCRYPT_MODE, KEY_SPEC, ivParameterSpec);

        // Extract the actual ciphertext from the input array
        byte[] ciphertextBytes = new byte[ciphertext.length - nonce.length];
        System.arraycopy(ciphertext, nonce.length, ciphertextBytes, 0, ciphertextBytes.length);

        return cipher.doFinal(ciphertextBytes);
    }

    public static String getUserToken(String publicKey) throws Exception {
        // 使用对称加密对非对称加密生成的publicKey进行加密
        return Base64Encoder.encode(encrypt(publicKey.getBytes(StandardCharsets.UTF_8)));
    }


    public static String getTokenBody(String token) throws Exception {
        return new String(decrypt(Base64Decoder.decode(token)));
    }

    /**
     * 反解出jtw生成的token中payload部分 subject
     *
     * @param jwt
     * @return
     */
    public static JSONObject decodeJwt(String jwt) {
        String[] splitString = jwt.split("\\.");
        String base64EncodedPayload = splitString[1];
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(base64EncodedPayload), StandardCharsets.UTF_8);
        return JSONObject.parseObject(payload);
    }

    public static TurPle<String, String> createToken() throws Exception {
        // 生成 RSA 的公钥
        HashMap<String, Object> keyPair = (HashMap<String, Object>) RSAUtils.genKeyPair();
        byte[] privateKeyBytes = RSAUtils.getPrivateKey(keyPair);
        String privateKey = Base64Encoder.encode(privateKeyBytes);
        // 获取公钥
        byte[] publicKeyBytes = RSAUtils.getPublicKey(keyPair);
        // 为了方便传输，对 bytes 数组进行一下 base64 编码
        String publicKey = Base64Encoder.encode(publicKeyBytes);
        String token = JwtUtil.getUserToken(publicKey);
        return new TurPle<>(privateKey, token);
    }
}

