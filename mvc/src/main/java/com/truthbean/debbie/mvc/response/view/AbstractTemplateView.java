/**
 * Copyright (c) 2023 TruthBean(Rogar·Q)
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

    public void setAttributes(Map<String, Object> attributes) {
        if (attributes != null && !attributes.isEmpty()) {
            data.putAll(attributes);
        }
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

    public static boolean isTemplateView(Object any){
        return any instanceof AbstractTemplateView;
    }
}
