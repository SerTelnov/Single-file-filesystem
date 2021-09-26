package org.test_project.jetbrains.file_system;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.READ;

public class FileSystemReader extends AbstractFileInteractive {

    public FileSystemReader(Path fileSystemFile) {
        super(fileSystemFile);
    }

    public void read(FileInfo fileInfo, OutputStream os) throws IOException {
        try (SeekableByteChannel sbc = Files.newByteChannel(fileSystemFile, READ)) {
            readContent(fileInfo, sbc, os);
        }
    }

    private void readContent(FileInfo contentInfo, SeekableByteChannel sbc, OutputStream os) throws IOException {
        sbc.position(contentInfo.getPositionOnFile());

        ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
        long contentSize = contentInfo.getLengthOfBytes();

        while (sbc.read(buf) > 0 && contentSize > 0) {
            buf.flip();
            int readBytes = safeMin(contentSize, buf.limit());
            os.write(buf.array(), buf.arrayOffset(), readBytes);
            contentSize -= readBytes;
            buf.clear();
        }
    }

    private int safeMin(long contentSize, int limit) {
        if (contentSize > Integer.MAX_VALUE) {
            return limit;
        }
        return Math.min(limit, (int) contentSize);
    }
}
