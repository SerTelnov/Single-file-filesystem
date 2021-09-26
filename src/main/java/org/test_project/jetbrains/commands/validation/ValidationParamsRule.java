package org.test_project.jetbrains.commands.validation;

import org.test_project.jetbrains.commands.CommandParams;

public interface ValidationParamsRule {
    void validate(CommandParams params);
}
