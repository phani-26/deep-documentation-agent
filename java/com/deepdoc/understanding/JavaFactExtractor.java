package com.deepdoc.understanding;

import com.deepdoc.model.CodeEntity;
import com.deepdoc.model.CodeFact;
import com.deepdoc.model.CodeRelationship;
import com.deepdoc.model.FactType;
import com.deepdoc.model.FileInfo;
import com.deepdoc.model.KnowledgeGraph;
import com.deepdoc.model.SourceReference;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Minimal Java fact extractor so the Day 8 unified fact model is shared across
 * Java and JavaScript/TypeScript. Full Java deep-analysis is Week 2 Day 6-7;
 * this covers structural facts needed for a language-agnostic graph.
 */
public class JavaFactExtractor implements FactExtractor {
    private static final Logger logger = LoggerFactory.getLogger(JavaFactExtractor.class);

    @Override
    public boolean supports(String language) {
        return "Java".equalsIgnoreCase(language);
    }

    @Override
    public void extract(FileInfo file, KnowledgeGraph graph) throws IOException {
        Path path = file.getFilePath();
        try {
            CompilationUnit cu = StaticJavaParser.parse(path);
            String language = "Java";
            String moduleId = language + ":" + path + "#MODULE:" + path.getFileName() + "@1";

            graph.addEntity(new CodeEntity(
                    path.getFileName().toString(),
                    CodeEntity.Kind.MODULE,
                    path,
                    1,
                    cu.getEnd().map(p -> p.line).orElse(1),
                    language));

            for (ImportDeclaration importDecl : cu.getImports()) {
                int line = importDecl.getBegin().map(p -> p.line).orElse(1);
                String name = importDecl.getNameAsString();
                SourceReference ref = new SourceReference(path, line, line, importDecl.toString().trim());
                graph.addFact(new CodeFact(
                        "Imports '" + name + "'",
                        FactType.STRUCTURAL,
                        0.98,
                        List.of(ref),
                        language,
                        "import"));
                graph.addRelationship(new CodeRelationship(
                        moduleId, "type:" + name, CodeRelationship.Type.IMPORTS, ref));
            }

            for (TypeDeclaration<?> type : cu.getTypes()) {
                extractType(path, language, type, graph);
            }

            graph.markAnalyzed(path);
        } catch (Exception e) {
            logger.warn("Failed to parse Java file {}: {}", path.getFileName(), e.getMessage());
            graph.markFailed(path);
        }
    }

    private void extractType(Path path, String language, TypeDeclaration<?> type, KnowledgeGraph graph) {
        int start = type.getBegin().map(p -> p.line).orElse(1);
        int end = type.getEnd().map(p -> p.line).orElse(start);
        String name = type.getNameAsString();
        SourceReference ref = new SourceReference(path, start, end, summarize(type));

        if (type instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) type;
            CodeEntity.Kind kind = cid.isInterface() ? CodeEntity.Kind.INTERFACE : CodeEntity.Kind.CLASS;
            CodeEntity entity = new CodeEntity(name, kind, path, start, end, language);
            graph.addEntity(entity);
            graph.addFact(new CodeFact(
                    (cid.isInterface() ? "Defines interface '" : "Defines class '") + name + "'",
                    FactType.STRUCTURAL,
                    0.98,
                    List.of(ref),
                    language,
                    cid.isInterface() ? "interface" : "class"));

            cid.getExtendedTypes().forEach(ext ->
                    graph.addRelationship(new CodeRelationship(
                            entity.getId(), ext.getNameAsString(),
                            CodeRelationship.Type.EXTENDS, ref)));
            cid.getImplementedTypes().forEach(impl ->
                    graph.addRelationship(new CodeRelationship(
                            entity.getId(), impl.getNameAsString(),
                            CodeRelationship.Type.IMPLEMENTS, ref)));

            for (MethodDeclaration method : cid.getMethods()) {
                int mStart = method.getBegin().map(p -> p.line).orElse(start);
                int mEnd = method.getEnd().map(p -> p.line).orElse(mStart);
                String methodName = method.getNameAsString();
                graph.addEntity(new CodeEntity(
                        name + "#" + methodName, CodeEntity.Kind.METHOD, path, mStart, mEnd, language));
                graph.addFact(new CodeFact(
                        "Defines method '" + name + "." + methodName + "'",
                        FactType.STRUCTURAL,
                        0.95,
                        List.of(new SourceReference(path, mStart, mEnd, method.getDeclarationAsString())),
                        language,
                        "method"));
            }
        } else if (type instanceof EnumDeclaration) {
            graph.addEntity(new CodeEntity(name, CodeEntity.Kind.TYPE, path, start, end, language));
            graph.addFact(new CodeFact(
                    "Defines enum '" + name + "'",
                    FactType.STRUCTURAL,
                    0.98,
                    List.of(ref),
                    language,
                    "enum"));
        }
    }

    private static String summarize(TypeDeclaration<?> type) {
        String raw = type.getNameAsString();
        if (type instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) type;
            return (cid.isInterface() ? "interface " : "class ") + raw;
        }
        return raw;
    }
}
