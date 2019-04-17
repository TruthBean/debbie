package com.truthbean.debbie.mvc.csrf;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author TruthBean
 * @since 0.0.1
 * @since 2019-03-30 14:22
 */
public class CsrfToken {
    private String token;

    private Timestamp createTime;

    private CsrfToken(String token, Timestamp createTime) {
        this.token = token;
        this.createTime = createTime;
    }

    public static CsrfToken create() {
        return new CsrfToken(UUID.randomUUID().toString(), new Timestamp(System.currentTimeMillis()));
    }

    public String getToken() {
        return token;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CsrfToken)) {
            return false;
        }
        if (token == null) {
            return false;
        }
        CsrfToken csrfToken = (CsrfToken) o;
        return token.equals(csrfToken.getToken());
    }

    @Override
    public int hashCode() {
        if (token == null) {
            return 0;
        }
        return token.hashCode();
    }
}
