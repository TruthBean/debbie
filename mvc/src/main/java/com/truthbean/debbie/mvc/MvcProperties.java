package com.truthbean.debbie.mvc;

import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.core.properties.AbstractProperties;
import com.truthbean.debbie.mvc.request.HttpMethod;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/2/25 22:02.
 */
public class MvcProperties extends AbstractProperties {
    //===========================================================================
    private static final String SERVER_DEFAULT_TYPES = "debbie.web.default.types";
    private static final String SERVER_CSRF = "debbie.web.csrf";

    private static final String SERVER_CORS = "debbie.web.cors";
    private static final String SERVER_CORS_ORIGINS = "debbie.web.cors.origins";
    private static final String SERVER_CORS_HEADERS = "debbie.web.cors.headers";
    private static final String SERVER_CORS_METHODS = "debbie.web.cors.methods";

    private static final String WEB_VIEW_TEMPLATE_SUFFIX = "debbie.web.view.template.suffix";
    private static final String WEB_VIEW_TEMPLATE_PREFIX = "debbie.web.view.template.prefix";

    //===========================================================================

    public static MvcConfiguration toConfiguration() {
        MvcProperties properties = new MvcProperties();

        MvcConfiguration.Builder builder = MvcConfiguration.builder();

        List<MediaType> defaultTypes = properties.getMediaTypeListValue(SERVER_DEFAULT_TYPES, ",");
        if (defaultTypes != null && !defaultTypes.isEmpty()) {
            builder.defaultTypes(defaultTypes);
        }

        builder.template(properties.getValue(WEB_VIEW_TEMPLATE_SUFFIX), properties.getValue(WEB_VIEW_TEMPLATE_PREFIX));

        boolean csrf = properties.getBooleanValue(SERVER_CSRF, false);
        if (csrf) {
            builder.enableCrsf();
        }

        boolean cors = properties.getBooleanValue(SERVER_CORS, false);
        if (cors) {
            List<String> corsOrigins = properties.getStringListValue(SERVER_CORS_ORIGINS, ",");
            List<String> corsHeaders = properties.getStringListValue(SERVER_CORS_HEADERS, ",");
            List<HttpMethod> corsMethods = properties.getRequestMethodListValue(SERVER_CORS_METHODS, ",");
            builder.enableCors(corsOrigins, corsMethods, corsHeaders);
        }

        return builder.build();
    }

    public List<MediaType> getMediaTypeListValue(String key, String split) {
        String[] value = getStringArrayValue(key, split);
        List<MediaType> result = null;
        if (value != null) {
            result = Arrays.stream(value).map(MediaType::of).collect(Collectors.toList());
        }
        return result;
    }

    public List<HttpMethod> getRequestMethodListValue(String key, String split) {
        String[] value = getStringArrayValue(key, split);
        List<HttpMethod> result = null;
        if (value != null) {
            result = Arrays.stream(value).map(HttpMethod::valueOf).collect(Collectors.toList());
        }
        return result;
    }
}
