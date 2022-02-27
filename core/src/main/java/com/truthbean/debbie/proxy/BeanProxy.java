package com.truthbean.debbie.proxy;

import java.util.function.Supplier;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/01/03 14:19.
 */
public interface BeanProxy<T> {

    default T proxy(T bean) {
        return proxy(() -> bean);
    }

    T proxy(Supplier<T> bean);
}
