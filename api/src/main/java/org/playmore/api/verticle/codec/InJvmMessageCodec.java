package org.playmore.api.verticle.codec;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class InJvmMessageCodec  implements MessageCodec<Object, Object> {

    public static final String CODEC_NAME = "in-jvm";

    @Override
    public void encodeToWire(Buffer buffer, Object o) {

    }

    @Override
    public Object decodeFromWire(int pos, Buffer buffer) {
        return null;
    }

    @Override
    public Object transform(Object o) {
        return o;
    }

    @Override
    public String name() {
        return CODEC_NAME;
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
