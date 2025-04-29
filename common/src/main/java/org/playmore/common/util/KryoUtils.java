package org.playmore.common.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.Objects;

/**
 * @Description
 * @Author zhangdh
 * @Date 2021-09-26 17:14
 */
public class KryoUtils {
    private static final ThreadLocal<Output> OUTPUT_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Input> INPUT_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Kryo> KRYO_LOCAL = new ThreadLocal<>();

    public static byte[] serialize(Object object) {
        Output output = getOutput();
        output.setBuffer(new byte[1024], -1);
        Kryo kryo = getKryo();
        kryo.writeObject(output, object);
        return output.getBuffer();
    }

    public static byte[] serializeClassAndObject(Object object) {
        Output output = getOutput();
        output.setBuffer(new byte[1024], -1);
        Kryo kryo = getKryo();
        kryo.writeClassAndObject(output, object);
        return output.getBuffer();
    }

    public static <T> T deSerialize(byte[] objBytes, Class<T> cls) {
        Input input = getInput();
        input.setBuffer(objBytes);
        Kryo kryo = getKryo();
        return kryo.readObject(input, cls);
    }

    public static Object deSerializeClassAndObject(byte[] objBytes) {
        Input input = getInput();
        input.setBuffer(objBytes);
        Kryo kryo = getKryo();
        return kryo.readClassAndObject(input);
    }

    private static Kryo getKryo() {
        Kryo kryo = KRYO_LOCAL.get();
        if (Objects.isNull(kryo)) {
            kryo = new Kryo();
            kryo.setRegistrationRequired(false);
            kryo.setReferences(false);
            KRYO_LOCAL.set(kryo);
        }
        kryo.reset();
        return kryo;
    }

    private static Output getOutput() {
        Output output = OUTPUT_LOCAL.get();
        if (Objects.isNull(output)) {
            OUTPUT_LOCAL.set(output = new Output());
        }
        output.reset();
        return output;
    }

    private static Input getInput() {
        Input input = INPUT_LOCAL.get();
        if (Objects.isNull(input)) {
            INPUT_LOCAL.set(input = new Input());
        }
        input.reset();
        return input;
    }

}
