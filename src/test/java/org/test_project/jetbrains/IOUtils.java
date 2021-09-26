package org.test_project.jetbrains;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public final class IOUtils {

    private IOUtils() {
        throw new UnsupportedOperationException();
    }

    public static Path getPath(String resourceFile) {
        return Paths.get(IOUtils.class.getClassLoader().getResource(resourceFile).getFile());
    }

    public static InputStream readJPGInputStream(String imgPath) throws IOException {
        return readImageInputStream(imgPath, "jpg");
    }

    public static InputStream readImageInputStream(String imgPath, String format) throws IOException {
        BufferedImage bi = ImageIO.read(readInputStream(imgPath));
        try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(bi, format, byteOutputStream);
            return new ByteArrayInputStream(byteOutputStream.toByteArray());
        }
    }

    public static InputStream readInputStream(String path) {
        return IOUtils.class.getClassLoader().getResourceAsStream(path);
    }

    public static InputStream asInputStream(String content) {
        return new ByteArrayInputStream(content.getBytes());
    }

    public static InputStream asInputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }
}
