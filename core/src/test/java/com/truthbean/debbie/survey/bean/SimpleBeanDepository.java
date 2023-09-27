package com.truthbean.debbie.survey.bean;

import com.truthbean.debbie.bean.BeanRegisterException;
import com.truthbean.debbie.bean.NoBeanException;
import com.truthbean.debbie.core.ApplicationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SimpleBeanDepository implements BeanDepository {

    private final ApplicationContext applicationContext;

    private final ConcurrentMap<String, List<BeanCreator>> depository = new ConcurrentHashMap<>();

    public SimpleBeanDepository(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public BeanCreator register(BeanDefinition beanDefinition) {
        String name = beanDefinition.getName();
        if (depository.containsKey(name)) {
            List<BeanCreator> creators = depository.get(name);
            for (BeanCreator creator : creators) {
                BeanDefinition definition = creator.getBeanDefinition();
                if (definition.getProfile().equals(beanDefinition.getProfile())) {
                    throw new BeanRegisterException("had registered!");
                }
            }
            creators.add(new SimpleBeanCreator(applicationContext, beanDefinition));
        } else {
            List<BeanCreator> creators = new ArrayList<>();
            creators.add(new SimpleBeanCreator(applicationContext, beanDefinition));
            depository.put(name, creators);
        }
        return null;
    }

    @Override
    public BeanCreator getBeanCreator(String profile, String name) {
        if (!depository.containsKey(name)) {
            throw new NoBeanException("no bean with name " + name + " registered");
        }
        List<BeanCreator> creators = depository.get(name);
        for (BeanCreator creator : creators) {
            BeanDefinition beanDefinition = creator.getBeanDefinition();
            if (beanDefinition.getProfile().equals(profile)) {
                return creator;
            }
        }
        throw new NoBeanException("no bean with name " + name + " in the profile " + profile);
    }

    @Override
    public List<BeanCreator> getBeanCreators(String profile, Class<?> beanClass) {
        List<BeanCreator> creators = new ArrayList<>();
        Collection<List<BeanCreator>> values = depository.values();
        for (List<BeanCreator> list : values) {
            for (BeanCreator creator : list) {
                BeanDefinition beanDefinition = creator.getBeanDefinition();
                if (beanDefinition.getBeanClass() == beanClass && beanDefinition.getProfile().equals(profile)) {
                    creators.add(creator);
                }
            }
        }
        return creators;
    }
}
