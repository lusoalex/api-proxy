package com.api.proxy.utils;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpHeaders.Names.IF_RANGE;
import static io.netty.handler.codec.http.HttpHeaders.Names.IF_UNMODIFIED_SINCE;
import static io.vertx.core.http.HttpHeaders.*;

/**
 * Created by Alexandre on 14/05/2016.
 */
public class ProxyHttpHeaderFilter {

    private final static Logger log = LoggerFactory.getLogger(ProxyHttpHeaderFilter.class);

    private final static HashSet<String> ALLOWED_REQUEST_HEADERS = new HashSet(Arrays.asList(
            ACCEPT, ACCEPT_CHARSET, ACCEPT_LANGUAGE, AUTHORIZATION, CACHE_CONTROL, CONTENT_TYPE, DATE, EXPECT, FROM, IF_MATCH, IF_MODIFIED_SINCE, IF_NONE_MATCH,
            IF_RANGE, IF_UNMODIFIED_SINCE, /*LINK, MAX_FORWARDS, PRAGMA,*/ PROXY_AUTHORIZATION, /*RANGE,*/ REFERER, UPGRADE /*,VIA*/
    ).stream().map(s -> s.toString().toLowerCase()).collect(Collectors.toSet()));
    private final static HashSet<String> ALLOWED_RESPONSE_HEADERS = new HashSet(Arrays.asList(
            ACCEPT_RANGES, AGE, ALLOW, CACHE_CONTROL, /*CONTENT_DISPOSITION,*/ CONTENT_LANGUAGE, CONTENT_LOCATION, CONTENT_RANGE, CONTENT_TYPE, DATE, ETAG, EXPIRES,
            LAST_MODIFIED, /*LINK,*/ LOCATION, /*PRAGMA,*/ PROXY_AUTHENTICATE, RETRY_AFTER/*, VARY, WARNING, WWW_AUTHENTICATE*/
    ).stream().map(s -> s.toString().toLowerCase()).collect(Collectors.toSet()));

    public static boolean filterHttpRequestHeader(Map.Entry<String,String> entry) {
        log.info("Filtering request header : {}",entry.getKey());
        return ALLOWED_REQUEST_HEADERS.contains(entry.getKey().toLowerCase());
    }

    public static boolean filterHttpResponseHeader(Map.Entry<String,String> entry) {
        log.info("Filtering response header : {}",entry.getKey());
        return ALLOWED_RESPONSE_HEADERS.contains(entry.getKey().toLowerCase());
    }
}
