package com.truthbean.code.debbie.mvc.response.view;

import com.truthbean.code.debbie.mvc.RouterSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/12 22:00.
 */
public abstract class AbstractTemplateView extends AbstractView {
    private boolean redirect = false;

    private final Map<String, Object> data = new HashMap<>();

    private RouterSession routerSession;

    public void setAttribute(String name, Object value) {
        data.put(name, value);
    }

    public void setRouterSession(RouterSession routerSession) {
        this.routerSession = routerSession;
    }

    public RouterSession getRouterSession() {
        return routerSession;
    }

    public void from(AbstractTemplateView modelAndView) {
        this.redirect = modelAndView.redirect;
        this.setTemplate(modelAndView.getTemplate());
        this.setSuffix(modelAndView.getSuffix());
        this.setPrefix(modelAndView.getPrefix());
        this.data.putAll(modelAndView.data);
        this.routerSession = modelAndView.routerSession;
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(data);
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public boolean isRedirect() {
        return redirect;
    }
}
