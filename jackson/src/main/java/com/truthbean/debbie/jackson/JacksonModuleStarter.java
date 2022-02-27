package com.truthbean.debbie.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.truthbean.debbie.bean.BeanComponentParser;
import com.truthbean.debbie.bean.BeanInfoManager;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.data.transformer.text.jackson.JsonNodeTransformer;
import com.truthbean.debbie.env.EnvironmentContent;
import com.truthbean.transformer.DataTransformerCenter;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author TruthBean
 * @since 0.5.4
 * Created on 2022/01/03 12:14.
 */
public class JacksonModuleStarter implements DebbieModuleStarter {
    @Override
    public boolean enable(EnvironmentContent envContent) {
        boolean enable = false;
        try {
            getClass().getClassLoader().loadClass("com.fasterxml.jackson.databind.JsonNode");
            enable = true;
        } catch (NoClassDefFoundError | ClassNotFoundException ignored) {
        }
        return DebbieModuleStarter.super.enable(envContent) && enable;
    }

    @Override
    public int getOrder() {
        return 9;
    }

    @Override
    public Map<Class<? extends Annotation>, BeanComponentParser> getComponentAnnotation() {
        return DebbieModuleStarter.super.getComponentAnnotation();
    }

    @Override
    public void registerBean(ApplicationContext applicationContext, BeanInfoManager beanInfoManager) {
        DataTransformerCenter.register(new JsonNodeTransformer(), JsonNode.class, String.class);
    }

    @Override
    public void configure(ApplicationContext applicationContext) {
        DebbieModuleStarter.super.configure(applicationContext);
    }

    @Override
    public void starter(ApplicationContext applicationContext) {
        DebbieModuleStarter.super.starter(applicationContext);
    }

    @Override
    public void postStarter(ApplicationContext applicationContext) {
        DebbieModuleStarter.super.postStarter(applicationContext);
    }

    @Override
    public void release(ApplicationContext applicationContext) {
        DebbieModuleStarter.super.release(applicationContext);
    }

    @Override
    public int compareTo(DebbieModuleStarter o) {
        return DebbieModuleStarter.super.compareTo(o);
    }
}
