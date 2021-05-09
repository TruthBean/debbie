/**
 * Copyright (c) 2021 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.mvc;

import java.util.Map;
import java.util.Set;

/**
 * @author TruthBean
 * @since 0.0.1
 * Created on 2019/3/15 22:03.
 */
public interface RouterSession {

    /**
     * Returns a string containing the unique identifier assigned
     * to this session. The identifier is assigned
     * by the servlet container and is implementation dependent.
     *
     * @return id
     */
    String getId();

    /**
     * Returns the time when this session was created, measured
     * in milliseconds since midnight January 1, 1970 GMT.
     *
     * @return create time
     */
    Long getCreateTime();

    /**
     * Returns the last time the client sent a request associated with
     * this session, as the number of milliseconds since midnight
     * January 1, 1970 GMT, and marked by the time the container received the
     * request.
     *
     * <p>Actions that your application takes, such as getting or setting
     * a value associated with the session, do not affect the access
     * time.
     *
     * @return last accessed time
     */
    Long getLastAccessedTime();

    /**
     * Specifies the time, in seconds, between client requests before the
     * servlet container will invalidate this session.  A negative time
     * indicates the session should never timeout.
     *
     * @return max inactive interval
     */
    Long getMaxInactiveInterval();

    /**
     * Returns the object bound with the specified name in this session, or
     * `null` if no object is bound under the name.
     *
     * @param name a string specifying the name of the object
     * @return the object with the specified name
     * @throws IllegalStateException if this method is called on an
     * invalidated session
     */
    Object getAttribute(String name);

    /**
     * Binds an object to this session, using the name specified.
     * If an object of the same name is already bound to the session,
     * the object is replaced.
     *
     * If the value passed in is null, this has the same effect as calling
     * `removeAttribute()`.
     *
     * @param name  the name to which the object is bound;
     * cannot be null
     * @param value the object to be bound
     * An IOFuture containing the previous value
     * @throws IllegalStateException if this method is called on an invalidated session
     */
    void setAttribute(String name, Object value);

    /**
     * Removes the object bound with the specified name from
     * this session. If the session does not have an object
     * bound with the specified name, this method does nothing.
     *
     *
     * After this method executes, and if the object
     * implements `HttpSessionBindingListener`,
     * the container calls
     * `HttpSessionBindingListener.valueUnbound`. The container
     * then notifies any `HttpSessionAttributeListener`s in the web
     * application.
     *
     * @param name                the name of the object to
     * remove from this session
     *
     * @exception IllegalStateException    if this method is called on an
     * invalidated session
     */
    void removeAttribute(String name);

    /**
     * Invalidates this session then unbinds any objects bound
     * to it.
     *
     * @exception IllegalStateException    if this method is called on an
     * already invalidated session
     */
    void invalidate();

    /**
     * Returns an `Set` of `String` objects
     * containing the names of all the objects bound to this session.
     *
     * @return an `Set` of
     * `String` objects specifying the
     * names of all the objects bound to
     * this session
     * @throws IllegalStateException if this method is called on an
     * invalidated session
     */
    Set<String> getAttributeNames();

    Map<String, Object> getAttributes();
}
