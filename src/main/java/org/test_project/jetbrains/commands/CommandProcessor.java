package org.test_project.jetbrains.commands;

import java.util.Map;
import java.util.Objects;

public class CommandProcessor {

    private final CommandParams params;
    private final Command command;

    public CommandProcessor(Map<String, String> params, Command command) {
        this(new CommandParams(params), command);
    }

    public CommandProcessor(CommandParams params, Command command) {
        this.params = params;
        this.command = Objects.requireNonNull(command);
    }

    public CommandParams getParams() {
        return params;
    }

    public Command getCommand() {
        return command;
    }

    public String getCommandName() {
        return command.getName();
    }

    public void process() {
        command.execute(params);
    }
}
