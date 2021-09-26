package org.test_project.jetbrains.commands;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.NoSuchFileException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.test_project.jetbrains.IOConsole;
import org.test_project.jetbrains.file_system.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadCommandTest {

    @Mock
    private FileSystem fileSystemMock;
    @Mock
    private IOConsole consoleMock;
    @Mock
    private FileIOUtils ioStreamMock;

    private ReadCommand command;

    @BeforeEach
    void setup() {
        command = new ReadCommand(fileSystemMock, ioStreamMock, consoleMock);
    }

    @Test
    void testReadFileContentIntoSystemOut() throws IOException {
        String content = "Hello, Reader!";
        when(fileSystemMock.readFile("hello.txt"))
                .thenReturn(content.getBytes());

        command.execute(CommandParams.of("file", "hello.txt"));
        verify(consoleMock)
                .write(eq(content));
    }

    @Test
    void testReadFileContentIntoOutStream() throws IOException {
        String fileName = "hello.txt";
        String dstPath = "destination.txt";
        String content = "Hello, Reader! Again...";

        doAnswer(invocation -> {
            OutputStream os = invocation.getArgument(1);
            os.write(content.getBytes());
            return null;
        }).when(fileSystemMock)
                .readFile(eq(fileName), any());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(ioStreamMock.getAsOutputStream(eq(dstPath)))
                .thenReturn(out);

        command.execute(new CommandParams(Map.of("file", "hello.txt", "destination", dstPath)));
        assertThat(out).hasToString(content);
    }

    @Test
    void testReadNotExistFile() throws IOException {
        String fileName = "hello.txt";
        doThrow(new NoSuchFileException(fileName))
                .when(fileSystemMock).readFile(eq(fileName));

        command.execute(CommandParams.of("file", fileName));

        verify(consoleMock).println(eq("No such file '" + fileName + "'"));
    }

    @Test
    void testReadImageFile() throws IOException {
        String dstPath = "destination.png";
        String fileName = "image.png";
        byte[] imgBytes = "Hello".getBytes();

        when(fileSystemMock.readFile(eq(fileName)))
                .thenReturn(imgBytes);

        command.execute(new CommandParams(Map.of(
                "file", fileName,
                "destination", dstPath
        )));

        verify(ioStreamMock)
                .writeAsImage(eq(dstPath), eq(imgBytes));
    }

    @Test
    void testValidateParams() {
        assertThatCode(() -> command.validateParams(CommandParams.emptyParams()))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> command.validateParams(CommandParams.of("file", "some-file.txt")))
                .doesNotThrowAnyException();

        assertThatCode(() -> command.validateParams(
                new CommandParams(Map.of(
                        "file", "some-file.txt",
                        "destination", "some/path/dst"
                ))
        )).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter 'destination' must have extension");

        assertThatCode(() -> command.validateParams(
                new CommandParams(Map.of(
                        "file", "some-file.txt",
                        "destination-path", "some/path/dst.txt"
                ))
        )).doesNotThrowAnyException();

        assertThatCode(() -> command.validateParams(CommandParams.of("file", "image.png")))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Add 'destination' parameter for binary file");
    }

    @Test
    void testCommandName() {
        assertThat(command.getName()).isNotNull();
    }

    @Test
    void testCommandUsage() {
        assertThat(command.getUsage())
                .startsWith("read")
                .contains("Example");
    }
}
