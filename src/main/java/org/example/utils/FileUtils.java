package org.example.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
public class FileUtils {


    public static List<String> readFileAllLines(String... filePath) {
        try (Stream<String> stream = Files.lines(convertResourceToPath(filePath))) {
            return stream.collect(Collectors.toList());
        } catch (IOException e) {
            log.error("readFileAllLines error", e);
            return new ArrayList<>();
        }
    }

    public static void writeAllLinesToFile(List<String> outputLines, String... filePath) {
        Path path = convertResourceToPath(filePath);
        try {
            Files.write(path, outputLines, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            log.error("writeAllLinesToFile error", e);
        }
    }

    private static Path convertResourceToPath(String... filePath) {
        String path = Arrays.stream(filePath).collect(Collectors.joining("/"));
        URL resource = FileUtils.class.getClassLoader().getResource(path);
        try {
            return Paths.get(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
