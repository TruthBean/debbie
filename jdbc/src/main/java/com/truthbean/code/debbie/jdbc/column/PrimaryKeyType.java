package com.truthbean.code.debbie.jdbc.column;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/4/4 23:11.
 */
public enum PrimaryKeyType {
    /**
     * 自动增长
     */
    AUTO_INCREMENT,

    /**
     * uuid
     */
    UUID,

    /**
     * 暂定为其他类型
     */
    OTHER,

    NONE
}
