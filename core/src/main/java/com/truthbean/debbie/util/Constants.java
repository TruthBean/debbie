/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2018-02-18 19:00
 */
public interface Constants {

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

    String TRUE = "true";

    String FALSE = "false";

    /**
     * Default character set (UTF-8)
     */
    Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    String APPLICATION_PROPERTIES = "application.properties";
    String APPLICATION_YAML = "application.yaml";
    String APPLICATION_YML = "application.yml";

    boolean USE_HTTPS = true;

    int EOF = -1;

    char ZERO = '0';
    char NINE = '9';
    char LOWERCASE_A = 'a';
    char LOWERCASE_F = 'f';
    char UPPERCASE_A = 'A';
    char UPPERCASE_F = 'F';

    Class<?>[] EMPTY_CLASS_ARRAY = {};

    String CONFIG_LOCATION_DELIMITERS = ",; \t\n";
}
