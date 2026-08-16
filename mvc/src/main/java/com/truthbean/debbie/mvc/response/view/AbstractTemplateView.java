/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.response.view;

import com.truthbean.debbie.mvc.RouterSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A template-based view (e.g. JSP, FreeMarker) carrying model
 * attributes, a redirect flag, and an optional {@link RouterSession}.
 *
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/12 22:00.
 */
public abstract class AbstractTemplateView extends AbstractView {
    /** whether this view triggers a redirect */
    private boolean redirect = false;

    /** model attributes passed to the template */
    private final Map<String, Object> data = new HashMap<>();

    /** the HTTP session associated with this view */
    private RouterSession routerSession;

    /** Sets a single model attribute. */
    public void setAttribute(String name, Object value) {
        data.put(name, value);
    }

    /** Sets multiple model attributes at once. */
    public void setAttributes(Map<String, Object> attributes) {
        if (attributes != null && !attributes.isEmpty()) {
            data.putAll(attributes);
        }
    }

    /** Sets the HTTP session. */
    public void setRouterSession(RouterSession routerSession) {
        this.routerSession = routerSession;
    }

    /** Returns the HTTP session. */
    public RouterSession getRouterSession() {
        return routerSession;
    }

    /** Copies all state (redirect, template, data, session) from the given view. */
    public void from(AbstractTemplateView modelAndView) {
        this.redirect = modelAndView.redirect;
        this.setTemplate(modelAndView.getTemplate());
        this.setSuffix(modelAndView.getSuffix());
        this.setPrefix(modelAndView.getPrefix());
        this.data.putAll(modelAndView.data);
        this.routerSession = modelAndView.routerSession;
    }

    /** Returns an unmodifiable view of the model attributes. */
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(data);
    }

    /** Sets whether this view triggers a redirect. */
    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    /** Returns whether this view triggers a redirect. */
    public boolean isRedirect() {
        return redirect;
    }

    /** Returns {@code true} if the given object is a template view. */
    public static boolean isTemplateView(Object any){
        return any instanceof AbstractTemplateView;
    }
}
