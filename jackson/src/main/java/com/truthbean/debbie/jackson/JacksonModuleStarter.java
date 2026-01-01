package com.truthbean.debbie.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.data.transformer.text.jackson.JsonNodeTransformer;
import com.truthbean.debbie.environment.Environment;
import com.truthbean.transformer.DataTransformerCenter;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/01/03 12:14.
 */
public class JacksonModuleStarter implements DebbieModuleStarter {
    @Override
    public boolean enable(Environment environment) {
        boolean enable = false;
        try {
            getClass().getClassLoader().loadClass("com.fasterxml.jackson.databind.JsonNode");
            enable = true;
        } catch (NoClassDefFoundError | ClassNotFoundException ignored) {
        }
        return DebbieModuleStarter.super.enable(environment) && enable;
    }

    @Override
    public int getOrder() {
        return 9;
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        DataTransformerCenter.register(new JsonNodeTransformer(), JsonNode.class, String.class);
    }

    @Override
    public int compareTo(DebbieModuleStarter o) {
        return DebbieModuleStarter.super.compareTo(o);
    }
}
