package com.truthbean.debbie.mvc.filter;

import com.truthbean.debbie.mvc.MvcConfiguration;
import com.truthbean.debbie.mvc.request.RouterRequest;
import com.truthbean.debbie.mvc.response.RouterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/15 21:21.
 */
public class CharacterEncodingFilter implements RouterFilter {

    private Charset charset;

    @Override
    public CharacterEncodingFilter setMvcConfiguration(MvcConfiguration configuration) {
        charset = configuration.getCharset();
        return this;
    }

    @Override
    public boolean preRouter(RouterRequest request, RouterResponse response) {
        LOGGER.debug("character encoding filter");
        request.setCharacterEncoding(this.charset);
        response.setCharacterEncoding(this.charset);
        return true;
    }

    @Override
    public void postRouter(RouterRequest request, RouterResponse response) {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterEncodingFilter.class);
}