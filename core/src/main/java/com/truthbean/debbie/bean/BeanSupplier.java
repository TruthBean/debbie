package com.truthbean.debbie.bean;

import com.truthbean.debbie.core.ApplicationContext;

import java.util.function.Supplier;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public interface BeanSupplier<T> {
    Supplier<T> supply(ApplicationContext applicationContext);
}
