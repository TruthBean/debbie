package com.truthbean.debbie.console;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.core.ApplicationContext;

import java.util.Set;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.0
 */
public class ConsoleBeanFactory<ConsoleBean> implements BeanFactory<ConsoleBean> {
    @Override
    public ConsoleBean factoryBean(ApplicationContext applicationContext) {
        return null;
    }

    @Override
    public boolean isCreated() {
        return false;
    }

    @Override
    public ConsoleBean getCreatedBean() {
        return null;
    }

    @Override
    public Class<?> getBeanClass() {
        return null;
    }

    @Override
    public Set<String> getAllName() {
        return Set.of();
    }
}
