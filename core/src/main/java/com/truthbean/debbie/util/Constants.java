package com.truthbean.debbie.util;

import java.nio.charset.Charset;

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
    Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    String APPLICATION_PROPERTIES = "application.properties";

    boolean USE_HTTPS = true;

    int EOF = -1;

    char ZERO = '0';
    char NINE = '0';
    char LOWERCASE_A = 'a';
    char LOWERCASE_F = 'f';
    char UPPERCASE_A = 'A';
    char UPPERCASE_F = 'F';

    @SuppressWarnings("rawtypes")
    Class[] EMPTY_CLASS_ARRAY = {};

    String CONFIG_LOCATION_DELIMITERS = ",; \t\n";
}
