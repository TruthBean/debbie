package com.truthbean.debbie.boot;

import java.util.function.Consumer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public interface DebbieStartedApplication {
    /**
     * do after start and before exit
     */
    DebbieStartedApplication afterStarted(Consumer<ApplicationBootContext> applicationBootContextConsumer);

    DebbieExitedApplication exit();
}
