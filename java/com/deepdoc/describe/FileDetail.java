package com.deepdoc.describe;

import com.deepdoc.model.SourceReference;

import java.util.Objects;

/** A deterministic, source-located detail extracted from one file. */
public final class FileDetail {
    private final String category;
    private final String value;
    private final SourceReference evidence;

    public FileDetail(String category, String value, SourceReference evidence) {
        this.category = Objects.requireNonNull(category, "category");
        this.value = Objects.requireNonNull(value, "value");
        this.evidence = evidence;
    }

    public String getCategory() { return category; }
    public String getValue() { return value; }
    public SourceReference getEvidence() { return evidence; }
}
