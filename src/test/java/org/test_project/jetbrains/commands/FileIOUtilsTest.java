package org.test_project.jetbrains.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.test_project.jetbrains.IOUtils.getPath;
import static org.test_project.jetbrains.IOUtils.readInputStream;
import static org.test_project.jetbrains.IOUtils.readJPGInputStream;
import static org.test_project.jetbrains.file_system.FileAssertions.assertUncheckedContentEquals;

class FileIOUtilsTest {

    FileIOUtils fileIOUtils = new FileIOUtils();

    @Test
    void testTextFileAsStream() {
        InputStream is = fileIOUtils.getAsInputStream(getPath("text/brodsky.txt"));
        InputStream expected = readInputStream("text/brodsky.txt");
        assertUncheckedContentEquals(is, expected);
    }

    @Test
    void testFileAsOutputStream() throws IOException {
        Path tempFile = Files.createTempFile(getPath("text/brodsky.txt").getParent(), "tmp", "txt");
        assertThatCode(() -> fileIOUtils.getAsOutputStream(tempFile))
                .doesNotThrowAnyException();

        assertThat(fileIOUtils.getAsOutputStream(tempFile))
                .isNotNull();

        Files.deleteIfExists(tempFile);
    }

    @Test
    void testImageAsInputStream() throws IOException {
        Path path = getPath("imgs/parrot.jpg");
        Path absolutePath = path.toAbsolutePath();
        InputStream actual = fileIOUtils.getAsImageInputStream(absolutePath.toString());
        InputStream expected = readJPGInputStream("imgs/parrot.jpg");

        assertUncheckedContentEquals(actual, expected);
    }

    @Test
    void testWriteAsImage() throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] bytes = Files.readAllBytes(getPath("imgs/parrot.jpg"));
            fileIOUtils.writeAsImage(os, "parrot.jpg", bytes);

            assertUncheckedContentEquals(
                    new ByteArrayInputStream(os.toByteArray()),
                    readJPGInputStream("imgs/parrot.jpg")
            );
        }
    }
}
