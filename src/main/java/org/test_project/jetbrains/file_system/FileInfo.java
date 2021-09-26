package org.test_project.jetbrains.file_system;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FileInfo implements Comparable<FileInfo> {

    private final long positionOnFile;
    private final String fileName;
    private final long lengthOfBytes;

    private FileInfo(long positionOnFile, String fileName, long lengthOfBytes) {
        this.positionOnFile = positionOnFile;
        this.fileName = fileName;
        this.lengthOfBytes = lengthOfBytes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getPositionOnFile() {
        return positionOnFile;
    }

    public String getFileName() {
        return fileName;
    }

    public long getLengthOfBytes() {
        return lengthOfBytes;
    }

    @Override
    public int compareTo(@Nonnull FileInfo o) {
        return Comparator.comparingLong(FileInfo::getPositionOnFile)
                .thenComparing(FileInfo::getFileName)
                .thenComparing(FileInfo::getLengthOfBytes)
                .compare(this, o);
    }

    public static class Builder {

        private Long positionOnFile;
        private String fileName;
        private Long lengthOfBytes;

        public Builder setPositionOnFile(Long positionOnFile) {
            this.positionOnFile = positionOnFile;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setLengthOfBytes(Long lengthOfBytes) {
            this.lengthOfBytes = lengthOfBytes;
            return this;
        }

        public FileInfo build() {
            return new FileInfo(positionOnFile, fileName, lengthOfBytes);
        }
    }
}
