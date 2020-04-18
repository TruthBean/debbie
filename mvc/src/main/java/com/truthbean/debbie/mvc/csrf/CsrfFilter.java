package com.truthbean.debbie.mvc.csrf;

import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.filter.RouterFilter;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.1
 * @since 2019-03-30 14:15
 */
public class CsrfFilter implements RouterFilter {

    private MvcConfiguration configuration;

    @Override
    public CsrfFilter setMvcConfiguration(MvcConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    @Override
    public boolean preRouter(RouterRequest request, RouterResponse response) {
        String csrfTokenInHeader = request.getHeader().getHeader("CSRF-TOKEN");
        logger.debug("crsfToken in header: " + csrfTokenInHeader);
        String csrfTokenInParams = (String) request.getParameter("_CSRF_TOKEN");
        logger.debug("crsfToken in hidden form: " + csrfTokenInParams);
        // return configuration.isEnableCrsf();
        return true;
    }

    @Override
    public Boolean postRouter(RouterRequest request, RouterResponse response) {
        return false;
    }

    private final Logger logger = LoggerFactory.getLogger(CsrfFilter.class);
}
