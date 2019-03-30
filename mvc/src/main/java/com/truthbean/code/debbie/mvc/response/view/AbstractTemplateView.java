package com.truthbean.code.debbie.mvc.response.view;

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

    public void setAttribute(String name, Object value) {
        data.put(name, value);
    }

    public void from(AbstractTemplateView modelAndView) {
        this.redirect = modelAndView.redirect;
        this.setTemplate(modelAndView.getTemplate());
        this.setSuffix(modelAndView.getSuffix());
        this.setPrefix(modelAndView.getPrefix());
        this.data.putAll(modelAndView.data);
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(data);
    }

    public boolean isRedirect() {
        return redirect;
    }
}
