package com.truthbean.debbie.console;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.0
 */
public interface DebbieConsoleLineHandler {
    /**
     * @param line the line to handle
     * @return true if the line is supported, false otherwise
     */
    boolean support(String line);

    /**
     * handle the line
     *
     * @param line the line to handle
     */
    String handle(String line);

    /**
     * @return the help message
     */
    String help();
}
