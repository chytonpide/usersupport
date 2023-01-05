package com.nconnect.usersupport.infrastructure.resource;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.Header;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.ResponseHeader;

import static io.vlingo.xoom.common.Completes.withSuccess;
import static io.vlingo.xoom.http.Response.Status.NotFound;
import static io.vlingo.xoom.http.Response.Status.Ok;
import static io.vlingo.xoom.http.ResponseHeader.headers;

public class CorsNegotiation {
    private final static String ALLOW_ORIGIN = "http://localhost:3000";
    private final static String ALLOW_METHODS = "*";
    private final static String ALLOW_HEADERS = "Authorization, Content-Type,";
    private final static String EXPOSE_HEADERS = "*";

    public static Header.Headers<ResponseHeader> headers() {
        return ResponseHeader.headers(ResponseHeader.of(ResponseHeader.AccessControlAllowOrigin, ALLOW_ORIGIN))
                .and(ResponseHeader.of(ResponseHeader.AccessControlAllowMethods, ALLOW_METHODS))
                .and(ResponseHeader.of(ResponseHeader.AccessControlAllowHeaders, ALLOW_HEADERS))
                .and(ResponseHeader.of(ResponseHeader.AccessControlExposeHeaders, EXPOSE_HEADERS));
    }


    public static Completes<Response> response() {
        return withSuccess(Response.of(Ok, headers())).otherwise(arg -> Response.of(NotFound));
    }



}
