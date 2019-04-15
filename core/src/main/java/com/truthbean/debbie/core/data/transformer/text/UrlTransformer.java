package com.truthbean.debbie.core.data.transformer.text;

import com.truthbean.debbie.core.data.transformer.DataTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class UrlTransformer implements DataTransformer<URL, String> {
    @Override
    public String transform(URL url) {
        return url.toString();
    }

    @Override
    public URL reverse(String s) {
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlTransformer.class);
}
