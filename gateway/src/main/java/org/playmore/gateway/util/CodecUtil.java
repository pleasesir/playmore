package org.playmore.gateway.util;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.playmore.common.util.AESUtils;
import org.playmore.common.util.CheckNull;
import org.playmore.common.util.JwtUtil;
import org.playmore.common.util.RSAUtils;
import org.playmore.gateway.net.CmdFlag;
import org.playmore.gateway.net.codec.msg.WsMessagePackage;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @ClassName CodecUtil
 * @Description: 类描述
 * @Author: Administrator
 * @CreateDate: 2025/8/28 00:18
 * @UpdateUser: 更新人
 * @UpdateDate: 2025/8/28 00:18
 * @UpdateRemark: 更新的信息
 * @Version: 1.0
 */
public class CodecUtil {
    public static final byte[] EMPTY = new byte[0];

    /**
     * 消息解码
     *
     * @param msg 消息内容
     * @return 正确解析后的 proto buf 对象
     */
    public static WsMessagePackage decode2Pb(ChannelHandlerContext ctx, ByteBuf msg, int len) {
        byte flag = msg.readByte();
        byte[] bytes = new byte[len - 1];
        msg.readBytes(bytes);

        return new WsMessagePackage(flag, bytes);
    }

    public static ByteBuf encode(ChannelHandlerContext ctx, byte[] bytes, int rsCmdId, AES aes) {
        if (CheckNull.isEmpty(bytes)) {
            bytes = EMPTY;
        }
        bytes = encrypt(bytes, aes);

        int msgLen = 5 + bytes.length;
        ByteBuf buf = ctx.alloc().heapBuffer(4 + msgLen);
        buf.writeInt(msgLen);
        buf.writeByte(CmdFlag.NORMAL_MSG);
        buf.writeInt(rsCmdId);
        buf.writeBytes(bytes);

        buf.resetReaderIndex();
        return buf;
    }


    public static String encodeHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        char[] ch = HexUtil.encodeHex(bytes, false);
        for (int i = 0; i < ch.length; i++) {
            if (i % 2 == 1) {
                sb.append(ch[i - 1]).append(ch[i]).append(", ");
            }
        }
        return sb.deleteCharAt(sb.length() - 2).toString();
    }

    /**
     * 消息确认的非对称加密
     * <p>
     * 网关协议加密逻辑:
     * 1. 账号服通过非对称加密(RSA)计算出公钥与私钥, 公钥通过token形式发送给客户端, 私钥直接发给客户端
     * 2. 客户端保留私钥，token发送给网关服务；服务器执行当前方法 #CodecUtil.confirmSecretRSA 逻辑，生成随机字符串密钥16位
     * ，通过上面非对称加密出的公钥对这16位密钥进行加密返回给客户端
     * 3. 客户端通过账号服务给出的私钥对上面返回被公钥加密过的16位密钥进行解密，获得接下来协议往来的对称加密(AES)的密钥
     * 4. 客户端服务器保留16位密钥使用AES对称加解密对正常游戏协议进行加解密
     * </p>
     *
     * @param bytes
     * @return
     */
    public static String confirmSecretRsa(ChannelHandlerContext ctx, byte[] bytes) throws Exception {
        String token = new String(bytes);
        String publicKey = JwtUtil.getTokenBody(token);
        String aseSecretKey = AESUtils.createSecret();
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, aseSecretKey.getBytes());
        aes.setRandom(secureRandom);
        ChannelUtil.setEncoder(ctx, aes);
        byte[] clientPublicKey = Base64.getDecoder().decode(publicKey);
        // 使用客户端的公钥非对称加密后续所需使用的对称加密的密钥
        byte[] encryptAesKet = RSAUtils.encryptByPublicKey(aseSecretKey.getBytes(), clientPublicKey);
        return Base64.getEncoder().encodeToString(encryptAesKet);
    }

    /**
     * 对消息解密
     *
     * @param body
     * @return
     */
    public static byte[] decrypt(byte[] body, AES aes) {
        byte[] iv = new byte[16];
        byte[] data = new byte[body.length - 16];
        // 把password分割成IV和密文
        System.arraycopy(body, 0, iv, 0, 16);
        System.arraycopy(body, 16, data, 0, data.length);
        aes.setIv(iv);
        return aes.decrypt(data);
    }

    /**
     * 对消息加密
     *
     * @param body
     * @return
     */
    public static byte[] encrypt(byte[] body, AES aes) {
        byte[] iv = RandomStringUtils.secure().nextAscii(16).getBytes(StandardCharsets.UTF_8);
        aes.setIv(iv);
        byte[] data = aes.encrypt(body);
        return join(iv, data);
    }

    public static byte[] join(byte[] bs1, byte[] bs2) {
        byte[] r = new byte[bs1.length + bs2.length];
        System.arraycopy(bs1, 0, r, 0, bs1.length);
        System.arraycopy(bs2, 0, r, bs1.length, bs2.length);
        return r;
    }
}
