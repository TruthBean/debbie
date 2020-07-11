package com.truthbean.debbie.event;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.logger.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
@EventBeanListener
public class TestStartedEventListener implements ApplicationListener<DebbieStartedEvent> {
    private final TestBean testBean;

    public TestStartedEventListener(@BeanInject TestBean testBean) {
        this.testBean = testBean;
    }

    @Override
    public void onEvent(DebbieStartedEvent event) {
        LOGGER.debug(() -> ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> hello debbie >>>>>>>>>>>>>>>>>>>>>");
        LOGGER.debug(testBean::toString);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TestStartedEventListener.class);

    @Override
    public void onApplicationEvent(DebbieStartedEvent event) {
        LOGGER.debug(() -> ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> hello debbie  >>>>>>>>>>>>>>>>>>>>>");
        LOGGER.debug(testBean::toString);
    }
}
