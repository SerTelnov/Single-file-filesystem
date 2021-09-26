package org.test_project.jetbrains.commands;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.test_project.jetbrains.IOConsole;
import org.test_project.jetbrains.file_system.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteCommandTest {

    @Mock
    private FileSystem fileSystem;
    @Mock
    private IOConsole console;

    private Command command;

    @BeforeEach
    void setUp() {
        command = new DeleteCommand(fileSystem, console);
    }

    @Test
    void testDeleteFile() throws IOException {
        String fileName = "some-file.txt";
        command.execute(CommandParams.of("file", fileName));
        verify(fileSystem).deleteFile(eq(fileName));
    }

    @Test
    void testDeleteNotExistFile() throws IOException {
        String fileName = "some-file.txt";
        doThrow(new NoSuchFileException(fileName))
                .when(fileSystem).deleteFile(eq(fileName));

        assertThatCode(() -> command.execute(CommandParams.of("file", fileName)))
                .doesNotThrowAnyException();

        verify(console)
                .println(eq("No such file '" + fileName + "'"));
    }

    @Test
    void testValidateParams() {
        assertThatCode(() -> command.validateParams(CommandParams.emptyParams()))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> command.validateParams(CommandParams.of("file", "some-file.txt")))
                .doesNotThrowAnyException();
    }

    @Test
    void testCommandName() {
        assertThat(command.getName())
                .isNotNull();
    }

    @Test
    void testUsage() {
        assertThat(command.getUsage())
                .isNotNull()
                .contains("Example");
    }
}