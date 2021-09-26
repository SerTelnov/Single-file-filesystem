package org.test_project.jetbrains.file_system;

public class FileHeaderFactory {

    public String createHeader(FileInfo fileInfo) {
        return '\n' + fileInfo.getFileName() + '\n';
    }
}
