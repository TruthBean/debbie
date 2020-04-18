package com.truthbean.debbie.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.bean.BeanScanConfiguration;
import com.truthbean.debbie.bean.DebbieBeanInfo;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.data.transformer.ClassTransformer;
import com.truthbean.debbie.data.transformer.collection.SetStringTransformer;
import com.truthbean.debbie.data.transformer.date.DefaultTimeTransformer;
import com.truthbean.debbie.data.transformer.jdbc.BlobToByteArrayTransformer;
import com.truthbean.debbie.data.transformer.jdbc.BlobToStringTransformer;
import com.truthbean.debbie.data.transformer.numeric.BigDecimalToLongTransformer;
import com.truthbean.debbie.data.transformer.numeric.IntegerToBooleanTransformer;
import com.truthbean.debbie.data.transformer.numeric.LongToIntegerTransformer;
import com.truthbean.debbie.data.transformer.text.*;
import com.truthbean.debbie.io.ResourceResolver;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.properties.PropertiesConfigurationRegister;
import com.truthbean.debbie.task.DebbieTaskConfigurer;
import com.truthbean.debbie.task.ThreadPooledExecutor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Blob;

/**
 * @author truthbean
 * @since 0.0.2
 */
public class DebbieCoreModuleStarter implements DebbieModuleStarter {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void registerBean(BeanFactoryHandler beanFactoryHandler, BeanInitialization beanInitialization) {
        DebbieBeanInfo<ResourceResolver> beanInfo = new DebbieBeanInfo<>(ResourceResolver.class);
        ResourceResolver resourceResolver = beanFactoryHandler.getResourceResolver();
        beanInfo.setBean(resourceResolver);
        beanInfo.setBeanName("resourceResolver");
        beanInitialization.initSingletonBean(beanInfo);

        beanInitialization.addAnnotationRegister(new PropertiesConfigurationRegister(beanInitialization));

        registerTransformer(beanInitialization);
    }

    @Override
    public void configure(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        configurationFactory.register(ClassesScanProperties.class, BeanScanConfiguration.class);
        new DebbieTaskConfigurer().configure(beanFactoryHandler);
    }

    @Override
    public void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        beanFactoryHandler.refreshBeans();
    }

    @Override
    public void release(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        configurationFactory.reset();

        ThreadPooledExecutor executor = beanFactoryHandler.factory("threadPooledExecutor");
        executor.destroy();
    }

    private void registerTransformer(BeanInitialization beanInitialization) {

        beanInitialization.registerDataTransformer(new DefaultTimeTransformer(), Long.class, String.class);
        beanInitialization.registerDataTransformer(new JsonNodeTransformer(), JsonNode.class, String.class);
        beanInitialization.registerDataTransformer(new UrlTransformer(), URL.class, String.class);

        beanInitialization.registerDataTransformer(new BigDecimalTransformer(), BigDecimal.class, String.class);
        beanInitialization.registerDataTransformer(new BigIntegerTransformer(), BigInteger.class, String.class);
        beanInitialization.registerDataTransformer(new BooleanTransformer(), Boolean.class, String.class);
        beanInitialization.registerDataTransformer(new FloatTransformer(), Float.class, String.class);
        beanInitialization.registerDataTransformer(new IntegerTransformer(), Integer.class, String.class);
        beanInitialization.registerDataTransformer(new LongTransformer(), Long.class, String.class);
        beanInitialization.registerDataTransformer(new ShortTransformer(), Short.class, String.class);

        beanInitialization.registerDataTransformer(new IntegerToBooleanTransformer(), Integer.class, Boolean.class);
        beanInitialization.registerDataTransformer(new LongToIntegerTransformer(), Long.class, Integer.class);
        beanInitialization.registerDataTransformer(new BigDecimalToLongTransformer(), BigDecimal.class, Long.class);

        beanInitialization.registerDataTransformer(new SetStringTransformer());
        beanInitialization.registerDataTransformer(new ClassTransformer(), Class.class, String.class);

        beanInitialization.registerDataTransformer(new BlobToStringTransformer(), Blob.class, String.class);
        beanInitialization.registerDataTransformer(new BlobToByteArrayTransformer(), Blob.class, byte[].class);
    }
}
