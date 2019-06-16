package com.truthbean.debbie.mvc;

import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.response.AbstractResponseContentHandler;
import com.truthbean.debbie.mvc.response.provider.NothingResponseHandler;
import com.truthbean.debbie.properties.BaseProperties;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.properties.DebbieProperties;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/2/25 22:02.
 */
public class MvcProperties extends BaseProperties implements DebbieProperties {
    //===========================================================================
    /**
     * static resources mapping and location, only support /XX/**=XX, like /static/**=static, or /resources/**=META-INF/resources;/webjar/**=/META-INF/resources/webjars
     */
    private static final String STATIC_RESOURCES_MAPPING_LOCATION = "debbie.web.static-resources-mapping-location";
    /**
     * use ** to replace path, like **.do, /api/**, /api/**.do
     * NOT: but do not support multi **, like /api/**-controller/**.do
     */
    private static final String DISPATCHER_MAPPING = "debbie.web.dispatcher-mapping";

    private static final String SERVER_RESPONSE_ALLOW_CLIENT = "debbie.web.default.response.allow-client";
    private static final String SERVER_RESPONSE_DEFAULT_TYPES = "debbie.web.default.response.types";

    private static final String SERVER_CONTENT_DEFAULT_TYPES = "debbie.web.default.content.types";
    private static final String SERVER_CONTENT_ACCEPT_CLIENT = "debbie.web.default.content.accept-client";


    private static final String SERVER_CSRF = "debbie.web.csrf";

    private static final String SERVER_CORS = "debbie.web.cors";
    private static final String SERVER_CORS_ORIGINS = "debbie.web.cors.origins";
    private static final String SERVER_CORS_HEADERS = "debbie.web.cors.headers";
    private static final String SERVER_CORS_METHODS = "debbie.web.cors.methods";

    private static final String WEB_VIEW_TEMPLATE_SUFFIX = "debbie.web.view.template.suffix";
    private static final String WEB_VIEW_TEMPLATE_PREFIX = "debbie.web.view.template.prefix";

    public static final String DEFAULT_RESPONSE_HANDLER = "debbie.web.response.default.handler";

    //===========================================================================

    private static MvcConfiguration configurationCache;

    public static MvcConfiguration toConfiguration() {
        if (configurationCache != null) {
            return configurationCache;
        }
        buildConfiguration();
        return configurationCache;
    }

    private static void buildConfiguration() {
        MvcProperties properties = new MvcProperties();

        MvcConfiguration.Builder builder = MvcConfiguration.builder()
                .staticResourcesMapping(properties.getMapValue(STATIC_RESOURCES_MAPPING_LOCATION, "=", ";"))
                .dispatcherMapping(properties.getStringValue(DISPATCHER_MAPPING, "/**"))
                .allowClientResponseType(properties.getBooleanValue(SERVER_RESPONSE_ALLOW_CLIENT, false))
                .acceptClientContentType(properties.getBooleanValue(SERVER_CONTENT_ACCEPT_CLIENT, false));

        List<MediaTypeInfo> defaultResponseTypes = properties.getMediaTypeListValue(SERVER_RESPONSE_DEFAULT_TYPES, ",");
        if (defaultResponseTypes != null && !defaultResponseTypes.isEmpty()) {
            builder.defaultResponseTypes(defaultResponseTypes);
        }

        List<MediaTypeInfo> defaultRequestTypes = properties.getMediaTypeListValue(SERVER_CONTENT_DEFAULT_TYPES, ",");
        if (defaultRequestTypes != null && !defaultRequestTypes.isEmpty()) {
            builder.defaultContentTypes(defaultRequestTypes);
        }

        var nothingResponseHandler = "com.truthbean.debbie.mvc.response.provider.NothingResponseHandler";
        var responseContentHandler = (Class<? extends AbstractResponseContentHandler>) properties.getClassValue(DEFAULT_RESPONSE_HANDLER, nothingResponseHandler);
        builder.template(properties.getValue(WEB_VIEW_TEMPLATE_SUFFIX), properties.getValue(WEB_VIEW_TEMPLATE_PREFIX), responseContentHandler);

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

        configurationCache = builder.build();
    }

    public List<MediaTypeInfo> getMediaTypeListValue(String key, String split) {
        String[] value = getStringArrayValue(key, split);
        List<MediaTypeInfo> result = null;
        if (value != null) {
            result = Arrays.stream(value).map(MediaTypeInfo::parse).collect(Collectors.toList());
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

    @Override
    public MvcConfiguration toConfiguration(BeanFactoryHandler beanFactoryHandler) {
        return toConfiguration();
    }
}
