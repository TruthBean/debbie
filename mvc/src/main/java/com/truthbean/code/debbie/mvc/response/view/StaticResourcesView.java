package com.truthbean.code.debbie.mvc.response.view;

import com.truthbean.code.debbie.core.io.ResourcesHandler;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-18 19:43
 */
public class StaticResourcesView extends AbstractView {

    public StaticResourcesView() {
        setText(true);
        setSuffix(".html");
        setPrefix("classpath*:statics/");
    }

    @Override
    public Object render() {
        if (isText()) {
            return ResourcesHandler.handleStaticResource(getLocation());
        } else {
            return ResourcesHandler.handleStaticBytesResource(getLocation());
        }
    }
}
