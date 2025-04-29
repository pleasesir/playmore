package org.playmore.common.util;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class TurPle<T extends Serializable, V extends Serializable> implements Serializable {
    @Serial
    private static final long serialVersionUID = -8377310095194380661L;
    private T a;
    private V b;

    public TurPle() {
    }

    public TurPle(T a, V b) {
        this.setA(a);
        this.setB(b);
    }

    @Override
    public String toString() {
        return "TurPle [a=" + a + ", b=" + b + "]";
    }

}
