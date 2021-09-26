package org.test_project.jetbrains.commands;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;

@ParametersAreNonnullByDefault
public class FileIOUtils {

    public InputStream getAsInputStream(String filePath) {
        Path path = Paths.get(filePath);
        return getAsInputStream(path);
    }

    public InputStream getAsInputStream(Path path) {
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String getFormat(String filePath) {
        String[] split = filePath.split("\\.");
        return split[split.length - 1];
    }

    public InputStream getAsImageInputStream(String imgPath) throws IOException {
        String format = getFormat(imgPath);
        return readImageInputStream(imgPath, format);
    }

    private InputStream readImageInputStream(String imgPath, String format) throws IOException {
        BufferedImage bi = ImageIO.read(getAsInputStream(imgPath));
        try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bi, format, byteOutputStream);
            return new ByteArrayInputStream(byteOutputStream.toByteArray());
        }
    }

    public void writeAsImage(String filePath, byte[] byteImg) throws IOException {
        try (OutputStream os = getAsOutputStream(filePath)) {
            writeAsImage(os, filePath, byteImg);
        }
    }

    public void writeAsImage(OutputStream os, String fileName, byte[] byteImg) throws IOException {
        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(byteImg));
        String format = getFormat(fileName);
        ImageIO.write(bi, format, os);
    }

    public OutputStream getAsOutputStream(String filePath) {
        return getAsOutputStream(Paths.get(filePath));
    }

    public OutputStream getAsOutputStream(Path path) {
        try {
            return Files.newOutputStream(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
