package com.deepdoc.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Language-agnostic store of facts, entities, and relationships extracted from code.
 */
public class KnowledgeGraph {
    private final List<CodeFact> facts = new ArrayList<>();
    private final Map<String, CodeEntity> entities = new LinkedHashMap<>();
    private final List<CodeRelationship> relationships = new ArrayList<>();
    private final List<Path> analyzedFiles = new ArrayList<>();
    private final List<Path> failedFiles = new ArrayList<>();

    public void addFact(CodeFact fact) {
        if (fact != null) {
            facts.add(fact);
        }
    }

    public void addFacts(List<CodeFact> more) {
        if (more != null) {
            more.forEach(this::addFact);
        }
    }

    public void addEntity(CodeEntity entity) {
        if (entity != null) {
            entities.put(entity.getId(), entity);
        }
    }

    public void addRelationship(CodeRelationship relationship) {
        if (relationship != null) {
            relationships.add(relationship);
        }
    }

    public void markAnalyzed(Path file) {
        analyzedFiles.add(file);
    }

    public void markFailed(Path file) {
        failedFiles.add(file);
    }

    public List<CodeFact> getFacts() {
        return Collections.unmodifiableList(facts);
    }

    public List<CodeEntity> getEntities() {
        return Collections.unmodifiableList(new ArrayList<>(entities.values()));
    }

    public List<CodeRelationship> getRelationships() {
        return Collections.unmodifiableList(relationships);
    }

    public List<Path> getAnalyzedFiles() {
        return Collections.unmodifiableList(analyzedFiles);
    }

    public List<Path> getFailedFiles() {
        return Collections.unmodifiableList(failedFiles);
    }

    public List<CodeFact> getFactsByLanguage(String language) {
        return facts.stream()
                .filter(f -> language.equalsIgnoreCase(f.getLanguage()))
                .collect(Collectors.toList());
    }

    public List<CodeFact> getFactsByCategory(String category) {
        return facts.stream()
                .filter(f -> category.equalsIgnoreCase(f.getCategory()))
                .collect(Collectors.toList());
    }

    public List<CodeEntity> getEntitiesByKind(CodeEntity.Kind kind) {
        return entities.values().stream()
                .filter(e -> e.getKind() == kind)
                .collect(Collectors.toList());
    }

    public int getFactCount() {
        return facts.size();
    }

    public int getEntityCount() {
        return entities.size();
    }

    public int getRelationshipCount() {
        return relationships.size();
    }

    @Override
    public String toString() {
        return "KnowledgeGraph{" +
                "facts=" + facts.size() +
                ", entities=" + entities.size() +
                ", relationships=" + relationships.size() +
                ", analyzed=" + analyzedFiles.size() +
                ", failed=" + failedFiles.size() +
                '}';
    }
}
