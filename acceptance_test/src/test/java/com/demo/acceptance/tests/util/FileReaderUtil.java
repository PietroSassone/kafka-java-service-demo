package com.demo.acceptance.tests.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class FileReaderUtil {

    private static final String FILE_PATH_TEMPLATE = "testdata/%s/%s";
    private static final String DELIMITER = "\n";

    @Autowired
    private ObjectMapper objectMapper;

    public ObjectNode readFileToJsonNode(final String file, final String locationFolder) {
        try (final InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream(String.format(FILE_PATH_TEMPLATE, locationFolder, file))) {
            return (ObjectNode) objectMapper.readTree(fileInputStream);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String readFileToString(final String file, final String locationFolder) {
        try {
            return String.join(DELIMITER, Files.readAllLines(Paths.get(this.getClass().getClassLoader().getResource(String.format(FILE_PATH_TEMPLATE, locationFolder, file)).toURI())));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
