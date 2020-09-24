/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.annotation;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-24 11:57
 */
public class AnnotationInfo {
    private final Annotation origin;

    private final Map<String, Object> properties = new HashMap<>();

    private final Map<String, AnnotationMethodInfo> methods = new HashMap<>();

    public AnnotationInfo(Annotation origin) {
        this.origin = origin;
    }

    public Annotation getOrigin() {
        return origin;
    }

    public Map<String, Object> properties() {
        return properties;
    }

    public Map<String, AnnotationMethodInfo> methods() {
        return methods;
    }

    @Override
    public String toString() {
        return "{" + "\"origin\":" + origin + ","
                + "\"properties\":" + properties + ","
                + "\"methods\":" + methods + "}";
    }
}
