package com.deepdoc.model;

import java.nio.file.Path;
import java.util.Objects;

/**
 * A named structural unit discovered in source (class, function, component, etc.).
 */
public class CodeEntity {
    public enum Kind {
        CLASS,
        INTERFACE,
        FUNCTION,
        METHOD,
        COMPONENT,
        MODULE,
        TYPE,
        VARIABLE
    }

    private final String name;
    private final Kind kind;
    private final Path file;
    private final int startLine;
    private final int endLine;
    private final String language;

    public CodeEntity(String name, Kind kind, Path file, int startLine, int endLine, String language) {
        this.name = name;
        this.kind = kind;
        this.file = file;
        this.startLine = startLine;
        this.endLine = endLine;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public Kind getKind() {
        return kind;
    }

    public Path getFile() {
        return file;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public String getLanguage() {
        return language;
    }

    public String getId() {
        return language + ":" + file + "#" + kind + ":" + name + "@" + startLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeEntity that = (CodeEntity) o;
        return startLine == that.startLine
                && Objects.equals(name, that.name)
                && kind == that.kind
                && Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, kind, file, startLine);
    }

    @Override
    public String toString() {
        return kind + " " + name + " (" + file.getFileName() + ":" + startLine + ")";
    }
}
