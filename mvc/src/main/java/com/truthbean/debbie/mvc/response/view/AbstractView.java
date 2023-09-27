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

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/12 22:13.
 */
public abstract class AbstractView {
    private String template = "index";

    private String suffix = ".ext";

    private String prefix = "classpath*:template/";

    private boolean text = true;

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isText() {
        return text;
    }

    public void setText(boolean text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "{" + "\"template\":\"" + template + "\"" + "," + "\"suffix\":\"" + suffix + "\"" + "," + "\"prefix\":\"" + prefix + "\"" + "," + "\"text\":" + text + "}";
    }

    public String getLocation() {
        return getPrefix() + getTemplate() + getSuffix();
    }

    /**
     * render view to target type
     * @return any type
     */
    public abstract Object render();
}
