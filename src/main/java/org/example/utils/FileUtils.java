package org.example.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
