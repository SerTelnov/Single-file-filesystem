package org.test_project.jetbrains.commands;

public class ExitCommand implements Command {

    @Override
    public void execute(CommandParams params) {
        throw new IllegalStateException();
    }

    @Override
    public void validateParams(CommandParams params) {
        throw new IllegalStateException();
    }

    @Override
    public String getUsage() {
        throw new IllegalStateException();
    }

    @Override
    public String getName() {
        return "exit";
    }
}
