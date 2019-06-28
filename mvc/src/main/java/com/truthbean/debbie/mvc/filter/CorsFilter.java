package com.truthbean.debbie.mvc.filter;

import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
        return true;
    }

    @Override
    public void postRouter(RouterRequest request, RouterResponse response) {
        LOGGER.debug("filter cors");
        var cors = configuration.getCors();
        if (cors != null && !cors.isEmpty()) {
            cors.forEach(response::addHeader);
        } else {
            response.addHeader("Access-Control-Allow-Origin", "*");

            response.addHeader("Access-Control-Allow-Headers", "User-Agent,Origin,Cache-Control,Content-type,Date,Server,withCredentials,AccessToken");
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            response.addHeader("Access-Control-Max-Age", "1209600");
            response.addHeader("Access-Control-Expose-Headers", "accesstoken");
            response.addHeader("Access-Control-Request-Headers", "accesstoken");
            response.addHeader("Expires", "-1");
            response.addHeader("Cache-Control", "no-cache");
            response.addHeader("pragma", "no-cache");
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CorsFilter.class);
}
