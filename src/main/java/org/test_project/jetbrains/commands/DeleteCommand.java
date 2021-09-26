package org.test_project.jetbrains.commands;

import java.io.IOException;

import org.test_project.jetbrains.IOConsole;
import org.test_project.jetbrains.file_system.FileSystem;

public class DeleteCommand extends FileSystemCommand {

    public DeleteCommand(FileSystem fileSystem, IOConsole console) {
        super(fileSystem, console);
    }

    @Override
    protected void runCommand(String fileName, CommandParams params) throws IOException {
        fileSystem.deleteFile(fileName);
    }

    @Override
    public String getUsage() {
        return super.getUsage() + "\n" +
                "Example: " + getUsageTemplateWithFileName("hello.txt");
    }

    @Override
    public String getName() {
        return "delete";
    }
}
