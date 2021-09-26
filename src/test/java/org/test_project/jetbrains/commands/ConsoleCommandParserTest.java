package org.test_project.jetbrains.commands;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ConsoleCommandParserTest {

    @Test
    void testParseHelpCommand() {
        Command command = Mockito.mock(Command.class);
        ConsoleCommandParser consoleCommandParser = getConsoleCommandParser(Map.of("help", command));

        CommandProcessor actualCommand = consoleCommandParser.parseCommand("help");
        assertThat(actualCommand.getCommand())
                .isEqualTo(command);
        assertThat(actualCommand.getParams().hasAnyParam()).isFalse();
    }

    @Test
    void testParseCommandWithParams() {
        Command command = Mockito.mock(Command.class);
        ConsoleCommandParser commandParser = getConsoleCommandParser(Map.of("command", command));

        CommandProcessor commandProcessor = commandParser.parseCommand("command --into hello.txt");
        assertThat(commandProcessor.getCommand())
                .isEqualTo(command);
        assertThat(commandProcessor.getParams().containsParam("into")).isTrue();
        assertThat(commandProcessor.getParams().getParam("into")).isEqualTo("hello.txt");
    }

    @Test
    void testNotParamValue() {
        Command command = Mockito.mock(Command.class);
        ConsoleCommandParser commandParser = getConsoleCommandParser(Map.of("command", command));

        Assertions.assertThatCode(() -> commandParser.parseCommand("command --param"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("No value for parameter 'param'");
    }

    @Test
    void testInvalidParamFormat() {
        Command command = Mockito.mock(Command.class);
        ConsoleCommandParser commandParser = getConsoleCommandParser(Map.of("command", command));

        Assertions.assertThatCode(() -> commandParser.parseCommand("command param value"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid parameter format");
    }

    @Test
    void testNoSuchCommand() {
        ConsoleCommandParser commandParser = getConsoleCommandParser(Map.of());

        Assertions.assertThatCode(() -> commandParser.parseCommand("hello"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("No command 'hello'");
    }

    private ConsoleCommandParser getConsoleCommandParser(Map<String, Command> params) {
        return new ConsoleCommandParser(
                params
        );
    }
}
