package org.test_project.jetbrains.file_system;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;
import static java.nio.file.attribute.PosixFilePermissions.asFileAttribute;

@ParametersAreNonnullByDefault
public class MainFile implements FileSystem {

    private static final Set<PosixFilePermission> MAIN_FILE_PERMISSION = Set.of(OWNER_WRITE, OWNER_READ);

    private final FileIndex fileIndex;

    private final FileSystemMover fileSystemMover;
    private final FileSystemWriter fileSystemWriter;
    private final FileSystemReader fileSystemReader;
    private final FileHeaderFactory fileHeaderFactory;

    public MainFile(String fileSystemFilePath) {
        Path fileSystemFile = Paths.get(fileSystemFilePath);
        fileIndex = new FileIndex(fileSystemFile);
        fileHeaderFactory = new FileHeaderFactory();

        fileSystemWriter = new FileSystemWriter(fileSystemFile);
        fileSystemMover = new FileSystemMover(fileSystemFile, fileHeaderFactory);
        fileSystemReader = new FileSystemReader(fileSystemFile);

        try {
            initFile(fileSystemFile);
        } catch (IOException e) {
            throw new UncheckedIOException("Can not create main file for file system", e);
        }
    }

    private void initFile(Path fileSystemFile) throws IOException {
        if (Files.exists(fileSystemFile)) {
            Files.deleteIfExists(fileSystemFile);
        }

        if (Files.notExists(fileSystemFile.getParent())) {
            Files.createDirectories(fileSystemFile.getParent());
        }

        Files.createFile(fileSystemFile, asFileAttribute(MAIN_FILE_PERMISSION));
    }

    @Override
    public void createFile(String fileName, InputStream content) throws IOException {
        validateFileNotExists(fileName);

        FileInfo fileInfo = fileSystemWriter.writeContent(fileName, content);
        fileSystemWriter.writeHeader(fileHeaderFactory.createHeader(fileInfo));

        fileIndex.addFileInfo(fileInfo);
    }

    @Override
    public boolean existsFile(String fileName) {
        return fileIndex.existsFile(fileName);
    }

    @Override
    public void deleteFile(String fileName) throws IOException {
        validateFileExists(fileName);
        moveOnRemovingFile(fileName);
        fileIndex.removeFileInfo(fileName);
    }

    private void moveOnRemovingFile(String fileName) throws IOException {
        FileInfo removeFileInfo = fileIndex.getFileInfo(fileName);
        long movePosition = removeFileInfo.getPositionOnFile();

        fileSystemMover
                .moveFiles(fileIndex.getFileTail(fileName), movePosition)
                .forEach(this::updateFileInfo);
    }

    private void updateFileInfo(FileInfo updatedInfo) {
        fileIndex.removeFileInfo(updatedInfo.getFileName());
        fileIndex.addFileInfo(updatedInfo);
    }

    @Override
    @Nonnull
    public byte[] readFile(String fileName) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            readFile(fileName, os);
            return os.toByteArray();
        }
    }

    @Override
    public void readFile(String fileName, OutputStream os) throws IOException {
        validateFileExists(fileName);
        fileSystemReader.read(fileIndex.getFileInfo(fileName), os);
    }

    private void validateFileExists(String fileName) throws NoSuchFileException {
        if (!existsFile(fileName)) {
            throw new NoSuchFileException(fileName);
        }
    }

    private void validateFileNotExists(String fileName) throws FileAlreadyExistsException {
        if (existsFile(fileName)) {
            throw new FileAlreadyExistsException(fileName);
        }
    }
}
