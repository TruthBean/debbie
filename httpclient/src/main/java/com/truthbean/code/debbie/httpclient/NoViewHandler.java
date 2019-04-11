package com.truthbean.code.debbie.httpclient;

import com.truthbean.code.debbie.mvc.response.view.AbstractTemplateViewHandler;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class NoViewHandler extends AbstractTemplateViewHandler {
    @Override
    public Object transform(Object o) {
        return null;
    }
}
