package com.truthbean.debbie.mvc.response.provider;

import com.truthbean.debbie.core.io.MediaType;
import com.truthbean.debbie.mvc.response.AbstractResponseContentHandler;
import com.truthbean.debbie.mvc.response.view.AbstractTemplateViewHandler;
import com.truthbean.debbie.mvc.response.view.NoTemplateViewHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-03-03 09:59
 */
public enum ResponseHandlerProviderEnum {

    /**
     * json restful
     */
    JSON_RESTFUL(new JsonResponseContentHandler()),

    /**
     * xml restful
     */
    XML_RESTFUL(new XmlResponseContentHandler()),

    /**
     * text restful
     */
    TEXT_RESTFUL(new TextResponseContentHandler()),

    /**
     * template view
     */
    TEMPLATE_VIEW(templateViewFilter());

    private AbstractResponseContentHandler provider;

    ResponseHandlerProviderEnum(AbstractResponseContentHandler handlerFilter) {
        this.provider = handlerFilter;
    }

    private static AbstractTemplateViewHandler templateViewFilter() {
        AbstractTemplateViewHandler search;
        ServiceLoader<AbstractTemplateViewHandler> serviceLoader = ServiceLoader.load(AbstractTemplateViewHandler.class);
        Iterator<AbstractTemplateViewHandler> handlerIterator = serviceLoader.iterator();
        if (handlerIterator.hasNext()) {
            search = handlerIterator.next();
        } else {
            final Logger logger = LoggerFactory.getLogger(ResponseHandlerProviderEnum.class);
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

    public AbstractResponseContentHandler getProvider() {
        return provider;
    }
}
