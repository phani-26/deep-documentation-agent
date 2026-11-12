package com.deepdoc.docgen;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * The system prompt sent with every documentation model call.
 * The text lives in {@code src/main/resources/prompts/deepdoc-system.md}.
 */
public final class SystemPrompt {
    public static final String RESOURCE_PATH = "/prompts/deepdoc-system.md";

    private static final String TEXT = load();

    private SystemPrompt() {
    }

    public static String text() {
        return TEXT;
    }

    private static String load() {
        try (InputStream input = SystemPrompt.class.getResourceAsStream(RESOURCE_PATH)) {
            if (input == null) {
                throw new IllegalStateException(
                        "Missing classpath resource " + RESOURCE_PATH
                                + ". Expected src/main/resources/prompts/deepdoc-system.md");
            }
            String text = new String(input.readAllBytes(), StandardCharsets.UTF_8).trim();
            if (text.isEmpty()) {
                throw new IllegalStateException(RESOURCE_PATH + " is empty");
            }
            return text;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + RESOURCE_PATH, e);
        }
    }
}
