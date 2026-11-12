package com.deepdoc.docgen;

import com.deepdoc.describe.FileDescription;
import com.deepdoc.model.FileInfo;
import com.deepdoc.ranker.DocLevel;
import com.deepdoc.understand.ExtractedFact;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * One file prepared for documentation: its Week-1 level, one-liner, and (for DEEP/SUMMARY)
 * extracted short facts.
 */
public final class FileDocEntry {
    private final FileInfo file;
    private final String relativePath;
    private final DocLevel level;
    private final String packageName;
    private final FileDescription description;
    private final List<ExtractedFact> facts;

    public FileDocEntry(FileInfo file, String relativePath, DocLevel level, String packageName,
                        FileDescription description, List<ExtractedFact> facts) {
        this.file = Objects.requireNonNull(file);
        this.relativePath = Objects.requireNonNull(relativePath);
        this.level = Objects.requireNonNull(level);
        this.packageName = packageName == null ? "" : packageName;
        this.description = Objects.requireNonNull(description);
        this.facts = Collections.unmodifiableList(facts == null ? List.of() : facts);
    }

    public FileInfo getFile() {
        return file;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public DocLevel getLevel() {
        return level;
    }

    public String getPackageName() {
        return packageName;
    }

    public FileDescription getDescription() {
        return description;
    }

    public List<ExtractedFact> getFacts() {
        return facts;
    }

    public String oneLiner() {
        return description.getText();
    }
}
