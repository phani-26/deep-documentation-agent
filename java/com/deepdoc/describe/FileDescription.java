package com.deepdoc.describe;

import com.deepdoc.model.FileInfo;
import com.deepdoc.model.SourceReference;

import java.util.Objects;

/**
 * One-line role for a repository file. Every file in the manifest gets one so coverage never
 * silently drops a path; origin tells the reader how trustworthy the wording is.
 */
public final class FileDescription {
    private final FileInfo file;
    private final String text;
    private final DescriptionOrigin origin;
    private final double confidence;
    private final SourceReference evidence;

    public FileDescription(FileInfo file, String text, DescriptionOrigin origin,
                           double confidence, SourceReference evidence) {
        this.file = Objects.requireNonNull(file, "file");
        this.text = Objects.requireNonNull(text, "text");
        this.origin = Objects.requireNonNull(origin, "origin");
        this.confidence = confidence;
        this.evidence = evidence;
    }

    public FileInfo getFile() {
        return file;
    }

    public String getText() {
        return text;
    }

    public DescriptionOrigin getOrigin() {
        return origin;
    }

    public double getConfidence() {
        return confidence;
    }

    /** Line that backs a COMMENT description; null for heuristics. */
    public SourceReference getEvidence() {
        return evidence;
    }

    @Override
    public String toString() {
        return text + " [" + origin.name().toLowerCase() + "]";
    }
}
