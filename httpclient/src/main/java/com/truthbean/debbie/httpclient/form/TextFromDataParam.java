package com.truthbean.debbie.httpclient.form;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-02-24 14:28
 */
public class TextFromDataParam extends FormDataParamName {
    private String value;
    private String charset;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
