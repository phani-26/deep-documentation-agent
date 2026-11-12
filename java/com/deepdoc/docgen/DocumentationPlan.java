package com.deepdoc.docgen;

import com.deepdoc.ranker.DocLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Every repository file slotted as DEEP / SUMMARY / LISTED with a one-liner (and facts when known). */
public final class DocumentationPlan {
    private final List<FileDocEntry> entries;

    public DocumentationPlan(List<FileDocEntry> entries) {
        this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
    }

    public List<FileDocEntry> getEntries() {
        return entries;
    }

    public List<FileDocEntry> at(DocLevel level) {
        return entries.stream().filter(e -> e.getLevel() == level).collect(Collectors.toList());
    }

    /** Package / subsystem name → files in ranking order within that group. */
    public Map<String, List<FileDocEntry>> byPackage() {
        Map<String, List<FileDocEntry>> grouped = new LinkedHashMap<>();
        for (FileDocEntry entry : entries) {
            String key = entry.getPackageName().isEmpty() ? "(root)" : entry.getPackageName();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(entry);
        }
        return grouped;
    }

    public int size() {
        return entries.size();
    }
}
