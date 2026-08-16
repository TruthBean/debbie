/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc.csrf;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * An immutable CSRF token consisting of a random UUID string and a
 * creation timestamp.
 *
 * @author TruthBean
 * @since 0.0.1
 * @since 2019-03-30 14:22
 */
public class CsrfToken {
    /** the token string */
    private final String token;

    /** when the token was created */
    private final Timestamp createTime;

    private CsrfToken(String token, Timestamp createTime) {
        this.token = token;
        this.createTime = createTime;
    }

    /** Creates a new {@link CsrfToken} with a random UUID and the current time. */
    public static CsrfToken create() {
        return new CsrfToken(UUID.randomUUID().toString(), new Timestamp(System.currentTimeMillis()));
    }

    /** Returns the token string. */
    public String getToken() {
        return token;
    }

    /** Returns the creation timestamp. */
    public Timestamp getCreateTime() {
        return createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CsrfToken csrfToken)) {
            return false;
        }
        if (token == null) {
            return false;
        }
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
