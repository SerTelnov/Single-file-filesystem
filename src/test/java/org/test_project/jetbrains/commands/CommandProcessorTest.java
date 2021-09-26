package org.test_project.jetbrains.commands;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class CommandProcessorTest {

    @Test
    void testProcessCommand() {
        CommandParams params = new CommandParams(Map.of("param", "value"));
        Command commandMock = Mockito.mock(Command.class);

        new CommandProcessor(params, commandMock).process();

        verify(commandMock).execute(eq(params));
        verifyNoMoreInteractions(commandMock);
    }

    @Test
    void testProcessInvalidParams() {
        CommandParams params = new CommandParams(Map.of("param", "value"));
        Command commandMock = Mockito.mock(Command.class);
        CommandProcessor processor = new CommandProcessor(params, commandMock);

        doThrow(IllegalArgumentException.class)
                .when(commandMock)
                .execute(eq(params));

        assertThatCode(processor::process)
                .isExactlyInstanceOf(IllegalArgumentException.class);
        Mockito.verify(commandMock)
                .execute(eq(params));
    }
}
