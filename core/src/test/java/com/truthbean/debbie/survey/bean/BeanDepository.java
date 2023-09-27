package com.truthbean.debbie.survey.bean;

import java.util.List;

public interface BeanDepository {

    BeanCreator register(BeanDefinition beanDefinition);

    BeanCreator getBeanCreator(String profile, String name);

    List<BeanCreator> getBeanCreators(String profile, Class<?> beanClass);
}
