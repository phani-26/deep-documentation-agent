package com.deepdoc.model;

import java.util.Objects;

/**
 * Directed relationship between two code entities (import, extends, calls, etc.).
 */
public class CodeRelationship {
    public enum Type {
        IMPORTS,
        EXTENDS,
        IMPLEMENTS,
        CALLS,
        USES,
        EXPORTS
    }

    private final String fromEntityId;
    private final String toEntityId;
    private final Type type;
    private final SourceReference source;

    public CodeRelationship(String fromEntityId, String toEntityId, Type type, SourceReference source) {
        this.fromEntityId = fromEntityId;
        this.toEntityId = toEntityId;
        this.type = type;
        this.source = source;
    }

    public String getFromEntityId() {
        return fromEntityId;
    }

    public String getToEntityId() {
        return toEntityId;
    }

    public Type getType() {
        return type;
    }

    public SourceReference getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeRelationship that = (CodeRelationship) o;
        return Objects.equals(fromEntityId, that.fromEntityId)
                && Objects.equals(toEntityId, that.toEntityId)
                && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromEntityId, toEntityId, type);
    }

    @Override
    public String toString() {
        return fromEntityId + " -[" + type + "]-> " + toEntityId;
    }
}
