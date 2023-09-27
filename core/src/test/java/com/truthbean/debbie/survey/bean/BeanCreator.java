package com.truthbean.debbie.survey.bean;

/**
 *
 * @author TruthBean
 * @since 0.5.3
 * Created on 2021/12/03 21:13.
 */
public interface BeanCreator {

    BeanDefinition getBeanDefinition();

    <T> T create(String scope);
}
