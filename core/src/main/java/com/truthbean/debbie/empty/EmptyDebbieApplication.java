package com.truthbean.debbie.empty;

import com.truthbean.debbie.boot.ApplicationBootContext;
import com.truthbean.debbie.boot.DebbieApplication;
import com.truthbean.debbie.boot.DebbieExitedApplication;
import com.truthbean.debbie.boot.DebbieStartedApplication;
import com.truthbean.debbie.core.ApplicationContext;
import com.truthbean.debbie.internal.DebbieApplicationBootContext;

import java.util.function.Consumer;

/**
 * @author TruthBean
 * @since 0.5.3
 */
public class EmptyDebbieApplication implements DebbieApplication {

    private final ApplicationContext applicationContext = new EmptyApplicationContext();
    private final ApplicationBootContext applicationBootContext = new DebbieApplicationBootContext(applicationContext);

    @Override
    public DebbieStartedApplication start() {
        // applicationContext.refreshBeans();
        return this;
    }

    @Override
    public DebbieExitedApplication exit() {
        applicationContext.release();
        return this;
    }

    @Override
    public DebbieApplication then(Consumer<ApplicationBootContext> applicationBootContextConsumer) {
        applicationBootContextConsumer.accept(applicationBootContext);
        return this;
    }
}
