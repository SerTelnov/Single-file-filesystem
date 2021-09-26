package org.test_project.jetbrains.commands.validation;

import org.junit.jupiter.api.Test;
import org.test_project.jetbrains.commands.CommandParams;

import static org.assertj.core.api.Assertions.assertThatCode;

class ValidationFileFormatRuleTest {

    private final ValidationFileFormatRule validationRule = new ValidationFileFormatRule("file");

    @Test
    void testCorrectParams() {
        assertThatCode(() -> validationRule.validate(CommandParams.of("file", "file.txt")))
                .doesNotThrowAnyException();
    }

    @Test
    void testNoProperty() {
        assertThatCode(() -> validationRule.validate(CommandParams.of("fileeee", "file.txt")))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameters does not have property 'file'");
    }

    @Test
    void testNoFileExtension() {
        assertThatCode(() -> validationRule.validate(CommandParams.of("file", "file")))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter 'file' must have extension");
    }
}
