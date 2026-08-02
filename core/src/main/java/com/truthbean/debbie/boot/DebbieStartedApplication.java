package com.truthbean.debbie.boot;

import java.util.function.Consumer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public interface DebbieStartedApplication {
    /**
     * do after start and before exit
     * @param applicationBootContextConsumer ApplicationBootContext Consumer
     */
    DebbieStartedApplication afterStarted(Consumer<ApplicationBootContext> applicationBootContextConsumer);

    /**
     * exit debbie application
     * @return DebbieExitedApplication
     */
    DebbieExitedApplication exit();

    /**
     * do action before exit debbie application, then exit debbie application
     * @return DebbieExitedApplication
     */
    DebbieExitedApplication exit(Consumer<ApplicationBootContext> applicationBootContextConsumer);

    /**
     * do action before exit debbie application, then force exit debbie application
     * @param applicationBootContextConsumer ApplicationBootContext Consumer
     */
    void forceExit(Consumer<ApplicationBootContext> applicationBootContextConsumer);

    /**
     * force exit debbie application
     */
    void forceExit();
}
