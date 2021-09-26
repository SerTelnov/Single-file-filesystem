package org.test_project.jetbrains.commands;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.test_project.jetbrains.IOConsole;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HelpCommandTest {

    @Mock
    private IOConsole console;

    @Test
    void testHelpCommand() {
        Describable mockDescribable1 = Mockito.mock(Describable.class);
        Describable mockDescribable2 = Mockito.mock(Describable.class);

        when(mockDescribable1.getUsage())
                .thenReturn("command1 --param value");
        when(mockDescribable2.getUsage())
                .thenReturn("command2 --other-param other-value");

        HelpCommand command = new HelpCommand(console, List.of(mockDescribable1, mockDescribable2));
        command.execute(CommandParams.emptyParams());

        verify(console).println(eq("" +
                "command1 --param value\n\n" +
                "command2 --other-param other-value"
        ));
    }

    @Test
    void testCorrectInput() {
        HelpCommand helpCommand = new HelpCommand(console, List.of());
        assertThatCode(() -> helpCommand.validateParams(CommandParams.emptyParams()))
                .doesNotThrowAnyException();
        assertThatCode(() -> helpCommand.validateParams(CommandParams.of("some-param", "some-value")))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
