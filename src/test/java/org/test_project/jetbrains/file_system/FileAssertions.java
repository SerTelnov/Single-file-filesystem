package org.test_project.jetbrains.file_system;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.List;

import org.apache.commons.io.IOUtils;

import static org.assertj.core.api.Assertions.assertThat;

public final class FileAssertions {

    private FileAssertions() {
        throw new UnsupportedOperationException();
    }

    public static void assertUncheckedContentEquals(InputStream a, InputStream b) {
        assertThat(uncheckedContentEquals(a, b)).isTrue();
    }

    public static boolean uncheckedContentEquals(InputStream a, InputStream b) {
        try {
            return IOUtils.contentEquals(a, b);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void assertInitFile(Path path) {
        assertThat(Files.exists(path)).isTrue();
        assertThat(Files.isRegularFile(path)).isTrue();
        try {
            assertThat(Files.getPosixFilePermissions(path))
                    .containsExactlyInAnyOrder(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void assertFileContent(String file, String expected) {
        final Path path = Paths.get(file);
        try {
            assertThat(Files.readString(path))
                    .isEqualTo(expected);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void assertFileContent(String file, String header, InputStream expected) {
        assertFileContent(file, List.of(new FileContent(header, expected)));
    }

    public static void assertFileContent(String file, List<FileContent> expected) {
        if (expected.isEmpty()) {
            throw new IllegalArgumentException("Empty expected");
        }

        try (SeekableByteChannel sbc = Files.newByteChannel(Paths.get(file))) {
            for (FileContent fileContent : expected) {
                final String header = fileContent.getFileName();
                assertInputReader(sbc, header, fileContent.getIS());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void assertInputReader(
            SeekableByteChannel actualByteChannel,
            String header,
            InputStream expected
    ) throws IOException {
        try (ReadableByteChannel expectedByteChannel = Channels.newChannel(expected)) {
            ByteBuffer expectedBuf = ByteBuffer.allocate(4 * 1024);
            ByteBuffer actualBuf = ByteBuffer.allocate(4 * 1024);

            while (true) {
                long position = actualByteChannel.position();

                int readExpected = expectedByteChannel.read(expectedBuf);
                int readActual = actualByteChannel.read(actualBuf);

                expectedBuf.flip();
                actualBuf.flip();

                if (readExpected <= 0) {
                    break;
                } else if (readActual <= 0) {
                    throw new AssertionError("Expected has more values");
                }

                assertUncheckedContentEquals(
                        new ByteArrayInputStream(expectedBuf.array(), expectedBuf.arrayOffset(), expectedBuf.limit()),
                        new ByteArrayInputStream(actualBuf.array(), actualBuf.arrayOffset(), expectedBuf.limit())
                );

                if (expectedBuf.limit() < actualBuf.limit()) {
                    actualByteChannel.position(position + expectedBuf.limit());
                    expectedBuf.clear();
                    break;
                }

                expectedBuf.clear();
                actualBuf.clear();
            }

            ByteBuffer headerBuf = ByteBuffer.allocate(("\n" + header + "\n").getBytes().length);
            actualByteChannel.read(headerBuf);
            assertThat(new String(
                    headerBuf.array(),
                    headerBuf.arrayOffset(),
                    headerBuf.limit(),
                    StandardCharsets.UTF_8)
            ).contains(header);
        }
    }
}
