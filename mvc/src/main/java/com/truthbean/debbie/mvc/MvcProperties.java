/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc;

import com.truthbean.core.util.StringUtils;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.DebbieEnvironmentDepositoryHolder;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.request.HttpMethod;
import com.truthbean.debbie.mvc.response.AbstractResponseContentHandler;
import com.truthbean.debbie.properties.DebbieProperties;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/2/25 22:02.
 */
public class MvcProperties extends DebbieEnvironmentDepositoryHolder implements DebbieProperties<MvcConfiguration> {
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

    private static final String SERVER_CHARSET = "debbie.web.charset";

    private static final String SERVER_CORS = "debbie.web.cors";
    private static final String SERVER_CORS_ORIGINS = "debbie.web.cors.origins";
    private static final String SERVER_CORS_HEADERS = "debbie.web.cors.headers";
    private static final String SERVER_CORS_METHODS = "debbie.web.cors.methods";

    private static final String WEB_VIEW_TEMPLATE_SUFFIX = "debbie.web.view.template.suffix";
    private static final String WEB_VIEW_TEMPLATE_PREFIX = "debbie.web.view.template.prefix";

    public static final String DEFAULT_RESPONSE_HANDLER = "debbie.web.response.default.handler";

    //===========================================================================

    private static final Map<String, Map<String, MvcConfiguration>> configurationCache = new HashMap<>();

    @Override
    public Map<String, Map<String, MvcConfiguration>> getAllProfiledCategoryConfiguration(ApplicationContext applicationContext) {
        return configurationCache;
    }

    @Override
    public Set<String> getCategories(String profile) {
        return null;
    }

    @Override
    public Set<String> getCategories() {
        return new HashSet<>();
    }

    @Override
    public MvcConfiguration getConfiguration(String profile, String category, ApplicationContext applicationContext) {
        if (!StringUtils.hasText(profile)) {
            profile = getDefaultProfile();
        }
        if (!StringUtils.hasText(category)) {
            category = DEFAULT_CATEGORY;
        }
        if (configurationCache.containsKey(getDefaultProfile())) {
            Map<String, MvcConfiguration> map = configurationCache.get(getDefaultProfile());
            if (map.containsKey(DEFAULT_CATEGORY)) {
                return map.get(DEFAULT_CATEGORY);
            }
        }
        buildConfiguration(applicationContext.getClassLoader());
        return configurationCache.get(profile).get(category);
    }

    public static MvcConfiguration toConfiguration(ClassLoader classLoader) {
        if (configurationCache.containsKey(DEFAULT_PROFILE)) {
            Map<String, MvcConfiguration> map = configurationCache.get(DEFAULT_PROFILE);
            if (map.containsKey(DEFAULT_CATEGORY)) {
                return map.get(DEFAULT_CATEGORY);
            }
        }
        buildConfiguration(classLoader);
        return configurationCache.get(DEFAULT_PROFILE).get(DEFAULT_CATEGORY);
    }

    private static void buildConfiguration(ClassLoader classLoader) {
        MvcProperties properties = new MvcProperties();
        Set<String> profiles = properties.getProfiles();
        for (String profile : profiles) {
            Environment environment = properties.getEnvironmentIfPresent(profile);
            MvcConfiguration.Builder builder = MvcConfiguration.builder(classLoader)
                    .staticResourcesMapping(environment.getMapValue(STATIC_RESOURCES_MAPPING_LOCATION, "=", ";"))
                    .dispatcherMapping(environment.getStringValue(DISPATCHER_MAPPING, "/**"))
                    .allowClientResponseType(environment.getBooleanValue(SERVER_RESPONSE_ALLOW_CLIENT, true))
                    .acceptClientContentType(environment.getBooleanValue(SERVER_CONTENT_ACCEPT_CLIENT, true));

            List<MediaTypeInfo> defaultResponseTypes = properties.getMediaTypeListValue(environment, SERVER_RESPONSE_DEFAULT_TYPES, ",");
            if (defaultResponseTypes != null && !defaultResponseTypes.isEmpty()) {
                builder.defaultResponseTypes(defaultResponseTypes);
            }

            List<MediaTypeInfo> defaultRequestTypes = properties.getMediaTypeListValue(environment, SERVER_CONTENT_DEFAULT_TYPES, ",");
            if (defaultRequestTypes != null && !defaultRequestTypes.isEmpty()) {
                builder.defaultContentTypes(defaultRequestTypes);
            }

            var nothingResponseHandler = "com.truthbean.debbie.mvc.response.provider.NothingResponseHandler";
            @SuppressWarnings({"unchecked"})
            var responseContentHandler = (Class<? extends AbstractResponseContentHandler<?, ?>>) environment.getClassValue(DEFAULT_RESPONSE_HANDLER, nothingResponseHandler);
            builder.template(environment.getValue(WEB_VIEW_TEMPLATE_SUFFIX), environment.getValue(WEB_VIEW_TEMPLATE_PREFIX), responseContentHandler);

            boolean csrf = environment.getBooleanValue(SERVER_CSRF, false);
            if (csrf) {
                builder.enableCrsf();
            }

            var charset = environment.getStringValue(SERVER_CHARSET, "UTF-8");
            builder.charset(Charset.forName(charset));

            boolean cors = environment.getBooleanValue(SERVER_CORS, false);
            if (cors) {
                List<String> corsOrigins = environment.getStringListValue(SERVER_CORS_ORIGINS, ",");
                List<String> corsHeaders = environment.getStringListValue(SERVER_CORS_HEADERS, ",");
                List<HttpMethod> corsMethods = properties.getRequestMethodListValue(environment, SERVER_CORS_METHODS, ",");
                builder.enableCors(corsOrigins, corsMethods, corsHeaders);
            }
            Map<String, MvcConfiguration> map = new HashMap<>();
            map.put(DEFAULT_CATEGORY, builder.build());
            configurationCache.put(DEFAULT_PROFILE, map);
        }
    }

    public List<MediaTypeInfo> getMediaTypeListValue(Environment environment, String key, String split) {
        String[] value = environment.getStringArrayValue(key, split);
        List<MediaTypeInfo> result = null;
        if (value != null) {
            result = Arrays.stream(value).map(MediaTypeInfo::parse).collect(Collectors.toList());
        }
        return result;
    }

    public List<HttpMethod> getRequestMethodListValue(Environment environment, String key, String split) {
        String[] value = environment.getStringArrayValue(key, split);
        List<HttpMethod> result = null;
        if (value != null) {
            result = Arrays.stream(value).map(HttpMethod::valueOf).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public MvcConfiguration getConfiguration(ApplicationContext applicationContext) {
        ClassLoader classLoader = applicationContext.getClassLoader();
        return toConfiguration(classLoader);
    }

    @Override
    public void close() throws IOException {

    }
}
