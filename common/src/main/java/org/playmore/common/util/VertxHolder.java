package org.playmore.common.util;

import io.vertx.core.Vertx;

public class VertxHolder {

    public static Vertx vertx;


    public static void init(Vertx vertx) {
        VertxHolder.vertx = vertx;
    }
}
