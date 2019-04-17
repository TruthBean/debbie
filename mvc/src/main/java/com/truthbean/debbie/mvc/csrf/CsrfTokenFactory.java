package com.truthbean.debbie.mvc.csrf;

import com.truthbean.debbie.mvc.request.RouterRequest;

/**
 * @author TruthBean
 * @since 0.0.1
 */
public interface CsrfTokenFactory {

    CsrfToken loadToken(RouterRequest request);

}
