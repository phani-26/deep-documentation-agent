package com.deepdoc.understanding;

import com.deepdoc.model.FileInfo;
import com.deepdoc.model.KnowledgeGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Dispatches each prioritized file to the matching {@link FactExtractor}
 * and builds one unified {@link KnowledgeGraph}.
 */
public class DefaultCodeUnderstandingEngine implements CodeUnderstandingEngine {
    private static final Logger logger = LoggerFactory.getLogger(DefaultCodeUnderstandingEngine.class);

    private final List<FactExtractor> extractors;

    public DefaultCodeUnderstandingEngine() {
        this(defaultExtractors());
    }

    public DefaultCodeUnderstandingEngine(List<FactExtractor> extractors) {
        this.extractors = new ArrayList<>(extractors);
    }

    private static List<FactExtractor> defaultExtractors() {
        List<FactExtractor> list = new ArrayList<>();
        list.add(new JavaScriptFactExtractor());
        return list;
    }

    @Override
    public KnowledgeGraph extractKnowledge(List<FileInfo> prioritizedFiles) {
        KnowledgeGraph graph = new KnowledgeGraph();
        if (prioritizedFiles == null || prioritizedFiles.isEmpty()) {
            logger.info("No files provided for code understanding");
            return graph;
        }

        logger.info("Extracting knowledge from {} files", prioritizedFiles.size());
        int handled = 0;
        int skipped = 0;

        for (FileInfo file : prioritizedFiles) {
            if (isGeneratedParserSource(file)) {
                skipped++;
                continue;
            }
            FactExtractor extractor = findExtractor(file.getLanguage());
            if (extractor == null) {
                skipped++;
                continue;
            }
            try {
                extractor.extract(file, graph);
                handled++;
            } catch (Exception e) {
                logger.warn("Fact extraction failed for {}: {}", file.getFilePath().getFileName(), e.getMessage());
                graph.markFailed(file.getFilePath());
            }
        }

        logger.info("Knowledge extraction complete: {} handled, {} skipped (unsupported language), {}",
                handled, skipped, graph);
        return graph;
    }

    private FactExtractor findExtractor(String language) {
        for (FactExtractor extractor : extractors) {
            if (extractor.supports(language)) {
                return extractor;
            }
        }
        return null;
    }

    /** Skip checked-in ANTLR-generated lexer/parser sources. */
    private static boolean isGeneratedParserSource(FileInfo file) {
        String name = file.getFilePath().getFileName().toString();
        return name.startsWith("JavaScriptTypeScript") && name.endsWith(".java");
    }
}
