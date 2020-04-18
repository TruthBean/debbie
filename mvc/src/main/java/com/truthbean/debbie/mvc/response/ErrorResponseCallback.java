package com.truthbean.debbie.mvc.response;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.0.2
 * Created on 2020-03-22 22:02
 */
@FunctionalInterface
public interface ErrorResponseCallback {

    void callback(RouterResponse response);

}
