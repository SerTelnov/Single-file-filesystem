package org.test_project.jetbrains.commands;

public interface Command extends Describable {

    void execute(CommandParams params);

    void validateParams(CommandParams params) throws IllegalArgumentException;
}
