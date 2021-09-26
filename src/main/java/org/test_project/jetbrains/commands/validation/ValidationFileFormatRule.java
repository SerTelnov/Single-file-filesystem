package org.test_project.jetbrains.commands.validation;

import org.test_project.jetbrains.commands.CommandParams;

public class ValidationFileFormatRule implements ValidationParamsRule {

    private final String propertyName;

    public ValidationFileFormatRule(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public void validate(CommandParams params) {
        params.getParamSafe(propertyName)
                .map(fileName -> fileName.split("\\."))
                .ifPresentOrElse(split -> {
                            if (split.length < 2) {
                                throw new IllegalArgumentException(
                                        "Parameter '" + propertyName + "' must have extension"
                                );
                            }
                        },
                        () -> {
                            throw new IllegalArgumentException(
                                    "Parameters does not have property '" + propertyName + "'"
                            );
                        }
                );
    }
}
