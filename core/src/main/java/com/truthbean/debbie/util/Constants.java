/**
 * Copyright (c) 2024 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.util;

import com.truthbean.core.CommonConstants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-18 19:00
 */
public interface Constants extends CommonConstants {

    String HTTP_PROTOCOL = "http";

    String HTTPS_PROTOCOL = "https";

    String CLASSPATH = "classpath:";

    String CLASSPATHS = "classpath*:";

    String QUESTION_MARK = "?";

    String AND_MARK = "&";

    String EQUAL_MARK = "=";

    String SLASH = "//";

    String COLON = ":";

    String SEMICOLON = ";";

    String SPACE = " ";

    String EMPTY_STRING = "";

    String FORM_DATA = "Content-Disposition: form-data;";



    /**
     * Default character set (UTF-8)
     */
    Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    String APPLICATION = "application";
    String DEBBIE_APPLICATION_PROPERTIES = "debbie.application.properties";
    String PROPERTIES = ".properties";
    String APPLICATION_PROPERTIES = "application.properties";
    String APPLICATION_YAML = "application.yaml";
    String YAML = ".yaml";
    String APPLICATION_YML = "application.yml";
    String YML = ".yml";

    boolean USE_HTTPS = true;

    String PROFILES = "application.profiles";

    String CONFIG_LOCATION_DELIMITERS = ",; \t\n";
}
