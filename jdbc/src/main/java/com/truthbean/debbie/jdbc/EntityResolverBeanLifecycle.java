package com.truthbean.debbie.jdbc;

import com.truthbean.debbie.bean.BeanLifecycle;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.entity.EntityResolverAware;
import com.truthbean.debbie.jdbc.entity.ResultMap;
import com.truthbean.debbie.jdbc.entity.ResultMapRegister;

import java.util.Collection;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/23 21:01.
 */
public class EntityResolverBeanLifecycle implements BeanLifecycle {

    private final EntityResolver entityResolver;

    public EntityResolverBeanLifecycle(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    @Override
    public boolean support(Class<?> clazz) {
        return EntityResolverAware.class.isAssignableFrom(clazz) ||
                ResultMapRegister.class.isAssignableFrom(clazz);
    }

    @Override
    public <T> T construct(T preparedBean, Object... params) {
        return preparedBean;
    }

    @Override
    public <T> T postConstruct(T bean, Object... params) {
        if (bean instanceof EntityResolverAware entityResolverAware) {
            entityResolverAware.setEntityResolver(entityResolver);
        }
        if (bean instanceof ResultMapRegister register) {
            Collection<ResultMap<?>> maps = register.register();
            if (maps != null && !maps.isEmpty()) {
                for (ResultMap<?> map : maps) {
                    entityResolver.addResultMap(map);
                }
            }
        }
        return bean;
    }
}
