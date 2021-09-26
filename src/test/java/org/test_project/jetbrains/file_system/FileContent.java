package org.test_project.jetbrains.file_system;

import java.io.InputStream;

public class FileContent {

    private final String fileName;
    private final InputStream is;

    public FileContent(String fileName, InputStream is) {
        this.fileName = fileName;
        this.is = is;
    }

    public String getFileName() {
        return fileName;
    }

    public InputStream getIS() {
        return is;
    }
}
