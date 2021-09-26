package org.test_project.jetbrains.commands;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.test_project.jetbrains.IOConsole;
import org.test_project.jetbrains.commands.validation.ValidationFileFormatRule;
import org.test_project.jetbrains.commands.validation.ValidationParamsRule;
import org.test_project.jetbrains.file_system.FileSystem;

import static org.test_project.jetbrains.commands.FileIOUtils.getFormat;

public abstract class FileSystemCommand implements Command {

    private static final String FILE_NAME_PARAM = "file";
    private static final Set<String> IMAGE_FORMATS = Set.of("JPG", "PNG");
    private static final String TEXT_FILE_FORMAT = "txt";

    protected final FileSystem fileSystem;
    protected final IOConsole console;
    protected final List<ValidationParamsRule> validationRules;

    public FileSystemCommand(FileSystem fileSystem, IOConsole console) {
        this.fileSystem = fileSystem;
        this.console = console;
        this.validationRules = new ArrayList<>();
        validationRules.add(new ValidationFileFormatRule(FILE_NAME_PARAM));
    }

    @Override
    public void execute(CommandParams params) {
        validateParams(params);

        String fileName = params.getParam(FILE_NAME_PARAM);

        try {
            runCommand(fileName, params);
        } catch (NoSuchFileException e) {
            console.println("No such file '" + fileName + "'");
        } catch (FileAlreadyExistsException e) {
            console.println("File '" + fileName + "' already exists");
        } catch (Exception e) {
            console.println(e.getMessage());
        }
    }

    @Override
    public void validateParams(CommandParams params) {
        validationRules
                .forEach(rule -> rule.validate(params));
    }

    @Override
    public String getUsage() {
        return getUsageTemplateWithFileName("<file-path>");
    }

    protected String getUsageTemplateWithFileName(String fileName) {
        return getName() + " --" + FILE_NAME_PARAM + " " + fileName;
    }

    protected boolean isTextFile(CommandParams params) {
        String fileName = params.getParam(FILE_NAME_PARAM);
        String format = getFormat(fileName);
        return format.toLowerCase()
                .endsWith(TEXT_FILE_FORMAT);
    }

    protected boolean isImage(String filePath) {
        String format = getFormat(filePath).toUpperCase();
        return isImageFormat(format);
    }

    private boolean isImageFormat(String format) {
        return IMAGE_FORMATS.contains(format);
    }

    protected abstract void runCommand(String fileName, CommandParams params) throws IOException;
}
