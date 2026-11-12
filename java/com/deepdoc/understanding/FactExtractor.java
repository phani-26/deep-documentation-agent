package com.deepdoc.understanding;

import com.deepdoc.model.FileInfo;
import com.deepdoc.model.KnowledgeGraph;

import java.io.IOException;

/**
 * Language-specific extractor that populates the unified {@link KnowledgeGraph}.
 */
public interface FactExtractor {

    /**
     * @return true if this extractor handles the given language name
     *         (e.g. "Java", "JavaScript", "TypeScript")
     */
    boolean supports(String language);

    /**
     * Parse {@code file} and add facts/entities/relationships into {@code graph}.
     */
    void extract(FileInfo file, KnowledgeGraph graph) throws IOException;
}
