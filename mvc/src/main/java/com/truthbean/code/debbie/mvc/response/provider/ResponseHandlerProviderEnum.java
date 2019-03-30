package com.truthbean.code.debbie.mvc.response.provider;

import com.truthbean.code.debbie.core.io.MediaType;
import com.truthbean.code.debbie.mvc.response.AbstractResponseHandler;
import com.truthbean.code.debbie.mvc.response.view.AbstractTemplateViewHandler;
import com.truthbean.code.debbie.mvc.response.view.NoTemplateHandlerProviderException;

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
    JSON_RESTFUL(new JsonResponseHandler()),

    /**
     * xml restful
     */
    XML_RESTFUL(new XmlResponseHandler()),

    /**
     * text restful
     */
    TEXT_RESTFUL(new TextResponseHandler()),

    /**
     * template view
     */
    TEMPLATE_VIEW(templateViewFilter());

    private AbstractResponseHandler provider;

    ResponseHandlerProviderEnum(AbstractResponseHandler handlerFilter) {
        this.provider = handlerFilter;
    }

    private static AbstractTemplateViewHandler templateViewFilter() {
        AbstractTemplateViewHandler search;
        ServiceLoader<AbstractTemplateViewHandler> serviceLoader = ServiceLoader.load(AbstractTemplateViewHandler.class);
        Iterator<AbstractTemplateViewHandler> handlerIterator = serviceLoader.iterator();
        if (handlerIterator.hasNext()) {
            search = handlerIterator.next();
        } else {
            throw new NoTemplateHandlerProviderException();
        }
        return search;
    }

    public static AbstractResponseHandler getByResponseType(MediaType responseType) {
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

    public AbstractResponseHandler getProvider() {
        return provider;
    }
}
