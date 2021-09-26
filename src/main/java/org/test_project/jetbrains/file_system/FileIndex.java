package org.test_project.jetbrains.file_system;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class FileIndex {

    private final Map<String, FileInfo> filesInformation;
    private final SortedSet<FileInfo> sortedByPositionFiles;

    public FileIndex(Path path) {
        this.filesInformation = new HashMap<>();
        this.sortedByPositionFiles = new TreeSet<>();
    }

    public void addFileInfo(FileInfo fileInfo) {
        filesInformation.put(fileInfo.getFileName(), fileInfo);
        sortedByPositionFiles.add(fileInfo);
    }

    public boolean existsFile(String fileName) {
        return filesInformation.containsKey(fileName);
    }

    public void removeFileInfo(String fileName) {
        FileInfo removed = filesInformation.remove(fileName);
        sortedByPositionFiles.remove(removed);
    }

    public Iterator<FileInfo> getFileTail(String fileName) {
        return sortedByPositionFiles.tailSet(getFileInfo(fileName))
                .stream()
                .skip(1)
                .iterator();
    }

    public FileInfo getFileInfo(String fileName) {
        return filesInformation.get(fileName);
    }
}
