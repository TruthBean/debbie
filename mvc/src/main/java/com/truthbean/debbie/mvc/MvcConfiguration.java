package com.truthbean.debbie.mvc;

import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.util.StringUtils;
import com.truthbean.debbie.mvc.exception.DispatcherMappingFormatException;
import com.truthbean.debbie.mvc.request.HttpMethod;

import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/2/25 22:08.
 */
public class MvcConfiguration extends BeanScanConfiguration {

    private String dispatcherMapping;

    /**
     * allow client change response type by request header "Response-Type"
     */
    private boolean allowClientResponseType;

    /**
     * default response type
     */
    private final Set<MediaTypeInfo> defaultResponseTypes = new HashSet<>();

    /**
     * accept client content type by request header "Content-Type"
     */
    private boolean acceptClientContentType;

    /**
     * default request type
     */
    private final Set<MediaTypeInfo> defaultContentTypes = new HashSet<>();

    // cors
    private boolean enableCors = false;
    private List<String> corsOrigins;
    private List<HttpMethod> corsMethods;
    private List<String> corsHeaders;

    private boolean enableCrsf = false;

    private String templateSuffix;
    private String templatePrefix;

    public MvcConfiguration() {
    }

    public MvcConfiguration(MvcConfiguration configuration) {
        copyFrom(configuration);
    }

    public void copyFrom(MvcConfiguration configuration) {
        this.dispatcherMapping = configuration.dispatcherMapping;

        this.allowClientResponseType = configuration.allowClientResponseType;
        this.defaultResponseTypes.addAll(configuration.defaultResponseTypes);

        this.acceptClientContentType = configuration.acceptClientContentType;
        this.defaultContentTypes.addAll(configuration.defaultContentTypes);

        this.enableCors = configuration.enableCors;
        this.corsOrigins = configuration.corsOrigins;
        this.corsMethods = configuration.corsMethods;
        this.corsHeaders = configuration.corsHeaders;

        this.templatePrefix = configuration.templatePrefix;
        this.templateSuffix = configuration.templateSuffix;
    }

    public String getDispatcherMapping() {
        return dispatcherMapping;
    }

    public void setDispatcherMapping(String dispatcherMapping) {
        this.dispatcherMapping = dispatcherMapping;
    }

    public boolean isEnableCors() {
        return enableCors;
    }

    public boolean isEnableCrsf() {
        return enableCrsf;
    }

    protected void enableCors(boolean cors) {
        this.enableCors = cors;
    }

    public List<String> getCorsOrigins() {
        return corsOrigins;
    }

    protected void setCorsOrigins(List<String> corsOrigins) {
        this.corsOrigins = corsOrigins;
    }

    public List<String> getCorsHeaders() {
        return corsHeaders;
    }

    protected void setCorsHeaders(List<String> corsHeaders) {
        this.corsHeaders = corsHeaders;
    }

    public List<HttpMethod> getCorsMethods() {
        return corsMethods;
    }

    protected void setCorsMethods(List<HttpMethod> corsMethods) {
        this.corsMethods = corsMethods;
    }

    public String getTemplateSuffix() {
        return templateSuffix;
    }

    public void setTemplateSuffix(String templateSuffix) {
        this.templateSuffix = templateSuffix;
    }

    public String getTemplatePrefix() {
        return templatePrefix;
    }

    public void setTemplatePrefix(String templatePrefix) {
        this.templatePrefix = templatePrefix;
    }

    public boolean noTemplate() {
        return (templateSuffix == null || templateSuffix.isBlank()) &&
                (templatePrefix == null || templatePrefix.isBlank());
    }

    public Map<String, String> getCors() {
        if (enableCors) {
            Map<String, String> map = new HashMap<>();
            map.put("Access-Control-Allow-Origin", StringUtils.joining(corsOrigins));
            map.put("Access-Control-Allow-Methods", StringUtils.joining(corsHeaders));
            map.put("Access-Control-Allow-Headers", StringUtils.joining(corsMethods));
            return map;
        } else {
            return null;
        }
    }

    public boolean isAcceptClientContentType() {
        return acceptClientContentType;
    }

    public Set<MediaTypeInfo> getDefaultResponseTypes() {
        return defaultResponseTypes;
    }

    public Set<MediaTypeInfo> getDefaultContentTypes() {
        return defaultContentTypes;
    }

    public boolean isAllowClientResponseType() {
        return allowClientResponseType;
    }

    public static Builder builder() {
        return new Builder(new MvcConfiguration());
    }

    public static final class Builder {
        private MvcConfiguration configuration;

        private Builder(MvcConfiguration configuration) {
            this.configuration = configuration;
        }

        public Builder enableCrsf() {
            configuration.enableCrsf = true;
            return this;
        }

        public Builder enableCors() {
            configuration.enableCors = true;
            configuration.corsOrigins = Collections.singletonList("*");
            return this;
        }

        public Builder enableCors(List<String> corsOrigins, List<HttpMethod> corsMethods, List<String> corsHeaders) {
            configuration.enableCors = true;
            configuration.corsOrigins = corsOrigins;
            configuration.corsHeaders = corsHeaders;
            configuration.corsMethods = corsMethods;
            return this;
        }

        public Builder acceptClientContentType(boolean acceptClientContentType) {
            configuration.acceptClientContentType = acceptClientContentType;
            return this;
        }

        public Builder defaultResponseTypes(List<MediaTypeInfo> defaultType) {
            configuration.defaultResponseTypes.addAll(defaultType);
            return this;
        }

        public Builder allowClientResponseType(boolean allowClientResponseType) {
            configuration.allowClientResponseType = allowClientResponseType;
            return this;
        }

        public Builder defaultContentTypes(List<MediaTypeInfo> defaultContentTypes) {
            configuration.defaultContentTypes.addAll(defaultContentTypes);
            return this;
        }

        public Builder template(String suffix, String prefix) {
            configuration.templateSuffix = suffix;
            configuration.templatePrefix = prefix;
            return this;
        }

        /**
         * use ** to replace path, like **.do, /api/**, /api/**.do
         * NOT: but do not support multi **, like /api/**-controller/**.do
         *
         * @param dispatcherMapping dispatcher mapping
         * @return Builder
         */
        public Builder dispatcherMapping(String dispatcherMapping) {
            int startCount = 0;
            char[] chars = dispatcherMapping.toCharArray();
            for (char c : chars) {
                if (c == '*') {
                    startCount++;
                }
            }

            if (startCount == 2) {
                configuration.dispatcherMapping = dispatcherMapping;
            } else {
                throw new DispatcherMappingFormatException(
                        "##debbie.web.dispatcher-mapping## only support **, like **.do, /api/**, /api/**.do, not support * or multi **");
            }
            return this;
        }

        public MvcConfiguration build() {
            return configuration;
        }
    }
}
