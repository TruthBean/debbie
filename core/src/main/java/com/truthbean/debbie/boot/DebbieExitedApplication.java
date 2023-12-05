package com.truthbean.debbie.boot;

import java.util.function.Consumer;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.5.5
 */
public interface DebbieExitedApplication {
    DebbieApplication then(Consumer<ApplicationBootContext> applicationBootContextConsumer);

    DebbieStartedApplication start();
}
