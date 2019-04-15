package com.truthbean.debbie.core.data.transformer.date;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public class DefaultTimeTransformer extends AbstractTimeTransformer {

    /**
     * 时间格式
     */
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public DefaultTimeTransformer() {
        super.setFormat(DATE_TIME_FORMAT);
    }
}