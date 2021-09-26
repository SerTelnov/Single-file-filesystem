package org.test_project.jetbrains.file_system;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.APPEND;

public class FileSystemWriter extends AbstractFileInteractive {

    public FileSystemWriter(Path fileSystemFile) {
        super(fileSystemFile);
    }

    public FileInfo writeContent(String fileName, InputStream content) throws IOException {
        long bytes = 0;
        long position;

        try (
                ReadableByteChannel rbc = Channels.newChannel(content);
                SeekableByteChannel sbc = Files.newByteChannel(fileSystemFile, APPEND)
        ) {
            position = sbc.position();
            ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);

            while (rbc.read(buf) > 0) {
                buf.flip();
                sbc.write(buf);
                bytes += buf.limit();
                buf.clear();
            }
        }

        return FileInfo.builder()
                .setFileName(fileName)
                .setPositionOnFile(position)
                .setLengthOfBytes(bytes)
                .build();
    }

    public void writeHeader(String header) throws IOException {
        try (SeekableByteChannel sbc = Files.newByteChannel(fileSystemFile, APPEND)) {
            sbc.write(ByteBuffer.wrap(header.getBytes()));
        }
    }
}
