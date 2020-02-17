package com.truthbean.debbie.event;

import com.truthbean.debbie.bean.BeanFactoryHandler;

/**
 * @author TruthBean
 * @since 0.0.2
 */
public class DebbieStartedEvent extends AbstractDebbieEvent {

    private BeanFactoryHandler beanFactoryHandler;

    /**
     * @see AbstractDebbieEvent#AbstractDebbieEvent(Object)
     *
     * @param source event source
     * @param beanFactoryHandler bean factory handler
     */
    public DebbieStartedEvent(Object source, BeanFactoryHandler beanFactoryHandler) {
        super(source);
        this.beanFactoryHandler = beanFactoryHandler;
    }

    public BeanFactoryHandler getBeanFactoryHandler() {
        return beanFactoryHandler;
    }
}
