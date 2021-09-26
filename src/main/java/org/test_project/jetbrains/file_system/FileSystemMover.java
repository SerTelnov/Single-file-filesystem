package org.test_project.jetbrains.file_system;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

public class FileSystemMover extends AbstractFileInteractive {

    private final FileHeaderFactory headerFactory;

    protected FileSystemMover(Path fileSystemFile, FileHeaderFactory headerFactory) {
        super(fileSystemFile);
        this.headerFactory = headerFactory;
    }

    public List<FileInfo> moveFiles(Iterator<FileInfo> filesToMoveIterator, long movePosition) throws IOException {
        List<FileInfo> updatedFiles = new ArrayList<>();

        try (SeekableByteChannel sbc = Files.newByteChannel(fileSystemFile, READ, WRITE)) {
            while (filesToMoveIterator.hasNext()) {
                FileInfo fileToMove = filesToMoveIterator.next();
                long nextPosition = moveFile(sbc, fileToMove, movePosition);

                updatedFiles.add(getUpdatedFileInfo(movePosition, fileToMove));
                movePosition = nextPosition;
            }
            sbc.truncate(movePosition);
        }

        return updatedFiles;
    }

    private long moveFile(SeekableByteChannel sbc, FileInfo fileInfo, long movePosition) throws IOException {
        long filePosition = fileInfo.getPositionOnFile();
        long contentSize = fileInfo.getLengthOfBytes();
        FileInfo updatedFileInfo = getUpdatedFileInfo(movePosition, fileInfo);

        ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
        sbc.position(filePosition);

        while (sbc.read(buf) > 0 && contentSize > 0) {
            sbc.position(movePosition);
            int readBytes = writeNextBuf(sbc, contentSize, buf);

            contentSize -= readBytes;
            movePosition += readBytes;
            filePosition += readBytes;
            sbc.position(filePosition);
        }

        sbc.position(movePosition);
        String header = headerFactory.createHeader(updatedFileInfo);
        sbc.write(ByteBuffer.wrap(header.getBytes()));

        return sbc.position();
    }

    private int writeNextBuf(SeekableByteChannel sbc, long contentSize, ByteBuffer buf) throws IOException {
        buf.flip();

        if (buf.limit() > contentSize) {
            buf.limit((int) contentSize);
        }

        sbc.write(buf);

        int readBytes = buf.limit();
        buf.clear();
        return readBytes;
    }

    private FileInfo getUpdatedFileInfo(long movePosition, FileInfo fileToUpdate) {
        return FileInfo.builder()
                .setPositionOnFile(movePosition)
                .setFileName(fileToUpdate.getFileName())
                .setLengthOfBytes(fileToUpdate.getLengthOfBytes())
                .build();
    }
}
