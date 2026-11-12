package com.deepdoc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A single verifiable (or explicitly low-confidence) claim about the codebase.
 * Language-agnostic so Java and JavaScript/TypeScript facts share one model.
 */
public class CodeFact {
    private final String claim;
    private final FactType type;
    private final double confidence;
    private final List<SourceReference> sources;
    private final String language;
    private final String category;

    public CodeFact(String claim, FactType type, double confidence,
                    List<SourceReference> sources, String language, String category) {
        this.claim = claim;
        this.type = type;
        this.confidence = clamp(confidence);
        this.sources = sources == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(sources));
        this.language = language;
        this.category = category == null ? "general" : category;
    }

    private static double clamp(double value) {
        if (value < 0.0) return 0.0;
        if (value > 1.0) return 1.0;
        return value;
    }

    public String getClaim() {
        return claim;
    }

    public FactType getType() {
        return type;
    }

    public double getConfidence() {
        return confidence;
    }

    public List<SourceReference> getSources() {
        return sources;
    }

    public String getLanguage() {
        return language;
    }

    public String getCategory() {
        return category;
    }

    public boolean isLowConfidence() {
        return confidence < 0.6;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeFact codeFact = (CodeFact) o;
        return Double.compare(codeFact.confidence, confidence) == 0
                && Objects.equals(claim, codeFact.claim)
                && type == codeFact.type
                && Objects.equals(sources, codeFact.sources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(claim, type, confidence, sources);
    }

    @Override
    public String toString() {
        return "CodeFact{" +
                "claim='" + claim + '\'' +
                ", type=" + type +
                ", confidence=" + confidence +
                ", category='" + category + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
