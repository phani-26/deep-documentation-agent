package com.deepdoc.understanding;

import com.deepdoc.model.CodeEntity;
import com.deepdoc.model.CodeFact;
import com.deepdoc.model.CodeRelationship;
import com.deepdoc.model.FactType;
import com.deepdoc.model.FileInfo;
import com.deepdoc.model.KnowledgeGraph;
import com.deepdoc.model.SourceReference;
import com.deepdoc.parser.JavaScriptTypeScriptBaseVisitor;
import com.deepdoc.parser.JavaScriptTypeScriptLexer;
import com.deepdoc.parser.JavaScriptTypeScriptParser;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts structural facts from JavaScript and TypeScript sources via ANTLR4,
 * with a text-based fallback when the focused grammar cannot parse a file
 * (e.g. heavy JSX). Facts land in the unified {@link KnowledgeGraph}.
 */
public class JavaScriptFactExtractor implements FactExtractor {
    private static final Logger logger = LoggerFactory.getLogger(JavaScriptFactExtractor.class);

    private static final Set<String> SUPPORTED = Set.of("JavaScript", "TypeScript");

    private static final Pattern IMPORT_FROM = Pattern.compile(
            "import\\s+(?:type\\s+)?(?:([\\w$]+)|\\*\\s+as\\s+([\\w$]+)|\\{([^}]+)\\})\\s+from\\s+['\"]([^'\"]+)['\"]");
    private static final Pattern IMPORT_SIDE_EFFECT = Pattern.compile(
            "import\\s+['\"]([^'\"]+)['\"]");
    private static final Pattern EXPORT_NAMED = Pattern.compile(
            "export\\s+(?:default\\s+)?(?:async\\s+)?(?:function\\*?|class|const|let|var|interface|type)\\s+([\\w$]+)");
    private static final Pattern EXPORT_DEFAULT_ANON = Pattern.compile(
            "export\\s+default\\s+");
    private static final Pattern CLASS_DECL = Pattern.compile(
            "(?:export\\s+(?:default\\s+)?)?(?:abstract\\s+)?class\\s+([\\w$]+)(?:\\s+extends\\s+([\\w$]+))?");
    private static final Pattern FUNCTION_DECL = Pattern.compile(
            "(?:export\\s+(?:default\\s+)?)?(?:async\\s+)?function\\*?\\s+([\\w$]+)");
    private static final Pattern ARROW_CONST = Pattern.compile(
            "(?:export\\s+(?:default\\s+)?)?(?:const|let|var)\\s+([A-Z][\\w$]*)\\s*(?::[^=]+)?=\\s*(?:async\\s*)?(?:\\([^)]*\\)|[\\w$]+)\\s*=>");
    private static final Pattern INTERFACE_DECL = Pattern.compile(
            "(?:export\\s+)?interface\\s+([\\w$]+)");
    private static final Pattern TYPE_ALIAS = Pattern.compile(
            "(?:export\\s+)?type\\s+([\\w$]+)\\s*=");
    private static final Pattern REACT_COMPONENT_JSX = Pattern.compile(
            "(?:function|const)\\s+([A-Z][\\w$]*)");

    @Override
    public boolean supports(String language) {
        return language != null && SUPPORTED.contains(language);
    }

    @Override
    public void extract(FileInfo file, KnowledgeGraph graph) throws IOException {
        Path path = file.getFilePath();
        String language = file.getLanguage();
        String source = Files.readString(path, StandardCharsets.UTF_8);

        boolean parsed = tryAntlrExtract(path, language, source, graph);
        if (!parsed) {
            logger.warn("ANTLR parse incomplete for {}; using text-based fallback", path.getFileName());
            extractWithHeuristics(path, language, source, graph);
        }
        graph.markAnalyzed(path);
    }

    private boolean tryAntlrExtract(Path path, String language, String source, KnowledgeGraph graph) {
        AtomicBoolean hadError = new AtomicBoolean(false);
        try {
            JavaScriptTypeScriptLexer lexer = new JavaScriptTypeScriptLexer(CharStreams.fromString(source));
            lexer.removeErrorListeners();
            lexer.addErrorListener(new FlaggingErrorListener(hadError));

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            JavaScriptTypeScriptParser parser = new JavaScriptTypeScriptParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new FlaggingErrorListener(hadError));

            ParseTree tree = parser.compilationUnit();
            FactVisitor visitor = new FactVisitor(path, language, source, graph);
            visitor.visit(tree);

            // If the lexer/parser reported errors but we still extracted something, keep results.
            return visitor.getExtractedCount() > 0 || !hadError.get();
        } catch (Exception e) {
            logger.debug("ANTLR extraction failed for {}: {}", path, e.getMessage());
            return false;
        }
    }

    private void extractWithHeuristics(Path path, String language, String source, KnowledgeGraph graph) {
        String[] lines = source.split("\\R", -1);
        String moduleId = moduleEntityId(path, language);

        CodeEntity module = new CodeEntity(
                path.getFileName().toString(),
                CodeEntity.Kind.MODULE,
                path,
                1,
                Math.max(1, lines.length),
                language);
        graph.addEntity(module);

        matchAll(IMPORT_FROM, source, lines, (m, line) -> {
            String modulePath = m.group(4);
            addImportFact(graph, path, language, moduleId, modulePath, line, m.group());
        });
        matchAll(IMPORT_SIDE_EFFECT, source, lines, (m, line) ->
                addImportFact(graph, path, language, moduleId, m.group(1), line, m.group()));

        matchAll(CLASS_DECL, source, lines, (m, line) -> {
            String name = m.group(1);
            String parent = m.group(2);
            addClass(graph, path, language, moduleId, name, parent, line, m.group());
        });
        matchAll(FUNCTION_DECL, source, lines, (m, line) ->
                addFunction(graph, path, language, moduleId, m.group(1), line, m.group(), false));
        matchAll(ARROW_CONST, source, lines, (m, line) ->
                addFunction(graph, path, language, moduleId, m.group(1), line, m.group(), true));
        matchAll(INTERFACE_DECL, source, lines, (m, line) ->
                addInterface(graph, path, language, m.group(1), line, m.group()));
        matchAll(TYPE_ALIAS, source, lines, (m, line) ->
                addTypeAlias(graph, path, language, m.group(1), line, m.group()));
        matchAll(EXPORT_NAMED, source, lines, (m, line) ->
                addExportFact(graph, path, language, moduleId, m.group(1), line, m.group()));

        if (EXPORT_DEFAULT_ANON.matcher(source).find()
                && REACT_COMPONENT_JSX.matcher(source).find()
                && ("JavaScript".equals(language) || "TypeScript".equals(language))) {
            // Soft signal only — React-ish file
            int line = firstMatchLine(REACT_COMPONENT_JSX, source, lines);
            graph.addFact(new CodeFact(
                    "File appears to define a React-style component (PascalCase export)",
                    FactType.INFERRED,
                    0.7,
                    List.of(ref(path, line, line, snippetAt(lines, line))),
                    language,
                    "framework"));
        }
    }

    private void addImportFact(KnowledgeGraph graph, Path path, String language,
                               String moduleId, String imported, int line, String snippet) {
        SourceReference ref = ref(path, line, line, snippet);
        graph.addFact(new CodeFact(
                "Imports module '" + imported + "'",
                FactType.STRUCTURAL,
                0.95,
                List.of(ref),
                language,
                "import"));
        graph.addRelationship(new CodeRelationship(
                moduleId,
                "module:" + imported,
                CodeRelationship.Type.IMPORTS,
                ref));
    }

    private void addExportFact(KnowledgeGraph graph, Path path, String language,
                               String moduleId, String name, int line, String snippet) {
        SourceReference ref = ref(path, line, line, snippet);
        graph.addFact(new CodeFact(
                "Exports '" + name + "'",
                FactType.STRUCTURAL,
                0.95,
                List.of(ref),
                language,
                "export"));
        graph.addRelationship(new CodeRelationship(
                moduleId,
                language + ":" + path + "#export:" + name + "@" + line,
                CodeRelationship.Type.EXPORTS,
                ref));
    }

    private void addClass(KnowledgeGraph graph, Path path, String language, String moduleId,
                          String name, String parent, int line, String snippet) {
        CodeEntity entity = new CodeEntity(name, CodeEntity.Kind.CLASS, path, line, line, language);
        graph.addEntity(entity);
        SourceReference ref = ref(path, line, line, snippet);
        graph.addFact(new CodeFact(
                "Defines class '" + name + "'" + (parent != null ? " extending '" + parent + "'" : ""),
                FactType.STRUCTURAL,
                0.95,
                List.of(ref),
                language,
                "class"));
        if (parent != null) {
            graph.addRelationship(new CodeRelationship(
                    entity.getId(), parent, CodeRelationship.Type.EXTENDS, ref));
        }
        if (looksLikeReactClass(snippet, parent)) {
            graph.addEntity(new CodeEntity(name, CodeEntity.Kind.COMPONENT, path, line, line, language));
            graph.addFact(new CodeFact(
                    "Class '" + name + "' appears to be a React component",
                    FactType.INFERRED,
                    0.75,
                    List.of(ref),
                    language,
                    "component"));
        }
    }

    private void addFunction(KnowledgeGraph graph, Path path, String language, String moduleId,
                             String name, int line, String snippet, boolean arrowComponentCandidate) {
        boolean component = arrowComponentCandidate || Character.isUpperCase(name.charAt(0));
        CodeEntity.Kind kind = component ? CodeEntity.Kind.COMPONENT : CodeEntity.Kind.FUNCTION;
        CodeEntity entity = new CodeEntity(name, kind, path, line, line, language);
        graph.addEntity(entity);
        SourceReference ref = ref(path, line, line, snippet);
        graph.addFact(new CodeFact(
                (component ? "Defines component '" : "Defines function '") + name + "'",
                component && arrowComponentCandidate ? FactType.INFERRED : FactType.STRUCTURAL,
                component && arrowComponentCandidate ? 0.8 : 0.95,
                List.of(ref),
                language,
                component ? "component" : "function"));
    }

    private void addInterface(KnowledgeGraph graph, Path path, String language,
                              String name, int line, String snippet) {
        graph.addEntity(new CodeEntity(name, CodeEntity.Kind.INTERFACE, path, line, line, language));
        graph.addFact(new CodeFact(
                "Defines interface '" + name + "'",
                FactType.STRUCTURAL,
                0.95,
                List.of(ref(path, line, line, snippet)),
                language,
                "interface"));
    }

    private void addTypeAlias(KnowledgeGraph graph, Path path, String language,
                              String name, int line, String snippet) {
        graph.addEntity(new CodeEntity(name, CodeEntity.Kind.TYPE, path, line, line, language));
        graph.addFact(new CodeFact(
                "Defines type alias '" + name + "'",
                FactType.STRUCTURAL,
                0.95,
                List.of(ref(path, line, line, snippet)),
                language,
                "type"));
    }

    private static boolean looksLikeReactClass(String snippet, String parent) {
        if (parent == null) return false;
        return parent.contains("Component") || parent.contains("PureComponent");
    }

    private static String moduleEntityId(Path path, String language) {
        return language + ":" + path + "#MODULE:" + path.getFileName() + "@1";
    }

    private static SourceReference ref(Path path, int start, int end, String snippet) {
        String trimmed = snippet == null ? "" : snippet.trim();
        if (trimmed.length() > 120) {
            trimmed = trimmed.substring(0, 117) + "...";
        }
        return new SourceReference(path, start, end, trimmed);
    }

    private static String snippetAt(String[] lines, int line) {
        if (line < 1 || line > lines.length) return "";
        return lines[line - 1].trim();
    }

    private static int firstMatchLine(Pattern pattern, String source, String[] lines) {
        Matcher m = pattern.matcher(source);
        if (m.find()) {
            return lineNumberOf(source, m.start(), lines);
        }
        return 1;
    }

    private static int lineNumberOf(String source, int offset, String[] lines) {
        int line = 1;
        int cursor = 0;
        for (String l : lines) {
            int next = cursor + l.length();
            // account for newline consumed by split
            if (offset <= next) {
                return line;
            }
            cursor = next + 1;
            line++;
        }
        return Math.max(1, lines.length);
    }

    @FunctionalInterface
    private interface MatchHandler {
        void handle(Matcher matcher, int line);
    }

    private static void matchAll(Pattern pattern, String source, String[] lines, MatchHandler handler) {
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            handler.handle(matcher, lineNumberOf(source, matcher.start(), lines));
        }
    }

    private static final class FlaggingErrorListener extends BaseErrorListener {
        private final AtomicBoolean flag;

        private FlaggingErrorListener(AtomicBoolean flag) {
            this.flag = flag;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                int line, int charPositionInLine, String msg,
                                RecognitionException e) {
            flag.set(true);
        }
    }

    /**
     * ANTLR visitor that maps grammar rules onto the unified fact model.
     */
    static final class FactVisitor extends JavaScriptTypeScriptBaseVisitor<Void> {
        private final Path path;
        private final String language;
        private final KnowledgeGraph graph;
        private final String moduleId;
        private int extracted;

        FactVisitor(Path path, String language, String source, KnowledgeGraph graph) {
            this.path = path;
            this.language = language;
            this.graph = graph;
            this.moduleId = moduleEntityId(path, language);
            String[] lines = source.split("\\R", -1);
            CodeEntity module = new CodeEntity(
                    path.getFileName().toString(),
                    CodeEntity.Kind.MODULE,
                    path,
                    1,
                    Math.max(1, lines.length),
                    language);
            graph.addEntity(module);
        }

        int getExtractedCount() {
            return extracted;
        }

        @Override
        public Void visitImportDecl(JavaScriptTypeScriptParser.ImportDeclContext ctx) {
            int line = ctx.getStart().getLine();
            String text = ctx.getText();
            String modulePath = findStringLiteral(ctx);
            if (modulePath != null) {
                SourceReference ref = ref(path, line, line, text);
                graph.addFact(new CodeFact(
                        "Imports module '" + modulePath + "'",
                        FactType.STRUCTURAL,
                        0.98,
                        List.of(ref),
                        language,
                        "import"));
                graph.addRelationship(new CodeRelationship(
                        moduleId, "module:" + modulePath, CodeRelationship.Type.IMPORTS, ref));
                extracted++;
            }
            return null;
        }

        @Override
        public Void visitClassDecl(JavaScriptTypeScriptParser.ClassDeclContext ctx) {
            if (ctx.Identifier() == null) {
                return null;
            }
            String name = ctx.Identifier().getText();
            int line = ctx.getStart().getLine();
            String parent = null;
            if (ctx.classHeritage() != null && ctx.classHeritage().EXTENDS() != null
                    && !ctx.classHeritage().typeName().isEmpty()) {
                parent = ctx.classHeritage().typeName(0).getText();
            }
            CodeEntity entity = new CodeEntity(name, CodeEntity.Kind.CLASS, path, line, line, language);
            graph.addEntity(entity);
            SourceReference ref = ref(path, line, line, ctx.getText());
            graph.addFact(new CodeFact(
                    "Defines class '" + name + "'" + (parent != null ? " extending '" + parent + "'" : ""),
                    FactType.STRUCTURAL,
                    0.98,
                    List.of(ref),
                    language,
                    "class"));
            if (parent != null) {
                graph.addRelationship(new CodeRelationship(
                        entity.getId(), parent, CodeRelationship.Type.EXTENDS, ref));
            }
            if (parent != null && (parent.contains("Component") || parent.contains("PureComponent"))) {
                graph.addEntity(new CodeEntity(name, CodeEntity.Kind.COMPONENT, path, line, line, language));
                graph.addFact(new CodeFact(
                        "Class '" + name + "' appears to be a React component",
                        FactType.INFERRED,
                        0.8,
                        List.of(ref),
                        language,
                        "component"));
            }
            extracted++;
            return visitChildren(ctx);
        }

        @Override
        public Void visitFunctionDecl(JavaScriptTypeScriptParser.FunctionDeclContext ctx) {
            if (ctx.Identifier() == null) {
                return null;
            }
            String name = ctx.Identifier().getText();
            int line = ctx.getStart().getLine();
            boolean component = !name.isEmpty() && Character.isUpperCase(name.charAt(0));
            CodeEntity.Kind kind = component ? CodeEntity.Kind.COMPONENT : CodeEntity.Kind.FUNCTION;
            graph.addEntity(new CodeEntity(name, kind, path, line, line, language));
            graph.addFact(new CodeFact(
                    (component ? "Defines component '" : "Defines function '") + name + "'",
                    FactType.STRUCTURAL,
                    0.98,
                    List.of(ref(path, line, line, ctx.getText())),
                    language,
                    component ? "component" : "function"));
            extracted++;
            return null;
        }

        @Override
        public Void visitInterfaceDecl(JavaScriptTypeScriptParser.InterfaceDeclContext ctx) {
            if (ctx.Identifier() == null) {
                return null;
            }
            String name = ctx.Identifier().getText();
            int line = ctx.getStart().getLine();
            graph.addEntity(new CodeEntity(name, CodeEntity.Kind.INTERFACE, path, line, line, language));
            graph.addFact(new CodeFact(
                    "Defines interface '" + name + "'",
                    FactType.STRUCTURAL,
                    0.98,
                    List.of(ref(path, line, line, ctx.getText())),
                    language,
                    "interface"));
            extracted++;
            return null;
        }

        @Override
        public Void visitTypeAliasDecl(JavaScriptTypeScriptParser.TypeAliasDeclContext ctx) {
            if (ctx.Identifier() == null) {
                return null;
            }
            String name = ctx.Identifier().getText();
            int line = ctx.getStart().getLine();
            graph.addEntity(new CodeEntity(name, CodeEntity.Kind.TYPE, path, line, line, language));
            graph.addFact(new CodeFact(
                    "Defines type alias '" + name + "'",
                    FactType.STRUCTURAL,
                    0.98,
                    List.of(ref(path, line, line, ctx.getText())),
                    language,
                    "type"));
            extracted++;
            return null;
        }

        @Override
        public Void visitVariableDecl(JavaScriptTypeScriptParser.VariableDeclContext ctx) {
            if (ctx.Identifier() == null) {
                return null;
            }
            String name = ctx.Identifier().getText();
            int line = ctx.getStart().getLine();
            String text = ctx.getText();
            boolean arrow = text.contains("=>");
            boolean component = arrow && !name.isEmpty() && Character.isUpperCase(name.charAt(0));
            if (component) {
                graph.addEntity(new CodeEntity(name, CodeEntity.Kind.COMPONENT, path, line, line, language));
                graph.addFact(new CodeFact(
                        "Defines component '" + name + "'",
                        FactType.INFERRED,
                        0.85,
                        List.of(ref(path, line, line, text)),
                        language,
                        "component"));
                extracted++;
            } else if (arrow) {
                graph.addEntity(new CodeEntity(name, CodeEntity.Kind.FUNCTION, path, line, line, language));
                graph.addFact(new CodeFact(
                        "Defines arrow function '" + name + "'",
                        FactType.STRUCTURAL,
                        0.9,
                        List.of(ref(path, line, line, text)),
                        language,
                        "function"));
                extracted++;
            } else {
                graph.addEntity(new CodeEntity(name, CodeEntity.Kind.VARIABLE, path, line, line, language));
                graph.addFact(new CodeFact(
                        "Declares variable '" + name + "'",
                        FactType.STRUCTURAL,
                        0.9,
                        List.of(ref(path, line, line, text)),
                        language,
                        "variable"));
                extracted++;
            }
            return null;
        }

        @Override
        public Void visitExportDecl(JavaScriptTypeScriptParser.ExportDeclContext ctx) {
            int line = ctx.getStart().getLine();
            String text = ctx.getText();
            // Child declarations are visited separately; record the export relationship.
            graph.addFact(new CodeFact(
                    "Contains export declaration",
                    FactType.STRUCTURAL,
                    0.9,
                    List.of(ref(path, line, line, text)),
                    language,
                    "export"));
            extracted++;
            return visitChildren(ctx);
        }

        private static String findStringLiteral(JavaScriptTypeScriptParser.ImportDeclContext ctx) {
            if (ctx.importBody() == null) {
                return null;
            }
            if (ctx.importBody().StringLiteral() != null) {
                return unquote(ctx.importBody().StringLiteral().getText());
            }
            return null;
        }

        private static String unquote(String literal) {
            if (literal == null || literal.length() < 2) {
                return literal;
            }
            return literal.substring(1, literal.length() - 1);
        }
    }
}
