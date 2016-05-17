package com.api.proxy;

import com.api.proxy.handler.ProxyHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Created by Alexandre on 13/05/2016.
 */
public class GatewayVerticle extends AbstractVerticle {

    private final static Logger log = LoggerFactory.getLogger(AbstractVerticle.class);

    @Override
    public void start(Future<Void> future) {
        log.info("Starting Gateway");

        //TODO: extract below code in private method
        final String DEFAULT_HOST = "localhost";
        final Integer DEFAULT_PORT = 8081;

        HttpServerOptions options = new HttpServerOptions();
        JsonObject config = context.config();
        JsonObject serverConfig = config.getJsonObject("server");
        if (serverConfig == null) {
            options.setHost(DEFAULT_HOST);
            options.setPort(DEFAULT_PORT);
        }
        HttpServer server = vertx.createHttpServer(options);

        ProxyHandler proxy = new ProxyHandler(vertx);
        server.requestHandler(proxy::doProxy);

        server.listen(res -> {
            if (res.failed()) {
                future.fail(res.cause());
            } else {
                log.info("...Gateway server started");
                future.complete();
            }
        });
    }

    @Override
    public void stop(Future<Void> future) {
        log.info("Closing Gateway server");
    }

    public void sayHello() {
        log.info("Hello world !!");
    }

}
