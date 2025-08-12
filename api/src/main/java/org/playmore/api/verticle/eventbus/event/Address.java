package org.playmore.api.verticle.eventbus.event;

import java.util.Locale;

public interface Address {

    String SPLICING_SYMBOL = "_";

    default String getAddress() {
        return this.getClass().getSimpleName().toLowerCase(Locale.ROOT)
                + SPLICING_SYMBOL
                + toString().toLowerCase(Locale.ROOT);
    }

    default boolean uniqueAddress() {
        return true;
    }

}
