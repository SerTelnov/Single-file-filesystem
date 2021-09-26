package org.test_project.jetbrains.commands;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.test_project.jetbrains.IOConsole;
import org.test_project.jetbrains.file_system.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.test_project.jetbrains.IOUtils.asInputStream;

@ExtendWith(MockitoExtension.class)
class CreateCommandTest {

    @Mock
    private FileSystem fileSystem;
    @Mock
    private FileIOUtils fileStreams;
    @Mock
    private IOConsole console;

    private CreateCommand command;

    @BeforeEach
    private void setUp() {
        command = new CreateCommand(fileSystem, fileStreams, console);
    }

    @Test
    void testReadInputTextAndWrite() throws IOException {
        String content = "Hello, file system, from console reader!";
        String fileName = "hello.txt";
        String sourcePath = "some/path/file.txt";

        InputStream is = asInputStream(content);
        when(fileStreams.getAsInputStream(eq(sourcePath)))
                .thenReturn(is);

        CommandParams params = new CommandParams(Map.of("file", fileName, "source", sourcePath));
        command.execute(params);

        verify(fileSystem)
                .createFile(eq(fileName), eq(is));
    }

    @Test
    void testCreateImageFile() throws IOException {
        String sourcePath = "imgs/landscape.jpg";
        String fileName = "landscape.jpg";

        CommandParams params = new CommandParams(Map.of("file", fileName, "source", sourcePath));
        command.execute(params);

        verify(fileStreams)
                .getAsImageInputStream(eq(sourcePath));
        verify(fileSystem)
                .createFile(eq(fileName), any());
    }

    @Test
    void testCreateAlreadyExistsFile() throws IOException {
        String fileName = "some-file.txt";
        doThrow(new FileAlreadyExistsException(fileName))
                .when(fileSystem).createFile(eq(fileName), any());

        CommandParams params = new CommandParams(Map.of("file", fileName, "source", "some-input.txt"));
        command.execute(params);
        verify(console)
                .println(eq("File '" + fileName + "' already exists"));
    }

    @Test
    void testExecuteWithInvalidParams() {
        assertThatCode(() -> command.execute(CommandParams.emptyParams()))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testCorrectParams() {
        CommandParams params = new CommandParams(Map.of("file", "file-name.txt", "source", "some-path.txt"));
        assertThatCode(() -> command.validateParams(params))
                .doesNotThrowAnyException();
    }

    @MethodSource("incorrectParamArgs")
    @ParameterizedTest
    void testIncorrectParams(Map<String, String> params) {
        assertThatCode(() -> command.validateParams(new CommandParams(params)))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    static Stream<Arguments> incorrectParamArgs() {
        return Stream.of(
                Arguments.of(Map.of("fiiiile", "")),
                Arguments.of(Map.of("file", "")),
                Arguments.of(Map.of("file0", "file", "file1", "file")),
                Arguments.of(Map.of("file", "value", "other-file", "value")),
                Arguments.of(Map.of("file", "file-name", "source", "some-path")),
                Arguments.of(Map.of("file", "file.txt", "source", "some-path"))
        );
    }

    @Test
    void testCommandName() {
        assertThat(command.getName())
                .isNotNull()
                .isEqualTo("create");
    }

    @Test
    void testCommandUsage() {
        assertThat(command.getUsage())
                .isNotEmpty()
                .contains("Example: ");
    }
}
