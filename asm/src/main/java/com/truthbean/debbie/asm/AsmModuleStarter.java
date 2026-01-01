package com.truthbean.debbie.asm;

import com.truthbean.debbie.asm.proxy.AsmBeanProxyHandler;
import com.truthbean.debbie.asm.proxy.AsmGenerated;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.environment.Environment;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/01/10 18:04.
 */
public class AsmModuleStarter implements DebbieModuleStarter {
    @Override
    public boolean enable(Environment environment) {
        return DebbieModuleStarter.super.enable(environment) && environment.getBooleanValue("debbie.asm.enable", true);
    }

    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        beanInfoManager.addIgnoreAnnotation(AsmGenerated.class);
        beanInfoManager.registerBeanLifecycle(new AsmBeanLifecycle(new AsmBeanProxyHandler(applicationContext)));
    }
}
