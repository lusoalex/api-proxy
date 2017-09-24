package com.api.proxy.handler;

import com.api.proxy.utils.ProxyHttpHeaderFilter;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Created by Alexandre on 14/05/2016.
 */
public class ProxyHandler {

    //Logger
    private final static Logger log = LoggerFactory.getLogger(ProxyHandler.class);


    //Attributes
    private Vertx vertx;
    private HttpClient client;

    public ProxyHandler(Vertx vertx) {
        this.vertx=vertx;
    }

    public void doProxy(HttpServerRequest incomingRequest) {
        log.info("Do proxy on : "+incomingRequest.absoluteURI());

        if(client==null) {
            client = vertx.createHttpClient();
        }

        // http://apifake.decathlon.com/api-fake-server-mvc/api/v1/stores/118
        HttpClientRequest requestToSend = client.request(
                incomingRequest.method(), //Use same method than the incoming request.
                "apifake.decathlon.com",  //Now send the request to another domain
                "/api-fake-server-mvc/api/v1/stores/118", //the api call...
                response -> {
                    log.info("Reading response status code : "+response.statusCode());
                    incomingRequest.response().setStatusCode(response.statusCode());
                    incomingRequest.response().setStatusMessage(response.statusMessage());

                    //Filter & set headers to the final response.
                    response.headers().entries().stream()
                            .filter(ProxyHttpHeaderFilter::filterHttpResponseHeader)
                            .forEach(header -> incomingRequest.response().putHeader(header.getKey(),header.getValue()));

                    /* SOLUTION A :
                        bodyHandler is triggered once the full body has been received and avoid to manage chunked responses.
                        Need to call end method on the buffer to stop the event.
                        This solution may be risky in case of heavy body response, because all will be in memory.
                    */
                    //response.bodyHandler(incomingRequest.response()::end);

                    /* Solution B :
                        Write the response by piece "chunk", each time a block of data is receipt, and then send it.
                        As advantages the full body will not be fully loaded in memory (but sent by chunk)
                        Needs you to specify the use of chunk (incomingRequest.response().setChunked(true)
                        Need you to manage handler & endHandler event.
                        Note that all browser may not managed chunked responses....
                    */
                    response.handler(incomingRequest.response()::write);
                    //can't use method reference cause he can't resolve method :'(
                    response.endHandler(event -> incomingRequest.response().end());
                }
        );

        incomingRequest.response().setChunked(true);//comment if you do not use solution B.

        //Filter & set headers to the request to send.
        incomingRequest.headers().entries().stream()
                .filter(ProxyHttpHeaderFilter::filterHttpRequestHeader)
                .map(header -> requestToSend.putHeader(header.getKey(), header.getValue()));

        incomingRequest.handler(requestToSend::write);//propage body (pour autre que GET)

        //Once ok with the incoming request, proceed the proxy (request to send)
        incomingRequest.endHandler(event -> requestToSend.end());
    }
}
