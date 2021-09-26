package org.test_project.jetbrains;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class IOConsoleTest {

    @Test
    void testReadLine() throws IOException {
        String content = "Hello, Console!";
        IOConsole console = makeConsole(content);

        assertThat(console.readLine())
                .isEqualTo(content);
    }

    @Test
    void testReadNoTrimLine() throws IOException {
        String input = "\timportant values\t";
        IOConsole console = makeConsole(input);

        assertThat(console.readLine())
                .isEqualTo(input);
    }

    @Test
    void readLine() throws IOException {
        String content = "content";
        String input = "\n\t" + content + "\t\n\t";
        IOConsole console = makeConsole(input);

        assertThat(console.readLine()).isEmpty();
        assertThat(console.readLine()).isEqualTo("\tcontent\t");
        assertThat(console.readLine()).isEqualTo("\t");

        assertThatCode(console::readLine)
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    void testReadClosedReader() {
        IOConsole console = makeConsole("");
        assertThatCode(console::readLine)
                .isNotInstanceOf(NullPointerException.class)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No data to read");
    }

    @Test
    void testWriteIntoOutput() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOConsole console = makeConsole(out);

        String msg = "This message will be written by Console";
        console.write(msg);

        assertThat(out).hasToString(msg);
    }

    @Test
    void testPrintlnIntoOutput() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOConsole console = makeConsole(out);

        String msg = "This message will be written by Console";
        console.println(msg);

        assertThat(out).hasToString(msg + "\n");
    }

    private IOConsole makeConsole(String input) {
        return makeConsole(new StringReader(input), new StringWriter());
    }

    private IOConsole makeConsole(OutputStream os) {
        return new IOConsole(new StringReader(""), new OutputStreamWriter(os));
    }

    private IOConsole makeConsole(Reader reader, Writer writer) {
        return new IOConsole(reader, writer);
    }
}
