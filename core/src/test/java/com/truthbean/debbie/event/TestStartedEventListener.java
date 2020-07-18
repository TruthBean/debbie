package com.truthbean.debbie.event;

import com.truthbean.Logger;
import com.truthbean.debbie.bean.BeanComponent;
import com.truthbean.debbie.bean.BeanInject;
import com.truthbean.logger.LoggerFactory;

/**
 * @author TruthBean
 * @since 0.0.2
 */
// @EventBeanListener
// @BeanComponent
@TestComponent
public class TestStartedEventListener implements ApplicationListener<TestStartedEvent> {
    private final TestBean testBean;

    public TestStartedEventListener(@BeanInject TestBean testBean) {
        this.testBean = testBean;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TestStartedEventListener.class);

    @Override
    public void onApplicationEvent(TestStartedEvent event) {
        LOGGER.debug(() -> ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> hello debbie  >>>>>>>>>>>>>>>>>>>>>");
        LOGGER.debug(testBean::toString);
    }
}
