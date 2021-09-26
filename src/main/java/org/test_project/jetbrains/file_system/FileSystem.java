package org.test_project.jetbrains.file_system;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface for File system which works inside one class.
 */
@ParametersAreNonnullByDefault
public interface FileSystem {

    /**
     * Create new file in the File system and write {@code content} into this file.
     *
     * @param fileName Name of new file
     * @param content  File content
     * @throws FileAlreadyExistsException If file with this name already exists.
     * @throws IOException                If I/O error occurs
     */
    void createFile(String fileName, InputStream content) throws IOException;

    /**
     * Read file content in the file system.
     * <p>
     * Recommend to use for small file only.
     *
     * @param fileName Name of the readable file
     * @return File content
     * @throws NoSuchFileException If readable file is not exist.
     * @throws IOException         If I/O error occurs
     */
    @Nonnull
    byte[] readFile(String fileName) throws IOException;

    /**
     * Read file content in the file system and write into {@code OuputStream}.
     *
     * @param fileName Name of the readable file
     * @param os       Output of read content
     * @throws NoSuchFileException If readable file is not exist.
     * @throws IOException         If I/O error occurs
     */
    void readFile(String fileName, OutputStream os) throws IOException;

    /**
     * Check existing of the file.
     *
     * @param fileName file to check
     * @return {@code true} if file exists.
     */
    boolean existsFile(String fileName);

    /**
     * Delete file from the file system.
     *
     * @param fileName Name of file to delete
     * @throws NoSuchFileException If readable file is not exist.
     * @throws IOException         If I/O error occurs
     */
    void deleteFile(String fileName) throws IOException;
}
