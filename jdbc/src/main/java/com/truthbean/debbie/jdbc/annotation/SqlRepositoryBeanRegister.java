package com.truthbean.debbie.jdbc.annotation;

import com.truthbean.debbie.bean.ClassBeanInfo;
import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanRegister;
import com.truthbean.debbie.jdbc.entity.EntityResolver;
import com.truthbean.debbie.jdbc.repository.JdbcRepository;
import com.truthbean.debbie.jdbc.repository.DebbieRepositoryFactory;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/07 20:15.
 */
public class SqlRepositoryBeanRegister implements BeanRegister {

    private final EntityResolver entityResolver = EntityResolver.getInstance();

    @Override
    public <Bean> boolean support(ClassBeanInfo<Bean> beanInfo) {
        Class<?> beanClass = beanInfo.getBeanClass();
        List<Type> actualTypes = beanInfo.getActualTypes();
        return JdbcRepository.class.isAssignableFrom(beanClass)
                && actualTypes.size() == 2 && actualTypes.get(0) != Object.class
                && support(beanInfo, SqlRepository.class);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <Bean> BeanFactory<Bean> getBeanFactory(ClassBeanInfo<Bean> beanInfo) {
        return new DebbieRepositoryFactory(beanInfo, entityResolver);
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
