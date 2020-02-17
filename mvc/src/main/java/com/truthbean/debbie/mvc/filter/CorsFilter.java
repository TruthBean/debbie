package com.truthbean.debbie.mvc.filter;

import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.HttpHeader;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.truthbean.debbie.mvc.request.HttpHeader.HttpHeaderNames.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/25 22:03.
 */
public class CorsFilter implements RouterFilter {

    private MvcConfiguration configuration;

    @Override
    public CorsFilter setMvcConfiguration(MvcConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    @Override
    public boolean preRouter(RouterRequest request, RouterResponse response) {
        return this.configuration.isEnableCors() && isCorsRequest(request) && !isPreFlightRequest(request);
    }

    @Override
    public void postRouter(RouterRequest request, RouterResponse response) {
        LOGGER.debug("filter cors");
        var cors = configuration.getCors();
        if (cors != null && !cors.isEmpty()) {
            cors.forEach(response::addHeader);
        } else {
            response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");

            response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, "User-Agent,Origin,Cache-Control,Content-type,Date,Server,withCredentials,AccessToken");
            response.addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            response.addHeader(ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            response.addHeader(ACCESS_CONTROL_MAX_AGE, "1209600");
            response.addHeader(ACCESS_CONTROL_EXPOSE_HEADERS, "accesstoken");
            response.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, "accesstoken");
            response.addHeader(EXPIRES, "-1");
            response.addHeader(CACHE_CONTROL, "no-cache");
            response.addHeader(PRAGMA, "no-cache");
        }
    }

    /**
     * Returns {@code true} if the request is a valid CORS one.
     * @param request router request
     * @return is cors request
     */
    public static boolean isCorsRequest(RouterRequest request) {
        final HttpHeader header = request.getHeader();
        if (header != null) {
            String origin = header.getHeader(HttpHeader.HttpHeaderNames.ORIGIN);
            return origin != null;
        }
        return false;
    }

    /**
     * @param request router request
     * @return {@code true} if the request is a valid CORS pre-flight one.
     */
    public static boolean isPreFlightRequest(RouterRequest request) {
        HttpHeader.HttpHeaderName acrm = HttpHeader.HttpHeaderNames.ACCESS_CONTROL_REQUEST_METHOD;
        return (isCorsRequest(request) && HttpMethod.OPTIONS == request.getMethod() &&
                request.getHeader().getHeader(acrm) != null);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CorsFilter.class);
}
