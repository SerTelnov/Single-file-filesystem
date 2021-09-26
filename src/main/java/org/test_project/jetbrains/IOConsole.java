package org.test_project.jetbrains;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;

import javax.annotation.Nonnull;

public class IOConsole implements AutoCloseable {

    private final BufferedReader reader;
    private final BufferedWriter writer;

    public IOConsole(Reader reader, Writer writer) {
        this.reader = new BufferedReader(reader);
        this.writer = new BufferedWriter(writer);
    }

    @Nonnull
    public String readLine() throws IOException {
        String line = reader.readLine();

        if (line == null) {
            throw new IllegalStateException("No data to read");
        }

        return line;
    }

    public void println(@Nonnull String message) {
        write(message + "\n");
    }

    public void write(@Nonnull String message) {
        try {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() throws Exception {
        reader.close();
        writer.close();
    }
}
