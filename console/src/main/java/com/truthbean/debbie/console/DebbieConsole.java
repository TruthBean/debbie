package com.truthbean.debbie.console;

import com.truthbean.Console;
import com.truthbean.Logger;
import com.truthbean.LoggerFactory;
import com.truthbean.logger.util.ColorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.0
 */
public class DebbieConsole {

    private final List<DebbieConsoleLineHandler> consoleLineHandlers = new ArrayList<>();

    public void addConsoleLineHandler(DebbieConsoleLineHandler handler) {
        consoleLineHandlers.add(handler);
    }

    public void consoleListener(DebbieConsoleConfig config) {
        final Scanner scanner = new Scanner(System.in);
        Console.writeLine(ColorHelper.GREEN, ColorHelper.BOLD, config.getPrompt() + " Welcome to use Debbie Console.");
        while (true) {
            try {
                Console.write(ColorHelper.GREEN, ColorHelper.BOLD, config.getPrompt());
                String line = scanner.nextLine();
                LOGGER.trace("console input: {}", line);
                if (line == null) {
                    Console.writeLine(ColorHelper.YELLOW, ColorHelper.BOLD, config.getPrompt() + " Illegal input.");
                    continue;
                }
                String[] args = line.split(" ");
                if (args.length == 0) {
                    Console.writeLine(ColorHelper.YELLOW, ColorHelper.BOLD, config.getPrompt() + " Illegal input.");
                    continue;
                }
                if ("exit".equalsIgnoreCase(args[0])) {
                    Console.writeLine(ColorHelper.RED, ColorHelper.BOLD, config.getPrompt() + " Bye.");
                    break;
                }
                for (DebbieConsoleLineHandler handler : consoleLineHandlers) {
                    if (handler.support(line)) {
                        Console.writeLine(ColorHelper.BLUE, ColorHelper.BOLD, handler.handle(line));
                        break;
                    }
                }
            } catch (Exception e) {
                LOGGER.error("console listener error.", e);
            }
        }
    }

    public void clearConsoleLineHandlers() {
        consoleLineHandlers.clear();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DebbieConsole.class);
}
