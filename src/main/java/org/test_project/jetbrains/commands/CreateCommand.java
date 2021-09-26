package org.test_project.jetbrains.commands;

import java.io.IOException;
import java.io.InputStream;

import org.test_project.jetbrains.IOConsole;
import org.test_project.jetbrains.commands.validation.ValidationFileFormatRule;
import org.test_project.jetbrains.file_system.FileSystem;

public class CreateCommand extends FileSystemCommand {

    private static final String COMMAND_NAME = "create";
    private static final String SOURCE_PATH_PARAM = "source";
    private final FileIOUtils fileIOUtils;

    public CreateCommand(FileSystem fileSystem, FileIOUtils fileIOUtils, IOConsole console) {
        super(fileSystem, console);
        this.fileIOUtils = fileIOUtils;
        this.validationRules.add(new ValidationFileFormatRule(SOURCE_PATH_PARAM));
    }

    @Override
    protected void runCommand(String fileName, CommandParams params) throws IOException {
        String source = params.getParam(SOURCE_PATH_PARAM);
        InputStream is;

        if (isImage(fileName)) {
            is = fileIOUtils.getAsImageInputStream(source);
        } else {
            is = fileIOUtils.getAsInputStream(source);
        }

        fileSystem.createFile(fileName, is);
    }

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public String getUsage() {
        String example = getUsageTemplateWithFileName("hello.txt");
        return super.getUsage() + " --" + SOURCE_PATH_PARAM + " <source-file-path>" + "\n" +
                "Example: " + example + " --" + SOURCE_PATH_PARAM + " /path/to/source/file.txt";
    }
}
