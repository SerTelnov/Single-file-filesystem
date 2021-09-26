package org.test_project.jetbrains.file_system;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.test_project.jetbrains.IOUtils.asInputStream;
import static org.test_project.jetbrains.IOUtils.readInputStream;
import static org.test_project.jetbrains.IOUtils.readJPGInputStream;
import static org.test_project.jetbrains.file_system.FileAssertions.assertFileContent;
import static org.test_project.jetbrains.file_system.FileAssertions.assertInitFile;
import static org.test_project.jetbrains.file_system.FileAssertions.assertUncheckedContentEquals;

class MainFileTest {

    private static final String ROOT = "test/resources/";
    public static final String FILE_SYSTEM_FILE_PATH = ROOT + "main-file";
    private static final String FILE_SYSTEM_FILE_PATH_WITH_DIRECTORIES = ROOT + "test-project/filesystem/main-file";
    private static final String SIMPLE_FILE_NAME = "simple_file.txt";

    @AfterEach
    void cleanUpFiles() throws IOException {
        Files.deleteIfExists(Paths.get(FILE_SYSTEM_FILE_PATH));
        Files.deleteIfExists(Paths.get(FILE_SYSTEM_FILE_PATH_WITH_DIRECTORIES));
    }

    @Test
    void testCreateMainFile() {
        new MainFile(FILE_SYSTEM_FILE_PATH);
        assertInitFile(Paths.get(FILE_SYSTEM_FILE_PATH));
    }

    @Test
    void testCreateMainFileWithDirectories() {
        new MainFile(FILE_SYSTEM_FILE_PATH_WITH_DIRECTORIES);
        assertInitFile(Paths.get(FILE_SYSTEM_FILE_PATH_WITH_DIRECTORIES));
    }

    @Test
    void testExistFileOnEmptySystem() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);
        assertThat(mainFile.existsFile("some_file.txt")).isFalse();

        mainFile.createFile("some_file.txt", asInputStream("some content"));
        assertThat(mainFile.existsFile("some_file.txt")).isTrue();
    }

    @Test
    void testAlreadyExistFile() throws IOException {
        final Path path = Paths.get(FILE_SYSTEM_FILE_PATH);
        if (Files.exists(path)) {
            cleanUpFiles();
        }
        Files.createFile(path);

        assertThatCode(() -> new MainFile(FILE_SYSTEM_FILE_PATH))
                .doesNotThrowAnyException();
        assertInitFile(path);
    }

    @Test
    void testReadContent() throws IOException {
        MainFile mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);
        final String content = "Hello, my file system!";
        mainFile.createFile(SIMPLE_FILE_NAME, asInputStream(content));

        final byte[] actualBytes = mainFile.readFile(SIMPLE_FILE_NAME);
        assertThat(new String(actualBytes, StandardCharsets.UTF_8))
                .isEqualTo(content);
    }

    @Test
    void testReadConcreteFile() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);
        mainFile.createFile("file01.txt", asInputStream("First file content"));
        mainFile.createFile("file02.txt", asInputStream("Second file content"));

        assertThat(new String(mainFile.readFile("file01.txt"), StandardCharsets.UTF_8))
                .isEqualTo("First file content");
        assertThat(new String(mainFile.readFile("file02.txt"), StandardCharsets.UTF_8))
                .isEqualTo("Second file content");
        assertThat(new String(mainFile.readFile("file01.txt"), StandardCharsets.UTF_8))
                .isEqualTo("First file content");
    }

    @Test
    void testReadImage() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);

        mainFile.createFile("parrot.jpg", readJPGInputStream("imgs/parrot.jpg"));

        byte[] bytes = mainFile.readFile("parrot.jpg");

        InputStream expected = readJPGInputStream("imgs/parrot.jpg");
        InputStream actual = new ByteArrayInputStream(bytes);

        assertUncheckedContentEquals(actual, expected);
    }

    @Test
    void testReadNotExistFile() {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);

        assertThatCode(() -> mainFile.readFile(SIMPLE_FILE_NAME))
                .isExactlyInstanceOf(NoSuchFileException.class)
                .hasMessage(SIMPLE_FILE_NAME);

        assertThatCode(() -> mainFile.readFile(SIMPLE_FILE_NAME, null))
                .isExactlyInstanceOf(NoSuchFileException.class)
                .hasMessage(SIMPLE_FILE_NAME);
    }

    @Test
    void testReadImageAndText() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);

        mainFile.createFile("parrot.jpg", readJPGInputStream("imgs/parrot.jpg"));
        mainFile.createFile("hello.txt", asInputStream("Hello, file system!"));

        String actualText = new String(mainFile.readFile("hello.txt"), StandardCharsets.UTF_8);
        assertThat(actualText)
                .isEqualTo("Hello, file system!");

        InputStream actualImg = new ByteArrayInputStream(mainFile.readFile("parrot.jpg"));
        InputStream expectedImg = readJPGInputStream("imgs/parrot.jpg");
        assertUncheckedContentEquals(actualImg, expectedImg);
    }

    @Test
    void testReadIntoOutputStream() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);

        mainFile.createFile("pushkin.txt", readInputStream("text/pushkin.txt"));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        mainFile.readFile("pushkin.txt", os);

        assertUncheckedContentEquals(
                readInputStream("text/pushkin.txt"),
                new ByteArrayInputStream(os.toByteArray())
        );
    }

    @Test
    void testCreateContentIntoFile() throws IOException {
        MainFile mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);
        final String content = "Hello, my file system!";
        mainFile.createFile(SIMPLE_FILE_NAME, asInputStream(content));

        assertFileContent(
                FILE_SYSTEM_FILE_PATH,
                "Hello, my file system!" +
                        "\nsimple_file.txt\n"
        );
    }

    @Test
    void testCreateAlreadyExistsFile() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);

        final String content = "Hello, my file system!";
        mainFile.createFile(SIMPLE_FILE_NAME, asInputStream(content));

        assertThatCode(() -> mainFile.createFile(SIMPLE_FILE_NAME, null))
                .isExactlyInstanceOf(FileAlreadyExistsException.class)
                .hasMessage(SIMPLE_FILE_NAME);
    }

    @Test
    void testCreateTwoFiles() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);
        mainFile.createFile("file01.txt", asInputStream("First file content"));
        mainFile.createFile("file02.txt", asInputStream("Second file content"));

        assertFileContent(
                FILE_SYSTEM_FILE_PATH,
                "First file content" +
                        "\nfile01.txt\n" +
                        "Second file content" +
                        "\nfile02.txt\n"
        );
    }

    @Test
    void testCreateFileWithEscapeSymbols() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);
        mainFile.createFile(SIMPLE_FILE_NAME, asInputStream("word\nsecond\tword\n\nthe\tend\n"));

        assertFileContent(
                FILE_SYSTEM_FILE_PATH,
                "word\nsecond\tword\n\nthe\tend\n" +
                        "\n" + SIMPLE_FILE_NAME + "\n"
        );
    }

    @Test
    void testCreateImage() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);
        InputStream imgIS = readJPGInputStream("imgs/parrot.jpg");
        mainFile.createFile("parrot.jpg", imgIS);

        assertFileContent(FILE_SYSTEM_FILE_PATH, "parrot.jpg", readJPGInputStream("imgs/parrot.jpg"));
    }

    @Test
    void testCreateTwoImage() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);

        mainFile.createFile("parrot.jpg", readJPGInputStream("imgs/parrot.jpg"));
        mainFile.createFile("landscape.jpg", readJPGInputStream("imgs/landscape.jpg"));

        assertFileContent(FILE_SYSTEM_FILE_PATH,
                List.of(
                        new FileContent("parrot.jpg", readJPGInputStream("imgs/parrot.jpg")),
                        new FileContent("landscape.jpg", readJPGInputStream("imgs/landscape.jpg"))
                )
        );
    }

    @Test
    void testCreateTwoImageAndText() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);

        mainFile.createFile("parrot.jpg", readJPGInputStream("imgs/parrot.jpg"));
        mainFile.createFile("hello.txt", asInputStream("Hello, file system!"));
        mainFile.createFile("landscape.jpg", readJPGInputStream("imgs/landscape.jpg"));

        assertFileContent(FILE_SYSTEM_FILE_PATH,
                List.of(
                        new FileContent("parrot.jpg", readJPGInputStream("imgs/parrot.jpg")),
                        new FileContent("hello.txt", asInputStream("Hello, file system!")),
                        new FileContent("landscape.jpg", readJPGInputStream("imgs/landscape.jpg"))
                )
        );
    }

    @Test
    void testDeleteFile() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);

        mainFile.createFile("hello.txt", asInputStream("Hello, File system!"));
        mainFile.deleteFile("hello.txt");

        assertThat(Files.readAllLines(Paths.get(FILE_SYSTEM_FILE_PATH)))
                .isEmpty();
        assertThat(mainFile.existsFile("hello.txt")).isFalse();
    }

    @Test
    void testDeleteNotExistFile() {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);

        assertThatCode(() -> mainFile.deleteFile(SIMPLE_FILE_NAME))
                .isExactlyInstanceOf(NoSuchFileException.class)
                .hasMessage(SIMPLE_FILE_NAME);
    }

    @Test
    void testDeleteFirstFileOfTwo() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);

        mainFile.createFile("file01.txt", asInputStream("First file content"));
        mainFile.createFile("file02.txt", asInputStream("Second file content"));

        mainFile.deleteFile("file01.txt");

        assertFileContent(
                FILE_SYSTEM_FILE_PATH,
                "Second file content" +
                        "\nfile02.txt\n"
        );
        assertThat(mainFile.existsFile("file01.txt")).isFalse();
    }

    @Test
    void testDeleteSecondFileOfTwo() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);

        mainFile.createFile("file01.txt", asInputStream("First file content"));
        mainFile.createFile("file02.txt", asInputStream("Second file content"));

        mainFile.deleteFile("file02.txt");

        assertFileContent(
                FILE_SYSTEM_FILE_PATH,
                "First file content" +
                        "\nfile01.txt\n"
        );
        assertThat(mainFile.existsFile("file02.txt")).isFalse();
    }

    @Test
    void testDeleteImgInsteadOfFile() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);

        mainFile.createFile("parrot.jpg", readJPGInputStream("imgs/parrot.jpg"));
        mainFile.createFile("hello.txt", asInputStream("Hello, file system!"));
        mainFile.createFile("landscape.jpg", readJPGInputStream("imgs/landscape.jpg"));

        mainFile.deleteFile("parrot.jpg");

        assertFileContent(FILE_SYSTEM_FILE_PATH,
                List.of(
                        new FileContent("hello.txt", asInputStream("Hello, file system!")),
                        new FileContent("landscape.jpg", readJPGInputStream("imgs/landscape.jpg"))
                )
        );
    }

    @Test
    void testDeleteAndDeleteFiles() throws IOException {
        FileSystem fileSystem = new MainFile(FILE_SYSTEM_FILE_PATH);

        fileSystem.createFile("file01.txt", asInputStream("First file Content"));
        fileSystem.createFile("file02.txt", asInputStream("Second file Content"));
        fileSystem.createFile("file03.txt", asInputStream("Third file Content"));
        fileSystem.createFile("file04.txt", asInputStream("Fourth file Content"));

        fileSystem.deleteFile("file03.txt");
        fileSystem.deleteFile("file02.txt");
        fileSystem.deleteFile("file01.txt");

        assertFileContent(FILE_SYSTEM_FILE_PATH,
                List.of(new FileContent("file04.txt", asInputStream("Fourth file Content")))
        );
    }

    @Test
    void testDeleteTextInsteadOfFile() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);

        mainFile.createFile("parrot.jpg", readJPGInputStream("imgs/parrot.jpg"));
        mainFile.createFile("hello.txt", asInputStream("Hello, file system!"));
        mainFile.createFile("landscape.jpg", readJPGInputStream("imgs/landscape.jpg"));

        mainFile.deleteFile("hello.txt");

        assertFileContent(FILE_SYSTEM_FILE_PATH,
                List.of(
                        new FileContent("parrot.jpg", readJPGInputStream("imgs/parrot.jpg")),
                        new FileContent("landscape.jpg", readJPGInputStream("imgs/landscape.jpg"))
                )
        );
    }

    @Test
    void testDeleteLastFile() throws IOException {
        FileSystem mainFile = new MainFile(FILE_SYSTEM_FILE_PATH);

        mainFile.createFile("parrot.jpg", readJPGInputStream("imgs/parrot.jpg"));
        mainFile.createFile("hello.txt", asInputStream("Hello, file system!"));
        mainFile.createFile("landscape.jpg", readJPGInputStream("imgs/landscape.jpg"));

        mainFile.deleteFile("landscape.jpg");

        assertFileContent(FILE_SYSTEM_FILE_PATH,
                List.of(
                        new FileContent("parrot.jpg", readJPGInputStream("imgs/parrot.jpg")),
                        new FileContent("hello.txt", asInputStream("Hello, file system!"))
                )
        );
    }
}
