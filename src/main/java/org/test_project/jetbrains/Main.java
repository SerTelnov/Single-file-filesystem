package org.test_project.jetbrains;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.test_project.jetbrains.commands.Command;
import org.test_project.jetbrains.commands.ConsoleCommandParser;
import org.test_project.jetbrains.commands.DeleteCommand;
import org.test_project.jetbrains.commands.ExitCommand;
import org.test_project.jetbrains.commands.CreateCommand;
import org.test_project.jetbrains.commands.FileIOUtils;
import org.test_project.jetbrains.commands.ReadCommand;
import org.test_project.jetbrains.commands.HelpCommand;
import org.test_project.jetbrains.file_system.FileSystem;
import org.test_project.jetbrains.file_system.MainFile;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("No path to filesystem main file");
        }

        String mainFilePath = args[0];

        try (IOConsole console = new IOConsole(new InputStreamReader(System.in), new OutputStreamWriter(System.out))) {
            FileSystem fileSystem = new MainFile(mainFilePath);
            FileIOUtils fileIOUtils = new FileIOUtils();

            CreateCommand createCommand = new CreateCommand(fileSystem, fileIOUtils, console);
            ReadCommand readCommand = new ReadCommand(fileSystem, fileIOUtils, console);
            DeleteCommand deleteCommand = new DeleteCommand(fileSystem, console);
            HelpCommand helpCommand = new HelpCommand(console, List.of(createCommand, readCommand, deleteCommand));
            ExitCommand exitCommand = new ExitCommand();

            Map<String, Command> commands = List.of(createCommand, exitCommand, readCommand, deleteCommand, helpCommand)
                    .stream()
                    .collect(Collectors.toMap(Command::getName, Function.identity()));

            ConsoleCommandParser consoleCommandParser = new ConsoleCommandParser(commands);

            new Runner(console, consoleCommandParser).runInConsole();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
