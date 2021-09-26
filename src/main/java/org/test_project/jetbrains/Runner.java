package org.test_project.jetbrains;

import org.test_project.jetbrains.commands.CommandProcessor;
import org.test_project.jetbrains.commands.ConsoleCommandParser;

public class Runner {

    private static final String EXIT = "exit";

    private final IOConsole console;
    private final ConsoleCommandParser consoleCommandParser;

    public Runner(IOConsole console, ConsoleCommandParser consoleCommandParser) {
        this.console = console;
        this.consoleCommandParser = consoleCommandParser;
    }

    public void runInConsole() {
        while (true) {
            console.write("> ");
            try {
                String input = console.readLine();
                if (!input.isBlank()) {
                    CommandProcessor commandProcessor = consoleCommandParser.parseCommand(input);
                    if (commandProcessor.getCommandName().equals(EXIT)) {
                        break;
                    }
                    commandProcessor.process();
                }
            } catch (IllegalArgumentException e) {
                console.println(e.getMessage() + "\nTry help");
            } catch (Exception e) {
                console.println(e.getMessage());
            }
        }
    }
}
