package com.truthbean.debbie.mvc.router;

import com.truthbean.debbie.bean.BeanFactory;
import com.truthbean.debbie.bean.BeanLifecycle;
import com.truthbean.debbie.bean.SimpleBeanLifecycle;

/**
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/04 00:13.
 */
public class RouterBeanLifecycle implements BeanLifecycle {

    private final MvcRouterRegister mvcRouterRegister;

    public RouterBeanLifecycle(MvcRouterRegister mvcRouterRegister) {
        this.mvcRouterRegister = mvcRouterRegister;
    }

    @Override
    public boolean support(Class<?> clazz) {
        return CustomizeMvcRouterRegister.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean support(BeanFactory<?> beanFactory) {
        return true;
    }

    @Override
    public <T> T construct(T object, Object... params) {
        return object;
    }

    @Override
    public <T> T postConstruct(T object, Object... params) {
        if (object instanceof CustomizeMvcRouterRegister) {
            ((CustomizeMvcRouterRegister) object).registerMvcRegister(mvcRouterRegister);
        }
        return object;
    }
}
