/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.response.provider;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.mvc.response.AbstractResponseContentHandler;
import com.truthbean.debbie.mvc.response.view.AbstractTemplateViewHandler;
import com.truthbean.debbie.mvc.response.view.NoTemplateViewHandler;
import com.truthbean.debbie.reflection.ClassLoaderUtils;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-03 09:59
 */
public enum ResponseContentHandlerProviderEnum {

    /**
     * json restful
     */
    JSON_RESTFUL(new JsonResponseHandler<>()),

    /**
     * xml restful
     */
    XML_RESTFUL(new XmlResponseHandler<>()),

    /**
     * text restful
     */
    TEXT_RESTFUL(new TextResponseHandler<>()),

    /**
     * properties
     */
    PROPERTIES(new PropertiesResponseHandler()),

    /**
     * template view
     */
    TEMPLATE_VIEW(templateViewFilter());

    private final AbstractResponseContentHandler<?, ?> provider;

    ResponseContentHandlerProviderEnum(AbstractResponseContentHandler<?, ?> handlerFilter) {
        this.provider = handlerFilter;
    }

    private static AbstractTemplateViewHandler<?, ?> templateViewFilter() {
        AbstractTemplateViewHandler<?, ?> search;
        var classLoader = ClassLoaderUtils.getClassLoader(ResponseContentHandlerProviderEnum.class);
        ServiceLoader<AbstractTemplateViewHandler> serviceLoader = ServiceLoader.load(AbstractTemplateViewHandler.class, classLoader);
        Iterator<AbstractTemplateViewHandler> handlerIterator = serviceLoader.iterator();
        if (handlerIterator.hasNext()) {
            search = handlerIterator.next();
        } else {
            final Logger logger = LoggerFactory.getLogger(ResponseContentHandlerProviderEnum.class);
            logger.warn("no template handler provider");
            search = new NoTemplateViewHandler();
        }
        return search;
    }

    public static AbstractResponseContentHandler getByResponseType(MediaType responseType) {
        switch (responseType) {
            // bean to json
            case APPLICATION_JSON_UTF8:
            case APPLICATION_JSON:
                return JSON_RESTFUL.provider;
            // bean to xml
            case APPLICATION_XML:
            case APPLICATION_XML_UTF8:
                return XML_RESTFUL.provider;
            // bean.toString() to text
            case TEXT_HTML:
            case TEXT_HTML_UTF8:

            case TEXT_CSS:
            case APPLICATION_JAVASCRIPT:

            case TEXT_PLAIN:
            case TEXT_PLAIN_UTF8:
                return TEXT_RESTFUL.provider;

            default:
                return TEMPLATE_VIEW.provider;
        }
    }

    public AbstractResponseContentHandler<?, ?> getProvider() {
        return provider;
    }
}
