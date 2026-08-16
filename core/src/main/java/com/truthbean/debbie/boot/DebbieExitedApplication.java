package com.truthbean.debbie.boot;

import java.util.function.Consumer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public interface DebbieExitedApplication {
    DebbieStartedApplication start();

    /**
     * do action after started application and before exit the application
     * @param applicationBootContextConsumer ApplicationBootContext Consumer
     * @return DebbieStartedApplication
     */
    DebbieStartedApplication afterStarted(Consumer<ApplicationBootContext> applicationBootContextConsumer);
}
