package org.test_project.jetbrains.commands;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.test_project.jetbrains.IOConsole;
import org.test_project.jetbrains.commands.validation.ValidationParamsRule;
import org.test_project.jetbrains.file_system.FileSystem;

public class ReadCommand extends FileSystemCommand {

    private static final String DESTINATION_PARAM = "destination";
    private final FileIOUtils fileIOUtils;

    public ReadCommand(FileSystem fileSystem, FileIOUtils fileIOUtils, IOConsole console) {
        super(fileSystem, console);
        this.fileIOUtils = fileIOUtils;

        this.validationRules.add(getBinaryFileValidationRule());
        this.validationRules.add(getDestinationExtensionRule());
    }

    private ValidationParamsRule getBinaryFileValidationRule() {
        return params -> {
            if (isTextFile(params)) {
                return;
            }

            if (!params.containsParam(DESTINATION_PARAM)) {
                throw new IllegalArgumentException(
                        "Add '" + DESTINATION_PARAM + "' parameter for binary file"
                );
            }
        };
    }

    private ValidationParamsRule getDestinationExtensionRule() {
        return params -> params.getParamSafe(DESTINATION_PARAM)
                .map(file -> file.split("\\."))
                .ifPresent(split -> {
                    if (split.length < 2) {
                        throw new IllegalArgumentException(
                                "Parameter '" + DESTINATION_PARAM + "' must have extension"
                        );
                    }
                });
    }

    @Override
    protected void runCommand(String fileName, CommandParams params) throws IOException {
        if (params.containsParam(DESTINATION_PARAM)) {
            String dst = params.getParam(DESTINATION_PARAM);
            if (isImage(dst)) {
                byte[] bytes = fileSystem.readFile(fileName);
                fileIOUtils.writeAsImage(dst, bytes);
            } else {
                read(fileName, dst);
            }
        } else {
            read(fileName);
        }
    }

    private void read(String file, String dst) throws IOException {
        fileSystem.readFile(file, fileIOUtils.getAsOutputStream(dst));
    }

    private void read(String file) throws IOException {
        byte[] bytes = fileSystem.readFile(file);
        console.write(new String(bytes, StandardCharsets.UTF_8));
    }

    @Override
    public String getName() {
        return "read";
    }

    @Override
    public String getUsage() {
        String example = getUsageTemplateWithFileName("hello.txt");
        return super.getUsage() + "\n" +
                super.getUsage() + " --" + DESTINATION_PARAM + " <dst>\n" +
                "Example: " + example + "\n" +
                "         " + example + " --" + DESTINATION_PARAM + " out.txt";
    }
}
