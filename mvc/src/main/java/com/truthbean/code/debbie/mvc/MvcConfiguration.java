package com.truthbean.code.debbie.mvc;

import com.truthbean.code.debbie.core.bean.BeanScanConfiguration;
import com.truthbean.code.debbie.core.io.MediaType;
import com.truthbean.code.debbie.core.util.CollectionUtils;
import com.truthbean.code.debbie.mvc.request.HttpMethod;

import java.util.*;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/2/25 22:08.
 */
public class MvcConfiguration extends BeanScanConfiguration {
    /**
     * default response type
     */
    private final Set<MediaType> defaultTypes = new HashSet<>();

    // cors
    private boolean cors = false;
    private List<String> corsOrigins;
    private List<HttpMethod> corsMethods;
    private List<String> corsHeaders;

    private String templateSuffix;
    private String templatePrefix;

    public MvcConfiguration() {
    }

    public MvcConfiguration(MvcConfiguration configuration) {
        copyFrom(configuration);
    }

    public void copyFrom(MvcConfiguration configuration) {
        this.defaultTypes.addAll(configuration.defaultTypes);

        this.cors = configuration.cors;
        this.corsOrigins = configuration.corsOrigins;
        this.corsMethods = configuration.corsMethods;
        this.corsHeaders = configuration.corsHeaders;

        this.templatePrefix = configuration.templatePrefix;
        this.templateSuffix = configuration.templateSuffix;
    }

    public boolean isCors() {
        return cors;
    }

    protected void setCors(boolean cors) {
        this.cors = cors;
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
        return (templateSuffix == null || "".equals(templateSuffix)) &&
                (templatePrefix == null || "".equals(templatePrefix));
    }

    public Map<String, String> getCors() {
        if (cors) {
            Map<String, String> map = new HashMap<>();
            map.put("Access-Control-Allow-Origin", CollectionUtils.splitList(corsOrigins));
            map.put("Access-Control-Allow-Methods", CollectionUtils.splitList(corsHeaders));
            map.put("Access-Control-Allow-Headers", CollectionUtils.splitList(corsMethods));
            return map;
        } else {
            return null;
        }
    }

    public Set<MediaType> getDefaultTypes() {
        return defaultTypes;
    }

    public static Builder builder() {
        return new Builder(new MvcConfiguration());
    }

    public static final class Builder {
        private MvcConfiguration configuration;

        private Builder(MvcConfiguration configuration) {
            this.configuration = configuration;
        }

        public Builder cors() {
            configuration.cors = true;
            configuration.corsOrigins = Collections.singletonList("*");
            return this;
        }

        public Builder cors(List<String> corsOrigins, List<HttpMethod> corsMethods, List<String> corsHeaders) {
            configuration.cors = true;
            configuration.corsOrigins = corsOrigins;
            configuration.corsHeaders = corsHeaders;
            configuration.corsMethods = corsMethods;
            return this;
        }

        public Builder defaultTypes(List<MediaType> defaultType) {
            configuration.defaultTypes.addAll(defaultType);
            return this;
        }

        public Builder template(String suffix, String prefix) {
            configuration.templateSuffix = suffix;
            configuration.templatePrefix = prefix;
            return this;
        }

        public MvcConfiguration build() {
            return configuration;
        }
    }
}
