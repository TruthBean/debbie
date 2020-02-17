package com.truthbean.debbie.mvc.response.view;

import com.truthbean.debbie.io.MediaType;
import com.truthbean.debbie.io.MediaTypeInfo;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class NoTemplateViewHandler extends AbstractTemplateViewHandler<Object, Object> {
    @Override
    public Object transform(Object o) {
        return null;
    }

    @Override
    public MediaTypeInfo getResponseType() {
        return MediaType.ANY.info();
    }
}
