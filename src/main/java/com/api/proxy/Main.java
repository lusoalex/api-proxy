package com.api.proxy;

import io.vertx.core.Vertx;

/**
 * Created by Alexandre on 13/05/2016.
 */
public class Main {
    public static void main(String... args) {
        Vertx main = Vertx.vertx();
        main.deployVerticle(new GatewayVerticle());
    }
}
