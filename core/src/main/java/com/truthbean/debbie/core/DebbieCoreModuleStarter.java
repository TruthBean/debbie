package com.truthbean.debbie.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.truthbean.debbie.bean.BeanFactoryHandler;
import com.truthbean.debbie.bean.BeanInitialization;
import com.truthbean.debbie.boot.DebbieModuleStarter;
import com.truthbean.debbie.data.transformer.ClassInstanceTransformer;
import com.truthbean.debbie.data.transformer.ClassTransformer;
import com.truthbean.debbie.data.transformer.collection.SetStringTransformer;
import com.truthbean.debbie.data.transformer.date.DefaultTimeTransformer;
import com.truthbean.debbie.data.transformer.numeric.BigDecimalToLongTransformer;
import com.truthbean.debbie.data.transformer.numeric.IntegerToBooleanTransformer;
import com.truthbean.debbie.data.transformer.numeric.LongToIntegerTransformer;
import com.truthbean.debbie.data.transformer.text.*;
import com.truthbean.debbie.properties.ClassesScanProperties;
import com.truthbean.debbie.properties.DebbieConfigurationFactory;
import com.truthbean.debbie.properties.PropertiesConfigurationRegister;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;

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
    public void registerBean(BeanFactoryHandler beanFactoryHandler) {
        BeanInitialization beanInitialization = beanFactoryHandler.getBeanInitialization();

        DebbieConfigurationFactory configurationFactory = beanFactoryHandler.getConfigurationFactory();
        configurationFactory.register(ClassesScanProperties.class);

        beanInitialization.addAnnotationRegister(new PropertiesConfigurationRegister());

        registerTransformer(beanInitialization);
    }

    @Override
    public void starter(DebbieConfigurationFactory configurationFactory, BeanFactoryHandler beanFactoryHandler) {
        beanFactoryHandler.refreshBeans();
    }

    private void registerTransformer(BeanInitialization beanInitialization) {
        beanInitialization.registerDataTransformer(new DefaultTimeTransformer(), Long.class, String.class);
        beanInitialization.registerDataTransformer(new DefaultTextTransformer(), Object.class, String.class);
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
        beanInitialization.registerDataTransformer(new ClassInstanceTransformer(), Object.class, String.class);

    }
}
