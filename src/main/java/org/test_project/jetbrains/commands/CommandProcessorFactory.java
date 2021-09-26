package org.test_project.jetbrains.commands;

import java.util.Map;

public interface CommandProcessorFactory {
    CommandProcessor createCommandProcessor(Map<String, String> params, Command command);
}
