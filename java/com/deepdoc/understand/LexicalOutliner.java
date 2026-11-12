package com.deepdoc.understand;

import com.deepdoc.graph.LanguageFamily;
import com.deepdoc.graph.SourceScanner;
import com.deepdoc.model.FileInfo;
import com.deepdoc.ranker.DocLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * One outline for every language. Comments and strings are blanked with the syntax
 * {@link LanguageFamily} already assigns, then the same patterns pick out imports,
 * types, functions, and capitalized names. It is not a parser: a declaration the
 * patterns miss is simply absent.
 */
public final class LexicalOutliner implements FileUnderstander {
    public static final String ENGINE = "lexical";

    private static final Pattern PACKAGE = Pattern.compile(
            "(?m)^[ \\t]*(?:package|namespace|module)\\s+([^\\s;{]+)");
    private static final Pattern IMPORT_LINE = Pattern.compile(
            "(?m)^[ \\t]*(?:import|include|require_relative|require|using|use)\\s+(.+?)\\s*;?\\s*$");
    private static final Pattern FROM_IMPORT = Pattern.compile(
            "(?m)^[ \\t]*from\\s+(\\S+)\\s+import\\s+(.+?)\\s*$");
    private static final Pattern HASH_INCLUDE = Pattern.compile(
            "(?m)^[ \\t]*#\\s*include\\s+(.+?)\\s*$");
    private static final Pattern QUOTED = Pattern.compile("\"([^\"]+)\"|'([^']+)'|<([^>]+)>");
    private static final Pattern TYPE = Pattern.compile(
            "\\b(?:class|struct|interface|enum|trait|record)\\s+([A-Za-z_]\\w*)");
    private static final Pattern TYPE_ALIAS = Pattern.compile(
            "\\btype\\s+([A-Za-z_]\\w*)\\b");
    private static final Pattern FUNC = Pattern.compile(
            "\\bfunc\\s+(?:\\([^)\\n]*\\)\\s*)?([A-Za-z_]\\w*)\\s*\\(");
    private static final Pattern FUNC_KEYWORD = Pattern.compile(
            "\\b(?:function|def|fn|fun)\\s+([A-Za-z_]\\w*)\\s*\\(");
    private static final Pattern METHOD = Pattern.compile(
            "(?m)^[ \\t]*(?:(?:public|private|protected|internal|static|final|synchronized|native|abstract|default|async|export|override|virtual|inline|pub)\\b[ \\t]+)*[A-Za-z_][\\w.<>,\\[\\]?]*[ \\t]+([A-Za-z_]\\w*)[ \\t]*\\(");
    private static final Pattern BARE_METHOD = Pattern.compile(
            "(?m)^[ \\t]+([A-Za-z_]\\w*)\\s*\\([^;]*\\)\\s*\\{");
    private static final Pattern ANNOTATION = Pattern.compile(
            "(?m)^[ \\t]*@([A-Za-z_]\\w*)");
    private static final Pattern CAPITAL = Pattern.compile("\\b([A-Z][A-Za-z0-9_]*)\\b");

    private static final Set<String> NOT_A_METHOD = new HashSet<>(Arrays.asList(
            "if", "for", "while", "switch", "catch", "return", "elif", "elsif",
            "sizeof", "new", "with", "except", "case", "when", "until", "unless"));
    private static final Set<String> NOT_A_REFERENCE = new HashSet<>(Arrays.asList(
            "String", "Int", "Integer", "Long", "Boolean", "Float", "Double", "Byte",
            "List", "Map", "Set", "Object", "Error", "Exception", "Any", "None", "True", "False"));

    @Override
    public FileOutline outline(FileInfo file, DocLevel level, String source) {
        if (source == null || source.isBlank()) {
            return new FileOutline(file, level, ENGINE, false, "", List.of(), List.of());
        }
        SourceScanner.Syntax syntax = syntaxOf(file.getLanguage());
        String kept = SourceScanner.blankComments(source, syntax);
        String code = SourceScanner.blankCommentsAndLiterals(source, syntax);

        String namespace = firstGroup(PACKAGE, kept);
        List<String> imports = imports(kept);
        List<Hit> hits = new ArrayList<>();
        collectTypes(code, hits);
        collectFuncs(code, hits);
        collectAnnotations(code, hits);
        hits.sort(Comparator.comparingInt(h -> h.offset));

        List<MutableSymbol> symbols = new ArrayList<>();
        MutableSymbol current = null;
        MutableSymbol fileLevel = null;
        List<String> pendingAnnotations = new ArrayList<>();
        for (Hit hit : hits) {
            if (hit.kind == HitKind.ANNOTATION) {
                pendingAnnotations.add(hit.name);
                continue;
            }
            if (hit.kind == HitKind.TYPE) {
                MutableSymbol symbol = new MutableSymbol(hit.name, hit.symbolKind, lineOf(source, hit.offset));
                symbol.annotations.addAll(pendingAnnotations);
                pendingAnnotations.clear();
                symbol.javadoc = javadocBefore(source, symbol.line);
                symbols.add(symbol);
                current = symbol;
                continue;
            }
            if (NOT_A_METHOD.contains(hit.name)) {
                continue;
            }
            if (current == null) {
                if (fileLevel == null) {
                    fileLevel = new MutableSymbol(fileBase(file), SymbolKind.CLASS, lineOf(source, hit.offset));
                    symbols.add(fileLevel);
                }
                current = fileLevel;
            }
            if (!current.methods.contains(hit.name)) {
                current.methods.add(hit.name);
            }
        }

        MutableSymbol owner = firstType(symbols);
        if (owner != null) {
            Set<String> declared = new HashSet<>();
            for (MutableSymbol symbol : symbols) {
                if (symbol.kind == SymbolKind.CLASS || symbol.kind == SymbolKind.INTERFACE
                        || symbol.kind == SymbolKind.ENUM || symbol.kind == SymbolKind.RECORD) {
                    declared.add(symbol.name);
                }
            }
            Matcher caps = CAPITAL.matcher(code);
            while (caps.find()) {
                String name = caps.group(1);
                if (!declared.contains(name) && !NOT_A_REFERENCE.contains(name) && !owner.references.contains(name)) {
                    owner.references.add(name);
                }
            }
        }

        List<DeclaredSymbol> frozen = new ArrayList<>();
        for (MutableSymbol symbol : symbols) {
            frozen.add(symbol.freeze());
        }
        return new FileOutline(file, level, ENGINE, true, namespace == null ? "" : namespace, imports, frozen);
    }

    private static void collectTypes(String code, List<Hit> hits) {
        Matcher types = TYPE.matcher(code);
        while (types.find()) {
            hits.add(Hit.type(types.start(1), types.group(1), kindOf(typeKeyword(code, types.start()))));
        }
        Matcher aliases = TYPE_ALIAS.matcher(code);
        while (aliases.find()) {
            hits.add(Hit.type(aliases.start(1), aliases.group(1), SymbolKind.CLASS));
        }
    }

    private static void collectFuncs(String code, List<Hit> hits) {
        addFuncs(FUNC, code, hits);
        addFuncs(FUNC_KEYWORD, code, hits);
        addFuncs(METHOD, code, hits);
        addFuncs(BARE_METHOD, code, hits);
    }

    private static void addFuncs(Pattern pattern, String code, List<Hit> hits) {
        Matcher matcher = pattern.matcher(code);
        while (matcher.find()) {
            hits.add(Hit.func(matcher.start(1), matcher.group(1)));
        }
    }

    private static void collectAnnotations(String code, List<Hit> hits) {
        Matcher matcher = ANNOTATION.matcher(code);
        while (matcher.find()) {
            hits.add(Hit.annotation(matcher.start(1), matcher.group(1)));
        }
    }

    private static List<String> imports(String kept) {
        List<String> imports = new ArrayList<>();
        boolean inBlock = false;
        for (String raw : kept.split("\\n", -1)) {
            String line = raw.trim();
            if (line.startsWith("import") && line.endsWith("(")) {
                inBlock = true;
                continue;
            }
            if (inBlock) {
                if (line.startsWith(")")) {
                    inBlock = false;
                    continue;
                }
                String quoted = quoted(line);
                if (quoted != null) {
                    imports.add(quoted);
                }
                continue;
            }
        }
        addMatches(FROM_IMPORT, kept, imports, true);
        addMatches(IMPORT_LINE, kept, imports, false);
        addMatches(HASH_INCLUDE, kept, imports, false);
        return new ArrayList<>(new LinkedHashSet<>(imports));
    }

    private static void addMatches(Pattern pattern, String text, List<String> imports, boolean fromImport) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            if (fromImport) {
                String module = matcher.group(1).replace(";", "");
                String names = matcher.group(2).replace(";", "").trim();
                imports.add(module + "." + names);
                continue;
            }
            String target = importTarget(matcher.group(1));
            if (target != null && !target.isBlank() && !target.equals("(")) {
                imports.add(target);
            }
        }
    }

    private static String importTarget(String rest) {
        String quoted = quoted(rest);
        if (quoted != null) {
            return quoted;
        }
        String cleaned = rest.replace(";", "").trim();
        if (cleaned.endsWith("(") || cleaned.isEmpty()) {
            return null;
        }
        return cleaned;
    }

    private static String quoted(String text) {
        Matcher matcher = QUOTED.matcher(text);
        if (!matcher.find()) {
            return null;
        }
        if (matcher.group(1) != null) {
            return matcher.group(1);
        }
        if (matcher.group(2) != null) {
            return matcher.group(2);
        }
        return matcher.group(3);
    }

    private static String typeKeyword(String code, int nameStart) {
        int i = nameStart - 1;
        while (i >= 0 && Character.isWhitespace(code.charAt(i))) {
            i--;
        }
        int end = i + 1;
        while (i >= 0 && Character.isLetter(code.charAt(i))) {
            i--;
        }
        return code.substring(i + 1, end);
    }

    private static SymbolKind kindOf(String keyword) {
        if ("interface".equals(keyword) || "trait".equals(keyword)) {
            return SymbolKind.INTERFACE;
        }
        if ("enum".equals(keyword)) {
            return SymbolKind.ENUM;
        }
        if ("record".equals(keyword)) {
            return SymbolKind.RECORD;
        }
        return SymbolKind.CLASS;
    }

    private static MutableSymbol firstType(List<MutableSymbol> symbols) {
        for (MutableSymbol symbol : symbols) {
            if (symbol.kind == SymbolKind.CLASS || symbol.kind == SymbolKind.INTERFACE
                    || symbol.kind == SymbolKind.ENUM || symbol.kind == SymbolKind.RECORD) {
                return symbol;
            }
        }
        return null;
    }

    private static String fileBase(FileInfo file) {
        String name = file.getFilePath().getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot <= 0 ? name : name.substring(0, dot);
    }

    private static SourceScanner.Syntax syntaxOf(String language) {
        LanguageFamily family = LanguageFamily.of(language);
        return family == null ? SourceScanner.Syntax.C_LIKE : family.syntax();
    }

    private static String firstGroup(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : "";
    }

    private static int lineOf(String source, int offset) {
        int line = 1;
        int end = Math.min(offset, source.length());
        for (int i = 0; i < end; i++) {
            if (source.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }

    /** First sentence of a comment sitting immediately above {@code line} (1-based). */
    static String javadocBefore(String source, int line) {
        String[] lines = source.split("\\n", -1);
        int i = line - 2;
        while (i >= 0 && (lines[i].trim().isEmpty() || lines[i].trim().startsWith("@"))) {
            i--;
        }
        if (i < 0) {
            return "";
        }
        String trimmed = lines[i].trim();
        if (trimmed.startsWith("//")) {
            return sentence(trimmed.substring(2).trim());
        }
        if (!trimmed.endsWith("*/")) {
            return "";
        }
        int end = i;
        while (i >= 0 && !lines[i].contains("/*")) {
            i--;
        }
        if (i < 0) {
            return "";
        }
        StringBuilder text = new StringBuilder();
        for (int k = i; k <= end; k++) {
            String part = lines[k].trim()
                    .replace("/**", "")
                    .replace("/*", "")
                    .replace("*/", "")
                    .replaceFirst("^\\*+", "")
                    .trim();
            if (!part.isEmpty()) {
                text.append(part).append(' ');
            }
        }
        return sentence(text.toString().trim());
    }

    private static String sentence(String text) {
        int dot = text.indexOf('.');
        if (dot >= 0) {
            return text.substring(0, dot + 1);
        }
        return text;
    }

    private enum HitKind { TYPE, FUNC, ANNOTATION }

    private static final class Hit {
        final HitKind kind;
        final int offset;
        final String name;
        final SymbolKind symbolKind;

        private Hit(HitKind kind, int offset, String name, SymbolKind symbolKind) {
            this.kind = kind;
            this.offset = offset;
            this.name = name;
            this.symbolKind = symbolKind;
        }

        static Hit type(int offset, String name, SymbolKind kind) {
            return new Hit(HitKind.TYPE, offset, name, kind);
        }

        static Hit func(int offset, String name) {
            return new Hit(HitKind.FUNC, offset, name, SymbolKind.METHOD);
        }

        static Hit annotation(int offset, String name) {
            return new Hit(HitKind.ANNOTATION, offset, name, SymbolKind.ANNOTATION);
        }
    }

    private static final class MutableSymbol {
        final String name;
        final SymbolKind kind;
        final int line;
        String javadoc = "";
        final List<String> annotations = new ArrayList<>();
        final List<String> methods = new ArrayList<>();
        final List<String> references = new ArrayList<>();

        MutableSymbol(String name, SymbolKind kind, int line) {
            this.name = name;
            this.kind = kind;
            this.line = line;
        }

        DeclaredSymbol freeze() {
            return new DeclaredSymbol(name, kind, line, "", javadoc,
                    annotations, List.of(), List.of(), methods, references);
        }
    }
}
