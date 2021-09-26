package org.test_project.jetbrains.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.test_project.jetbrains.IOConsole;

public class HelpCommand implements Command {

    private final IOConsole console;
    private final List<Describable> describable;

    public HelpCommand(IOConsole console, List<Describable> describable) {
        this.console = console;
        this.describable = describable;
    }

    @Override
    public void execute(CommandParams params) {
        String usages = collectUsage();
        console.println(usages);
    }

    private String collectUsage() {
        return describable.stream()
                .map(Describable::getUsage)
                .collect(Collectors.joining("\n\n"));
    }

    @Override
    public void validateParams(CommandParams params) {
        if (params.hasAnyParam()) {
            throw new IllegalArgumentException("Command help do not need any parameters");
        }
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage() {
        throw new UnsupportedOperationException();
    }
}
