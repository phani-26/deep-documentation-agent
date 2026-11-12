package com.deepdoc.docgen;

import com.deepdoc.graph.DependencyGraph;
import com.deepdoc.model.DetectedModule;
import com.deepdoc.model.DetectedPattern;
import com.deepdoc.model.RepositoryContext;
import com.deepdoc.model.SourceReference;
import com.deepdoc.ranker.DocLevel;
import com.deepdoc.understand.ExtractedFact;
import com.deepdoc.understand.FactKind;
import com.deepdoc.understand.UnderstandingResult;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/** Verification component (design-doc "verification" slice). */
public final class Verification {

    /** Confidence label applied to a claim. */
    public enum Status {
        VERIFIED("**VERIFIED**"),
        INFERRED("*INFERRED*"),
        UNKNOWN("*UNKNOWN*");

        private final String label;
        Status(String label) { this.label = label; }
        public String label() { return label; }
    }

    /** One checkable claim about the repository. */
    public static final class Claim {
        private final String category;
        private final String statement;
        private final Status status;
        private final List<SourceReference> evidence;
        private final String reasoning;

        Claim(String category, String statement, Status status,
              List<SourceReference> evidence, String reasoning) {
            this.category = category;
            this.statement = statement;
            this.status = status;
            this.evidence = evidence == null ? List.of() : List.copyOf(evidence);
            this.reasoning = reasoning == null ? "" : reasoning;
        }

        public String getCategory() { return category; }
        public String getStatement() { return statement; }
        public Status getStatus() { return status; }
        public List<SourceReference> getEvidence() { return evidence; }
        public String getReasoning() { return reasoning; }
        public boolean hasEvidence() { return !evidence.isEmpty(); }
    }

    /** Complete verification result. */
    public static final class Report {
        private final List<Claim> verified = new ArrayList<>();
        private final List<Claim> inferred = new ArrayList<>();
        private final List<Claim> unknown = new ArrayList<>();
        private final List<Claim> all = new ArrayList<>();

        Report() {}
        List<Claim> getVerified() { return verified; }
        List<Claim> getInferred() { return inferred; }
        List<Claim> getUnknown() { return unknown; }
        List<Claim> getAll() { return all; }
        int verifiedCount() { return verified.size(); }
        int inferredCount() { return inferred.size(); }
        int unknownCount() { return unknown.size(); }
        void add(Claim c) {
            all.add(c);
            if (c.status == Status.VERIFIED) verified.add(c);
            else if (c.status == Status.INFERRED) inferred.add(c);
            else unknown.add(c);
        }
    }
}

    /** Human-readable Markdown report. */
    public static String toMarkdown(Report report, String repoName) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Verification Report — ").append(repoName).append("\n\n");
        sb.append("Every documentation claim is traced to source evidence and labeled ")
                .append(Status.VERIFIED.label()).append(", ")
                .append(Status.INFERRED.label()).append(", or ")
                .append(Status.UNKNOWN.label()).append(".\n\n");
        sb.append("## Summary\n\n");
        sb.append("- **Verified claims:** ").append(report.verifiedCount()).append("\n");
        sb.append("- **Inferred claims:** ").append(report.inferredCount()).append("\n");
        sb.append("- **Unknown claims:** ").append(report.unknownCount()).append("\n");
        sb.append("- **Total claims:** ").append(report.getAll().size()).append("\n\n");
        writeSection(sb, "Verified claims", report.getVerified());
        writeSection(sb, "Inferred claims", report.getInferred());
        writeSection(sb, "Unknown claims", report.getUnknown());
        sb.append("## Method\n\n");
        sb.append("- Claims are checked against detected patterns, extracted code facts, and discovered files.\n");
        sb.append("- A claim is **VERIFIED** only when a source line or detector evidence directly supports it.\n");
        sb.append("- A claim is **INFERRED** when reasoned from file names, annotations, or static references.\n");
        sb.append("- A claim is **UNKNOWN** when no evidence could be found.\n");
        sb.append("- Static references are code dependencies, not proof of runtime call order.\n");
        return sb.toString();
    }

    private static void writeSection(StringBuilder sb, String title, List<Claim> claims) {
        sb.append("## ").append(title).append("\n\n");
        if (claims.isEmpty()) { sb.append("(none)\n\n"); return; }
        for (Claim c : claims) {
            sb.append("- ").append(c.getStatement()).append(" ").append(c.getStatus().label()).append("\n");
            if (!c.getEvidence().isEmpty()) {
                sb.append("  - Evidence: ")
                        .append(c.getEvidence().stream().map(r -> ref(r)).collect(Collectors.joining(", ")))
                        .append("\n");
            }
            if (!c.getReasoning().isBlank()) {
                sb.append("  - Reasoning: ").append(c.getReasoning()).append("\n");
            }
        }
        sb.append("\n");
    }

    private static String ref(SourceReference r) {
        Path file = r.getFile();
        String rel = file == null ? "unknown" : file.getFileName().toString();
        return rel + ":" + r.getStartLine();
    }

    /** Machine-readable JSON report. */
    public static String toJson(Report report, String repoName) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"repository\": ").append(jsonString(repoName)).append(",\n");
        sb.append("  \"generatedAt\": \"").append(Instant.now().toString()).append("\",\n");
        sb.append("  \"summary\": {\n");
        sb.append("    \"verified\": ").append(report.verifiedCount()).append(",\n");
        sb.append("    \"inferred\": ").append(report.inferredCount()).append(",\n");
        sb.append("    \"unknown\": ").append(report.unknownCount()).append(",\n");
        sb.append("    \"total\": ").append(report.getAll().size()).append("\n");
        sb.append("  },\n");
        sb.append("  \"claims\": [\n");
        boolean first = true;
        for (Claim c : report.getAll()) {
            if (!first) sb.append(",\n");
            first = false;
            sb.append("    {\n");
            sb.append("      \"category\": ").append(jsonString(c.getCategory())).append(",\n");
            sb.append("      \"statement\": ").append(jsonString(c.getStatement())).append(",\n");
            sb.append("      \"status\": ").append(jsonString(c.getStatus().name())).append(",\n");
            sb.append("      \"reasoning\": ").append(jsonString(c.getReasoning())).append(",\n");
            sb.append("      \"evidence\": [\n");
            boolean fe = true;
            for (SourceReference r : c.getEvidence()) {
                if (!fe) sb.append(",\n");
                fe = false;
                sb.append("        {\"file\": ").append(jsonString(r.getFile() == null ? "" : r.getFile().toString()))
                        .append(", \"line\": ").append(r.getStartLine()).append("}");
            }
            sb.append("\n      ]\n    }");
        }
        sb.append("\n  ]\n}\n");
        return sb.toString();
    }

    private static String jsonString(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r") + "\"";
    }
}

    /** Run verification, handling null understanding gracefully. */
    public static Report verify(DocumentationPlan plan, RepositoryContext context,
                                DependencyGraph graph, UnderstandingResult understanding) {
        Report report = new Report();
        if (context != null) {
            verifyArchitecture(report, context);
            verifyFrameworks(report, context);
            verifyTechnologies(report, context);
            verifyModules(report, context);
            verifyPatterns(report, context);
        }
        if (understanding != null && plan != null) {
            verifyCapabilities(report, plan, understanding);
            verifyWorkflows(report, plan, understanding, graph);
        }
        recordUnknowns(report, plan, context, graph);
        return report;
    }
