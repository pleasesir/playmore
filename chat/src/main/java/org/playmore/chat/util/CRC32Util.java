package org.playmore.chat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;
import java.util.zip.CRC32;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-09-24 22:03
 */
public class CRC32Util {
    private final static Logger logger = LoggerFactory.getLogger(CRC32Util.class);
    private static final ThreadLocal<CRC32> CRC32_LOCAL = new ThreadLocal<>();


    public static long calcCRC32Value(Object obj) {
        if (obj == null) {
            return -1;
        }
        byte[] bytes = KryoUtils.serialize(obj);
        if (bytes == null) {
            return -1;
        }
        CRC32 crc32 = getCrc32();
        crc32.update(bytes);
        return crc32.getValue();
    }

    public static long calcBytesCRC32Value(byte[] bytes) {
        CRC32 crc32 = new CRC32();
        crc32.reset();
        crc32.update(bytes);
        return crc32.getValue();
    }

    private static CRC32 getCrc32() {
        CRC32 crc32 = CRC32_LOCAL.get();
        if (Objects.isNull(crc32)) {
            crc32 = new CRC32();
            CRC32_LOCAL.set(crc32);
        }
        crc32.reset();
        return crc32;
    }


    private static byte[] object2ByteArray(Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            return bos.toByteArray();
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }
}
