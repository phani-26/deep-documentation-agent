package com.deepdoc.understanding;

import com.deepdoc.model.FileInfo;
import com.deepdoc.model.KnowledgeGraph;

import java.util.List;

/**
 * Builds a language-agnostic knowledge graph from prioritized source files.
 * See design document section 7.4.
 */
public interface CodeUnderstandingEngine {

    KnowledgeGraph extractKnowledge(List<FileInfo> prioritizedFiles);
}
