package org.test_project.jetbrains.commands;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConsoleCommandParser {

    private static final String WHITESPACE_REGEX = "\\s+";
    private final Map<String, Command> commands;

    public ConsoleCommandParser(Map<String, Command> commands) {
        this.commands = new HashMap<>(commands);
    }

    public CommandProcessor parseCommand(String userInput) {
        userInput = userInput.trim();
        String[] split = userInput.split(WHITESPACE_REGEX);
        return new CommandProcessor(extractParams(split), getCommandChecked(split[0]));
    }

    private Command getCommandChecked(String commandName) {
        if (!commands.containsKey(commandName)) {
            throw new IllegalArgumentException("No command '" + commandName + "'");
        }
        return commands.get(commandName);
    }

    private Map<String, String> extractParams(String[] split) {
        Map<String, String> params = new HashMap<>();

        for (int i = 1; i < split.length; i += 2) {
            String rawParam = split[i];

            if (!rawParam.startsWith("--")) {
                throw new IllegalArgumentException("Invalid parameter format");
            }
            String param = rawParam.substring(2);

            if (i + 1 >= split.length) {
                throw new IllegalArgumentException("No value for parameter '" + param + "'");
            }
            params.put(param, split[i + 1]);
        }
        return params;
    }
}
