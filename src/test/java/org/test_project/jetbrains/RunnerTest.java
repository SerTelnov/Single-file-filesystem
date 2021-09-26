package org.test_project.jetbrains;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.test_project.jetbrains.commands.CommandProcessor;
import org.test_project.jetbrains.commands.ConsoleCommandParser;
import org.test_project.jetbrains.commands.ExitCommand;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RunnerTest {

    @Mock
    private IOConsole consoleMock;
    @Mock
    private ConsoleCommandParser commandParserMock;
    @Mock
    private CommandProcessor commandProcessorMock;

    @Test
    void testRunInConsole() throws IOException {
        Runner runner = new Runner(consoleMock, commandParserMock);

        when(consoleMock.readLine())
                .thenReturn("\t\t", "hello", "exit");

        when(commandParserMock.parseCommand(eq("hello")))
                .thenReturn(commandProcessorMock);
        when(commandParserMock.parseCommand(eq("exit")))
                .thenReturn(new CommandProcessor(Map.of(), new ExitCommand()));

        when(commandProcessorMock.getCommandName())
                .thenReturn("hello");
        doThrow(IllegalArgumentException.class)
                .when(commandProcessorMock)
                .process();

        runner.runInConsole();

        verify(consoleMock, times(3))
                .write("> ");

        verify(commandParserMock, times(2))
                .parseCommand(any());

        verify(consoleMock, times(1))
                .println(argThat(s -> s.endsWith("Try help")));
    }
}