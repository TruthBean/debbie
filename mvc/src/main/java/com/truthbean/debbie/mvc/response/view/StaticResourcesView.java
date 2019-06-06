package com.truthbean.debbie.mvc.response.view;

import com.truthbean.debbie.io.ResourcesHandler;

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
