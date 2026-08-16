/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 * http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.request;

import com.truthbean.debbie.reflection.ExecutableArgument;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * SPI interface for parsing request parameter annotations into
 * {@link RequestParameterInfo} metadata.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.1.0
 * Created on 2020-09-24 10:41
 */
public interface RequestParameterParser {
    /** Parses the given annotation into parameter info; default returns {@code null}. */
    default RequestParameterInfo parse(Annotation annotation) {
        return null;
    }

    /** Parses the given executable argument into parameter info; default returns {@code null}. */
    default RequestParameterInfo parse(ExecutableArgument argument) {
        return null;
    }

    /** Parses the given reflective parameter into parameter info; default returns {@code null}. */
    default RequestParameterInfo parse(Parameter parameter) {
        return null;
    }
}
