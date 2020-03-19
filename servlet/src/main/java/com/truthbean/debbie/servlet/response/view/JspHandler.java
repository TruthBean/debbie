package com.truthbean.debbie.servlet.response.view;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;
import com.truthbean.debbie.mvc.response.view.AbstractTemplateViewHandler;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/10 21:13.
 */
public class JspHandler extends AbstractTemplateViewHandler {

    @Override
    public Object transform(Object o) {
        // do nothing
        return o;
    }

    @Override
    public MediaTypeInfo getResponseType() {
        return MediaType.TEXT_HTML_UTF8.info();
    }
}