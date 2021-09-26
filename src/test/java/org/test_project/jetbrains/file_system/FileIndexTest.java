package org.test_project.jetbrains.file_system;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.test_project.jetbrains.file_system.MainFileTest.FILE_SYSTEM_FILE_PATH;

class FileIndexTest {

    @Test
    void testIndexEmptyFile() {
        FileIndex fileIndex = new FileIndex(Paths.get(FILE_SYSTEM_FILE_PATH));
        assertThat(fileIndex.existsFile("file.txt"))
                .isFalse();
    }
}