package com.deepdoc.describe;

import com.deepdoc.model.SourceReference;

import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pulls a leading documentation comment from source text. Language-agnostic: works from
 * common comment forms rather than a full parser. No new library — plain regex over the file.
 */
final class DocCommentExtractor {

    /** First Javadoc/JSDoc-style block in the file preamble (after package/imports is fine). */
    private static final Pattern BLOCK_DOC = Pattern.compile(
            "(?s)/\\*\\*(.*?)\\*/");
    private static final Pattern PYTHON_DOCSTRING = Pattern.compile(
            "(?s)^\\s*(?:(?:from\\s+\\S+\\s+import\\s+[^\\n]+|import\\s+[^\\n]+)\\s*\\n)*\\s*[ruRU]?\"\"\"(.*?)\"\"\"");
    private static final Pattern PYTHON_DOCSTRING_SINGLE = Pattern.compile(
            "(?s)^\\s*(?:(?:from\\s+\\S+\\s+import\\s+[^\\n]+|import\\s+[^\\n]+)\\s*\\n)*\\s*[ruRU]?'''(.*?)'''");
    private static final Pattern HTML_COMMENT = Pattern.compile(
            "(?s)<!--(.*?)-->");

    private DocCommentExtractor() {
    }

    static Optional<CommentHit> extract(Path file, String language, String source) {
        if (source == null || source.isBlank()) {
            return Optional.empty();
        }
        String lang = language == null ? "" : language;
        // Only search the opening of the file so method-level docs do not become the file role.
        String preamble = preamble(source);

        if (isPythonFamily(lang)) {
            Optional<CommentHit> py = firstUsefulGroup(file, source, preamble, PYTHON_DOCSTRING);
            if (py.isPresent()) {
                return py;
            }
            return firstUsefulGroup(file, source, preamble, PYTHON_DOCSTRING_SINGLE);
        }

        if ("HTML".equalsIgnoreCase(lang) || "XML".equalsIgnoreCase(lang)) {
            Optional<CommentHit> html = firstUsefulGroup(file, source, preamble, HTML_COMMENT);
            if (html.isPresent()) {
                return html;
            }
        }

        Optional<CommentHit> block = firstUsefulGroup(file, source, preamble, BLOCK_DOC);
        if (block.isPresent()) {
            return block;
        }

        if (isHashCommentLanguage(lang)) {
            return leadingLineComments(file, source, '#');
        }

        return leadingLineComments(file, source, '/');
    }

    /** Roughly the first 80 lines or until a type/function body opens. */
    private static String preamble(String source) {
        String[] lines = source.split("\\R", -1);
        StringBuilder out = new StringBuilder();
        int limit = Math.min(lines.length, 80);
        for (int i = 0; i < limit; i++) {
            out.append(lines[i]).append('\n');
            String trimmed = lines[i].trim();
            if (trimmed.endsWith("{") && !trimmed.startsWith("*") && !trimmed.startsWith("//")
                    && !trimmed.startsWith("#") && !trimmed.startsWith("/*")) {
                break;
            }
        }
        return out.toString();
    }

    private static Optional<CommentHit> firstUsefulGroup(Path file, String fullSource,
                                                         String searchIn, Pattern pattern) {
        Matcher m = pattern.matcher(searchIn);
        while (m.find()) {
            String sentence = cleanComment(m.group(1));
            if (sentence.isEmpty() || looksLikeLicenseBanner(sentence)) {
                continue;
            }
            int startLine = lineOfOffset(fullSource, m.start());
            int endLine = lineOfOffset(fullSource, Math.max(m.start(), m.end() - 1));
            return Optional.of(new CommentHit(sentence,
                    new SourceReference(file, startLine, endLine, sentence)));
        }
        return Optional.empty();
    }

    private static Optional<CommentHit> leadingLineComments(Path file, String source, char marker) {
        String[] lines = source.split("\\R", -1);
        StringBuilder raw = new StringBuilder();
        int startLine = -1;
        int endLine = -1;
        int i = 0;
        while (i < lines.length && lines[i].trim().isEmpty()) {
            i++;
        }
        // Skip shebang
        if (i < lines.length && lines[i].startsWith("#!")) {
            i++;
            while (i < lines.length && lines[i].trim().isEmpty()) {
                i++;
            }
        }
        // Skip package / import preamble for slash comments so file-level // above the type still counts
        // when it is the first comment; stop at first non-comment code.
        while (i < lines.length) {
            String trimmed = lines[i].trim();
            if (trimmed.isEmpty()) {
                if (startLine > 0) {
                    break;
                }
                i++;
                continue;
            }
            String body = lineCommentBody(trimmed, marker);
            if (body == null) {
                break;
            }
            if (startLine < 0) {
                startLine = i + 1;
            }
            endLine = i + 1;
            if (raw.length() > 0) {
                raw.append(' ');
            }
            raw.append(body);
            i++;
        }
        if (startLine < 0) {
            return Optional.empty();
        }
        // Ignore license banners that are only copyright lines
        String sentence = cleanComment(raw.toString());
        if (sentence.isEmpty() || looksLikeLicenseBanner(sentence)) {
            return Optional.empty();
        }
        return Optional.of(new CommentHit(sentence,
                new SourceReference(file, startLine, endLine, sentence)));
    }

    private static String lineCommentBody(String trimmed, char marker) {
        if (marker == '#') {
            if (!trimmed.startsWith("#") || trimmed.startsWith("#!")) {
                return null;
            }
            return trimmed.substring(1).trim();
        }
        if (!trimmed.startsWith("//")) {
            return null;
        }
        return trimmed.substring(2).trim();
    }

    static String cleanComment(String raw) {
        if (raw == null) {
            return "";
        }
        String cleaned = raw
                .replaceAll("(?m)^\\s*\\*+\\s?", "")
                .replaceAll("\\{@link\\s+([^}]+)}", "$1")
                .replaceAll("\\{@code\\s+([^}]+)}", "$1")
                .replaceAll("\\{@.*?}|</?[a-zA-Z][^>]*>", "")
                .replaceAll("\\s+", " ")
                .trim();
        // Drop leading Javadoc tags like @author on a one-line comment
        if (cleaned.startsWith("@")) {
            return "";
        }
        int end = cleaned.indexOf('.');
        return end < 0 ? cleaned : cleaned.substring(0, end + 1).trim();
    }

    private static boolean looksLikeLicenseBanner(String text) {
        String lower = text.toLowerCase();
        return lower.contains("copyright") || lower.contains("licensed under")
                || lower.contains("all rights reserved") || lower.startsWith("spdx-");
    }

    private static boolean isPythonFamily(String lang) {
        return "Python".equalsIgnoreCase(lang);
    }

    private static boolean isHashCommentLanguage(String lang) {
        return "Python".equalsIgnoreCase(lang)
                || "Ruby".equalsIgnoreCase(lang)
                || "Shell".equalsIgnoreCase(lang)
                || "Bash".equalsIgnoreCase(lang)
                || "YAML".equalsIgnoreCase(lang)
                || "Dockerfile".equalsIgnoreCase(lang)
                || "R".equalsIgnoreCase(lang)
                || "Perl".equalsIgnoreCase(lang)
                || "TOML".equalsIgnoreCase(lang);
    }

    private static int lineOfOffset(String source, int offset) {
        int line = 1;
        int n = Math.min(offset, source.length());
        for (int i = 0; i < n; i++) {
            if (source.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }

    static final class CommentHit {
        final String text;
        final SourceReference evidence;

        CommentHit(String text, SourceReference evidence) {
            this.text = text;
            this.evidence = evidence;
        }
    }
}
