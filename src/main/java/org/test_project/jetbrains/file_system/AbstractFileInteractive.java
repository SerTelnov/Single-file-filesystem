package org.test_project.jetbrains.file_system;

import java.nio.file.Path;

public abstract class AbstractFileInteractive {

    protected static final int BUFFER_SIZE = 8 * 1024;
    protected final Path fileSystemFile;

    protected AbstractFileInteractive(Path fileSystemFile) {
        this.fileSystemFile = fileSystemFile;
    }
}
