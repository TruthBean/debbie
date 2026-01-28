package com.truthbean.debbie.console;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.0
 */
public class DebbieConsoleHelpLineHandler implements DebbieConsoleLineHandler {
    @Override
    public boolean support(String line) {
        return line.equalsIgnoreCase("help");
    }

    @Override
    public String handle(String line) {
        return "help: print this help message";
    }

    @Override
    public String help() {
        return "print this help message";
    }
}
