package com.deepdoc.docgen;

import com.deepdoc.graph.DependencyEdge;
import com.deepdoc.graph.DependencyGraph;
import com.deepdoc.model.FileInfo;
import com.deepdoc.ranker.DocLevel;
import com.deepdoc.ranker.EntryPointDetector;
import com.deepdoc.understand.ExtractedFact;
import com.deepdoc.understand.FactKind;
import org.jgrapht.Graph;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Builds a walkthrough of how the repository runs: start at controllers, application
 * classes, and UI roots, then follow static dependencies. Load tests and scripts are
 * listed apart from that path.
 */
public final class HowItWorks {
    static final int MAX_DEPTH = 4;
    static final int MAX_STEPS = 8;
    static final int MAX_CALLEES = 3;
    static final int MAX_TRACES_PER_MODULE = 6;

    private static final Set<String> ENTRY_ANNOTATIONS = Set.of(
            "RestController", "Controller", "SpringBootApplication", "WebServlet",
            "KafkaListener", "RabbitListener", "JmsListener", "Path", "Command",
            "ControllerAdvice");

    private HowItWorks() {
    }

    /** Evidence the model sees. Paths and facts only; no invented hops. */
    public static String promptBody(String architecture, DocumentationPlan plan, DependencyGraph graph) {
        return promptBody(architecture, build(plan, fileGraph(graph), detectedEntries(graph)));
    }

    /** Same evidence, with an explicit file graph and no source-based entry detection. */
    static String promptBody(String architecture, DocumentationPlan plan, Graph<FileInfo, DependencyEdge> files) {
        return promptBody(architecture, build(plan, files, Set.of()));
    }

    private static String promptBody(String architecture, Model model) {
        StringBuilder sb = new StringBuilder();
        sb.append("Detected architecture: ").append(architecture == null ? "Unknown" : architecture).append("\n\n");
        if (model.traces.isEmpty()) {
            sb.append("NO ENTRY POINTS\n");
            appendCentral(sb, model.central);
        } else {
            String module = null;
            for (Trace trace : model.traces) {
                if (!trace.module.equals(module)) {
                    module = trace.module;
                    sb.append("MODULE ").append(module).append('\n');
                }
                appendTrace(sb, trace);
            }
        }
        if (!model.auxiliary.isEmpty()) {
            sb.append("AUXILIARY (tools and scripts, not the running request path)\n");
            for (FileDocEntry entry : model.auxiliary) {
                sb.append("- ").append(entry.getRelativePath()).append(" — ").append(entry.oneLiner()).append('\n');
            }
            sb.append('\n');
        }
        sb.append("Static edges only. A client file ends the chain; the remote service is not an import.\n");
        return sb.toString();
    }

    /** Readable page used when the model is off or a call fails. */
    public static String factual(String architecture, DocumentationPlan plan, DependencyGraph graph) {
        return factual(architecture, build(plan, fileGraph(graph), detectedEntries(graph)));
    }

    /** Same page, with an explicit file graph and no source-based entry detection. */
    static String factual(String architecture, DocumentationPlan plan, Graph<FileInfo, DependencyEdge> files) {
        return factual(architecture, build(plan, files, Set.of()));
    }

    private static String factual(String architecture, Model model) {
        String label = architecture == null ? "Unknown" : architecture;
        StringBuilder sb = new StringBuilder();
        sb.append("# How this repository works\n\n");
        sb.append("Detected architecture: **").append(label).append("**. ");
        sb.append("This page walks each entry file and the files it references.\n\n");
        sb.append("## Purpose and Scope\n\n");
        sb.append("The walk starts at entry points such as HTTP controllers, application classes, listeners, and UI roots. ");
        sb.append("A nested file is one the parent file references. ");
        sb.append("Calls that leave this repository are marked Inferred and end the chain.\n\n");
        sb.append("## Overview\n\n");
        if (model.traces.isEmpty()) {
            sb.append("No controller, application class, listener, or UI root was found.\n\n");
            sb.append("## Central files\n\n");
            if (model.central.isEmpty()) {
                sb.append("The plan has no ranked files to follow.\n");
            } else {
                for (Trace trace : model.central) {
                    appendFactualTrace(sb, trace);
                }
            }
        } else {
            String module = null;
            for (Trace trace : model.traces) {
                if (!trace.module.equals(module)) {
                    module = trace.module;
                    sb.append("## ").append(module).append("\n\n");
                }
                appendFactualTrace(sb, trace);
            }
        }
        if (!model.auxiliary.isEmpty()) {
            sb.append("## Tools and scripts\n\n");
            sb.append("These files are executable utilities. They are not on the request path.\n\n");
            for (FileDocEntry entry : model.auxiliary) {
                sb.append("- `").append(entry.getRelativePath()).append("` — ").append(entry.oneLiner()).append('\n');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Overview page in the shape used by DeepWiki and OpenDeepWiki: purpose, overview,
     * an architecture diagram, a layout table, then the main request flow.
     * Built from the same traces as {@link #factual}.
     */
    public static String forFresher(String architecture, DocumentationPlan plan, DependencyGraph graph) {
        return forFresher(architecture, plan, build(plan, fileGraph(graph), detectedEntries(graph)));
    }

    /** Same page, with an explicit file graph and no source-based entry detection. */
    static String forFresher(String architecture, DocumentationPlan plan, Graph<FileInfo, DependencyEdge> files) {
        return forFresher(architecture, plan, build(plan, files, Set.of()));
    }

    private static String forFresher(String architecture, DocumentationPlan plan, Model model) {
        String label = architecture == null ? "Unknown" : architecture;
        List<Trace> traces = model.traces.isEmpty() ? model.central : model.traces;
        Map<String, List<Trace>> byModule = groupByModule(traces);
        StringBuilder sb = new StringBuilder();
        sb.append("# What this repository does\n\n");
        sb.append("This repository is **").append(label).append("**. ");
        sb.append("The page explains the parts the scan found and the order in which one file references the next.\n\n");

        sb.append("## Purpose and Scope\n\n");
        sb.append("This page covers the repository overview, the architecture visible in the static graph, ");
        sb.append("and the longest in-repository flow. ");
        sb.append("The file-by-file walk is in [how-it-works.md](how-it-works.md). ");
        sb.append("Package dependencies are in [diagrams.md](diagrams.md). ");
        sb.append("Configuration values, API signatures, and source excerpts are omitted when this scan did not extract them.\n\n");

        sb.append("## Overview\n\n");
        if (model.traces.isEmpty() && !traces.isEmpty()) {
            sb.append("No controller, application class, listener, or UI root was found. ");
            sb.append("The central files below are the ones the ranker kept.\n\n");
        } else if (traces.isEmpty()) {
            sb.append("No controller, application class, listener, UI root, or ranked source file was found.\n\n");
        } else {
            sb.append("Someone new to the code can treat each part below as one job. ");
            sb.append("Open the file in the Starts at column, then follow the files it uses.\n\n");
        }
        appendModuleBullets(sb, byModule);

        sb.append("## Architecture\n\n");
        sb.append("An arrow means the file on the left references the file on the right. ");
        sb.append("The arrow comes from the static graph. A call that leaves this repository is not drawn.\n\n");
        sb.append("```mermaid\n");
        sb.append(architectureDiagram(byModule));
        sb.append("```\n\n");

        sb.append("The table lists each part and the file where that part starts.\n\n");
        sb.append("| Part | Starts at | What it is |\n|---|---|---|\n");
        if (byModule.isEmpty()) {
            sb.append("| — | — | No entry file was found |\n");
        } else {
            for (Map.Entry<String, List<Trace>> module : byModule.entrySet()) {
                Trace first = module.getValue().get(0);
                sb.append("| `").append(module.getKey()).append("` | `")
                        .append(first.entry.getRelativePath()).append("` | ")
                        .append(escapeCell(first.entry.oneLiner())).append(" |\n");
            }
        }
        sb.append('\n');

        sb.append("## Main content\n\n");
        sb.append("| Kind | What the scan found |\n|---|---|\n");
        sb.append("| Architecture | ").append(label).append(" |\n");
        sb.append("| Languages | ").append(languages(plan)).append(" |\n\n");
        if (byModule.isEmpty()) {
            sb.append("No module had an entry file to describe.\n\n");
        } else {
            for (Map.Entry<String, List<Trace>> module : byModule.entrySet()) {
                sb.append("### ").append(module.getKey()).append("\n\n");
                for (Trace trace : module.getValue()) {
                    appendFresherTrace(sb, trace);
                }
            }
        }

        if (!model.auxiliary.isEmpty()) {
            sb.append("### Tools, not the product\n\n");
            sb.append("These files run as scripts or load tests. A user request does not go through them.\n\n");
            for (FileDocEntry entry : model.auxiliary) {
                sb.append("- `").append(entry.getRelativePath()).append("` — ").append(entry.oneLiner()).append('\n');
            }
            sb.append('\n');
        }

        Trace richest = richestTrace(traces);
        sb.append("## Core Flow\n\n");
        if (richest == null || richest.steps.isEmpty()) {
            sb.append("No entry file imports another file in this repository, so there is no in-repo flow to draw.\n\n");
        } else {
            sb.append("The longest chain from an entry file is in `").append(richest.module).append("`.\n\n");
            sb.append("```mermaid\n");
            sb.append(flowDiagram(richest));
            sb.append("```\n\n");
            sb.append("1. A request starts at `").append(richest.entry.getRelativePath()).append("` — ")
                    .append(sentence(richest.entry.oneLiner())).append('\n');
            int n = 2;
            for (Step step : richest.steps) {
                sb.append(n++).append(". `").append(step.relativePath).append('`');
                if (!step.oneLiner.isEmpty()) {
                    sb.append(" — ").append(sentence(step.oneLiner));
                }
                if (step.client) {
                    sb.append(" Inferred: this call leaves the repository.");
                }
                sb.append('\n');
            }
            sb.append('\n');
        }
        sb.append(pathsToOpen(model));

        sb.append("## Related links\n\n");
        sb.append("- [how-it-works.md](how-it-works.md) — the same paths, with more detail\n");
        sb.append("- [diagrams.md](diagrams.md) — which package depends on which\n");
        sb.append("- [README.md](README.md) — index of the generated pages\n");
        sb.append("- [coverage.md](coverage.md) — every discovered file\n");
        return sb.toString();
    }

    /**
     * Numbered file order for a reader on their first day.
     * Step 1 is the entry file. Each later step is a file that entry references.
     */
    public static String followTheseFiles(String architecture, DocumentationPlan plan, DependencyGraph graph) {
        return followTheseFiles(architecture, plan, fileGraph(graph));
    }

    /** Same order, with an explicit file graph and no source-based entry detection. */
    static String followTheseFiles(String architecture, DocumentationPlan plan, Graph<FileInfo, DependencyEdge> files) {
        return "## Core Flow\n\n" + pathsToOpen(build(plan, files, Set.of()));
    }

    private static String pathsToOpen(Model model) {
        List<Trace> traces = model.traces.isEmpty() ? model.central : model.traces;
        StringBuilder sb = new StringBuilder();
        sb.append("Read each list from top to bottom. ");
        sb.append("The first file is where that part of the system starts. ");
        sb.append("The next file is one the file above it references. ");
        sb.append("The order comes from the static dependency graph.\n\n");
        if (traces.isEmpty()) {
            sb.append("No entry file was found to follow.\n\n");
            return sb.toString();
        }
        String module = null;
        for (Trace trace : traces) {
            if (!trace.module.equals(module)) {
                module = trace.module;
                sb.append("### ").append(module).append("\n\n");
            }
            int n = 1;
            sb.append(n++).append(". `").append(trace.entry.getRelativePath()).append("` — ")
                    .append(sentence(trace.entry.oneLiner()));
            String role = fresherRole(trace.entry);
            if (!role.isEmpty()) {
                sb.append(' ').append(role);
            }
            sb.append('\n');
            for (Step step : trace.steps) {
                sb.append(n++).append(". `").append(step.relativePath).append('`');
                if (!step.oneLiner.isEmpty()) {
                    sb.append(" — ").append(sentence(step.oneLiner));
                }
                if (step.client) {
                    sb.append(" Inferred: the next hop leaves this repository.");
                }
                sb.append('\n');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Entry-point chains the repository guide turns into a request flow. */
    static Flows flows(DocumentationPlan plan, DependencyGraph graph) {
        return flows(build(plan, fileGraph(graph), detectedEntries(graph)));
    }

    /** Same chains, with an explicit file graph and no source-based entry detection. */
    static Flows flows(DocumentationPlan plan, Graph<FileInfo, DependencyEdge> files) {
        return flows(build(plan, files, Set.of()));
    }

    private static Flows flows(Model model) {
        boolean centralOnly = model.traces.isEmpty();
        List<Trace> source = centralOnly ? model.central : model.traces;
        List<Flow> flows = new ArrayList<>();
        for (Trace trace : source) {
            List<Hop> hops = new ArrayList<>();
            for (Step step : trace.steps) {
                hops.add(new Hop(step.depth, step.relativePath, step.oneLiner, step.client));
            }
            flows.add(new Flow(trace.module, trace.entry, hops));
        }
        return new Flows(flows, centralOnly, model.auxiliary);
    }

    static final class Flows {
        final List<Flow> flows;
        final boolean centralOnly;
        final List<FileDocEntry> auxiliary;

        Flows(List<Flow> flows, boolean centralOnly, List<FileDocEntry> auxiliary) {
            this.flows = flows;
            this.centralOnly = centralOnly;
            this.auxiliary = auxiliary;
        }
    }

    static final class Flow {
        final String module;
        final FileDocEntry entry;
        final List<Hop> hops;

        Flow(String module, FileDocEntry entry, List<Hop> hops) {
            this.module = module;
            this.entry = entry;
            this.hops = hops;
        }
    }

    static final class Hop {
        final int depth;
        final String path;
        final String oneLiner;
        final boolean client;

        Hop(int depth, String path, String oneLiner, boolean client) {
            this.depth = depth;
            this.path = path;
            this.oneLiner = oneLiner;
            this.client = client;
        }
    }

    private static Map<String, List<Trace>> groupByModule(List<Trace> traces) {
        Map<String, List<Trace>> byModule = new LinkedHashMap<>();
        for (Trace trace : traces) {
            byModule.computeIfAbsent(trace.module, key -> new ArrayList<>()).add(trace);
        }
        return byModule;
    }

    private static void appendModuleBullets(StringBuilder sb, Map<String, List<Trace>> byModule) {
        if (byModule.isEmpty()) {
            return;
        }
        for (Map.Entry<String, List<Trace>> module : byModule.entrySet()) {
            Trace first = module.getValue().get(0);
            sb.append("- **").append(module.getKey()).append("** — ")
                    .append(sentence(first.entry.oneLiner())).append('\n');
        }
        sb.append('\n');
    }

    private static String architectureDiagram(Map<String, List<Trace>> byModule) {
        StringBuilder sb = new StringBuilder();
        sb.append("flowchart TD\n");
        if (byModule.isEmpty()) {
            sb.append("  empty[\"No entry files\"]\n");
            return sb.toString();
        }
        int i = 0;
        for (Map.Entry<String, List<Trace>> module : byModule.entrySet()) {
            String id = "m" + i++;
            String entryName = fileName(module.getValue().get(0).entry.getRelativePath());
            sb.append("  ").append(id).append("[\"").append(mermaidLabel(entryName)).append("\"]\n");
            int s = 0;
            String previous = id;
            String previousLabel = entryName;
            for (Trace trace : module.getValue()) {
                if (trace.steps.isEmpty() || s >= 2) {
                    continue;
                }
                String stepName = fileName(trace.steps.get(0).relativePath);
                if (stepName.equals(previousLabel)) {
                    continue;
                }
                String sid = id + "s" + s++;
                sb.append("  ").append(sid).append("[\"").append(mermaidLabel(stepName)).append("\"]\n");
                sb.append("  ").append(previous).append(" --> ").append(sid).append('\n');
                previous = sid;
                previousLabel = stepName;
            }
        }
        return sb.toString();
    }

    private static String flowDiagram(Trace trace) {
        StringBuilder sb = new StringBuilder();
        sb.append("sequenceDiagram\n");
        sb.append("  participant E as ").append(mermaidLabel(fileName(trace.entry.getRelativePath()))).append('\n');
        int i = 0;
        String previous = "E";
        for (Step step : trace.steps) {
            if (i >= 4) {
                break;
            }
            String id = "S" + i++;
            sb.append("  participant ").append(id).append(" as ")
                    .append(mermaidLabel(fileName(step.relativePath))).append('\n');
            sb.append("  ").append(previous).append("->>").append(id).append(": uses\n");
            previous = id;
        }
        return sb.toString();
    }

    private static Trace richestTrace(List<Trace> traces) {
        Trace best = null;
        for (Trace trace : traces) {
            if (trace.steps.isEmpty()) {
                continue;
            }
            if (best == null || trace.steps.size() > best.steps.size()) {
                best = trace;
            }
        }
        return best;
    }

    private static String languages(DocumentationPlan plan) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (FileDocEntry entry : plan.getEntries()) {
            String name = fileName(entry.getRelativePath()).toLowerCase(Locale.ROOT);
            String language = languageOf(name);
            if (language != null) {
                counts.merge(language, 1, Integer::sum);
            }
        }
        if (counts.isEmpty()) {
            return "None detected";
        }
        List<String> names = new ArrayList<>(counts.keySet());
        names.sort(Comparator.comparingInt((String name) -> counts.get(name)).reversed().thenComparing(name -> name));
        return String.join(", ", names);
    }

    private static String languageOf(String fileName) {
        if (fileName.endsWith(".java")) {
            return "Java";
        }
        if (fileName.endsWith(".kt")) {
            return "Kotlin";
        }
        if (fileName.endsWith(".ts") || fileName.endsWith(".tsx") || fileName.endsWith(".js") || fileName.endsWith(".jsx")) {
            return "TypeScript";
        }
        if (fileName.endsWith(".py")) {
            return "Python";
        }
        if (fileName.endsWith(".go")) {
            return "Go";
        }
        if (fileName.endsWith(".cs")) {
            return "C#";
        }
        if (fileName.endsWith(".rs")) {
            return "Rust";
        }
        return null;
    }

    private static String mermaidLabel(String text) {
        return text.replace("\"", "'").replace("\n", " ");
    }

    private static String escapeCell(String text) {
        return text.replace("|", "\\|").replace("\n", " ");
    }

    private static void appendFresherTrace(StringBuilder sb, Trace trace) {
        FileDocEntry entry = trace.entry;
        sb.append("A request starts at `").append(entry.getRelativePath()).append("`.\n\n");
        sb.append(sentence(entry.oneLiner()));
        String role = fresherRole(entry);
        if (!role.isEmpty()) {
            sb.append(' ').append(role);
        }
        appendMethods(sb, methodNames(notes(entry)));
        sb.append("\n\n");
        if (trace.steps.isEmpty()) {
            sb.append("Nothing else in this repository is imported from this file.\n");
            if (isBootstrap(entry)) {
                sb.append("Inferred: starting this file starts the service. The framework connects the other classes.\n");
            }
            sb.append('\n');
            return;
        }
        sb.append("From there the code uses:\n\n");
        for (Step step : trace.steps) {
            sb.append("  ".repeat(Math.max(0, step.depth - 1))).append("- `").append(step.relativePath).append('`');
            if (!step.oneLiner.isEmpty()) {
                sb.append(" — ").append(sentence(step.oneLiner));
            }
            appendMethods(sb, methodNames(step.notes));
            sb.append('\n');
            if (step.client) {
                sb.append("  ".repeat(step.depth))
                        .append("- Inferred: this file calls another service. That call leaves this repository.\n");
            }
        }
        sb.append('\n');
    }

    private static String fresherRole(FileDocEntry entry) {
        for (ExtractedFact fact : entry.getFacts()) {
            if (fact.getKind() != FactKind.ANNOTATED) {
                continue;
            }
            switch (fact.getObject()) {
                case "RestController":
                case "Controller":
                    return "This file receives HTTP requests.";
                case "SpringBootApplication":
                    return "This file starts the service.";
                case "KafkaListener":
                case "RabbitListener":
                case "JmsListener":
                    return "This file receives messages.";
                default:
                    break;
            }
        }
        return "";
    }

    private static List<String> methodNames(List<String> notes) {
        List<String> methods = new ArrayList<>();
        for (String note : notes) {
            if (note.startsWith("method: ") && methods.size() < 4) {
                methods.add(note.substring("method: ".length()));
            }
        }
        return methods;
    }

    private static void appendMethods(StringBuilder sb, List<String> methods) {
        if (methods.isEmpty()) {
            return;
        }
        sb.append(" Methods here include");
        for (int i = 0; i < methods.size(); i++) {
            sb.append(i == 0 ? " `" : ", `").append(methods.get(i)).append('`');
        }
        sb.append('.');
    }

    private static String sentence(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        char last = text.charAt(text.length() - 1);
        if (last == '.' || last == '!' || last == '?') {
            return text;
        }
        return text + ".";
    }

    private static void appendTrace(StringBuilder sb, Trace trace) {
        FileDocEntry entry = trace.entry;
        sb.append("ENTRY ").append(entry.getRelativePath()).append('\n');
        sb.append("  one-liner: ").append(entry.oneLiner()).append('\n');
        for (String note : notes(entry)) {
            sb.append("  ").append(note).append('\n');
        }
        if (trace.steps.isEmpty()) {
            sb.append("  steps: (none in this repository)\n");
            if (isBootstrap(entry)) {
                sb.append("  Inferred: this class starts the process; the framework wires other types without an import edge.\n");
            }
        } else {
            sb.append("  steps:\n");
            for (Step step : trace.steps) {
                sb.append("  ".repeat(step.depth)).append("- ").append(step.relativePath);
                if (!step.oneLiner.isEmpty()) {
                    sb.append(" — ").append(step.oneLiner);
                }
                sb.append('\n');
                for (String note : step.notes) {
                    sb.append("  ".repeat(step.depth + 1)).append(note).append('\n');
                }
                if (step.client) {
                    sb.append("  ".repeat(step.depth + 1))
                            .append("Inferred: client file. The graph stops here; the remote service is not an import.\n");
                }
                if (!step.module.equals(trace.module)) {
                    sb.append("  ".repeat(step.depth + 1))
                            .append("Lives in module ").append(step.module).append(".\n");
                }
            }
        }
        sb.append('\n');
    }

    private static void appendFactualTrace(StringBuilder sb, Trace trace) {
        FileDocEntry entry = trace.entry;
        sb.append("### `").append(entry.getRelativePath()).append("`\n\n");
        sb.append(entry.oneLiner()).append("\n\n");
        for (String note : notes(entry)) {
            sb.append("- ").append(capitalize(note)).append('\n');
        }
        if (!notes(entry).isEmpty()) {
            sb.append('\n');
        }
        if (trace.steps.isEmpty()) {
            sb.append("No in-repository callees.\n");
            if (isBootstrap(entry)) {
                sb.append("Inferred: this class starts the process, and the framework wires the other types.\n");
            }
            sb.append('\n');
            return;
        }
        sb.append("Then:\n\n");
        for (Step step : trace.steps) {
            sb.append("  ".repeat(step.depth - 1)).append("- `").append(step.relativePath).append('`');
            if (!step.oneLiner.isEmpty()) {
                sb.append(" — ").append(step.oneLiner);
            }
            sb.append('\n');
            for (String note : step.notes) {
                sb.append("  ".repeat(step.depth)).append("- ").append(capitalize(note)).append('\n');
            }
            if (step.client) {
                sb.append("  ".repeat(step.depth))
                        .append("- Inferred: this is a client. The static graph stops here; the remote service is not an import.\n");
            }
            if (!step.module.equals(trace.module)) {
                sb.append("  ".repeat(step.depth))
                        .append("- This file lives in `").append(step.module).append("`.\n");
            }
        }
        sb.append('\n');
    }

    private static void appendCentral(StringBuilder sb, List<Trace> central) {
        for (Trace trace : central) {
            appendTrace(sb, trace);
        }
    }

    private static Graph<FileInfo, DependencyEdge> fileGraph(DependencyGraph graph) {
        return graph == null ? null : graph.getFileGraph();
    }

    private static Model build(DocumentationPlan plan, Graph<FileInfo, DependencyEdge> files, Set<Path> detected) {
        Map<Path, FileDocEntry> byPath = new HashMap<>();
        for (FileDocEntry entry : plan.getEntries()) {
            byPath.put(entry.getFile().getFilePath(), entry);
        }

        List<FileDocEntry> auxiliary = new ArrayList<>();
        List<Trace> traces = new ArrayList<>();
        for (FileDocEntry entry : plan.getEntries()) {
            if (isAuxiliary(entry.getRelativePath()) || !isSourceName(fileName(entry.getRelativePath()))) {
                if (isAuxiliary(entry.getRelativePath()) && entry.getLevel() != DocLevel.LISTED) {
                    auxiliary.add(entry);
                }
                continue;
            }
            boolean detectedEntry = detected.contains(entry.getFile().getFilePath());
            if (!isProductEntry(entry) && !detectedEntry) {
                continue;
            }
            String module = moduleOf(entry.getRelativePath());
            traces.add(new Trace(module, entry, walk(entry, files, byPath)));
        }
        traces.sort(Comparator
                .comparingInt((Trace t) -> moduleRank(t.module))
                .thenComparing(t -> t.module)
                .thenComparingInt(t -> entryRank(t.entry))
                .thenComparing(t -> t.entry.getRelativePath()));
        List<Trace> capped = new ArrayList<>();
        Map<String, Integer> kept = new HashMap<>();
        for (Trace trace : traces) {
            int count = kept.getOrDefault(trace.module, 0);
            if (count >= MAX_TRACES_PER_MODULE) {
                continue;
            }
            kept.put(trace.module, count + 1);
            capped.add(trace);
        }
        traces = capped;
        auxiliary.sort(Comparator.comparing(FileDocEntry::getRelativePath));

        List<Trace> central = List.of();
        if (traces.isEmpty()) {
            central = centralTraces(plan, files, byPath);
        }
        return new Model(traces, central, auxiliary);
    }

    private static List<Trace> centralTraces(DocumentationPlan plan,
                                             Graph<FileInfo, DependencyEdge> files,
                                             Map<Path, FileDocEntry> byPath) {
        List<Trace> traces = new ArrayList<>();
        for (FileDocEntry entry : plan.at(DocLevel.DEEP)) {
            if (traces.size() >= MAX_TRACES_PER_MODULE) {
                break;
            }
            if (isAuxiliary(entry.getRelativePath())) {
                continue;
            }
            traces.add(new Trace(moduleOf(entry.getRelativePath()), entry, walk(entry, files, byPath)));
        }
        return traces;
    }

    private static List<Step> walk(FileDocEntry entry,
                                   Graph<FileInfo, DependencyEdge> files,
                                   Map<Path, FileDocEntry> byPath) {
        List<Step> steps = new ArrayList<>();
        Set<Path> seen = new HashSet<>();
        seen.add(entry.getFile().getFilePath());
        walkFrom(entry.getFile(), 1, files, byPath, seen, steps);
        return steps;
    }

    private static void walkFrom(FileInfo file, int depth,
                                 Graph<FileInfo, DependencyEdge> files,
                                 Map<Path, FileDocEntry> byPath,
                                 Set<Path> seen,
                                 List<Step> steps) {
        if (depth > MAX_DEPTH || steps.size() >= MAX_STEPS) {
            return;
        }
        for (FileInfo callee : callees(files, file, byPath)) {
            if (steps.size() >= MAX_STEPS || !seen.add(callee.getFilePath())) {
                continue;
            }
            FileDocEntry entry = byPath.get(callee.getFilePath());
            String relative = entry == null ? callee.getFilePath().getFileName().toString() : entry.getRelativePath();
            String oneLiner = entry == null ? "" : entry.oneLiner();
            boolean client = isClient(entry, relative);
            steps.add(new Step(depth, moduleOf(relative), relative, oneLiner, notes(entry), client));
            if (!client) {
                walkFrom(callee, depth + 1, files, byPath, seen, steps);
            }
        }
    }

    private static List<FileInfo> callees(Graph<FileInfo, DependencyEdge> files,
                                          FileInfo from,
                                          Map<Path, FileDocEntry> byPath) {
        if (files == null || !files.containsVertex(from)) {
            return List.of();
        }
        List<FileInfo> next = new ArrayList<>();
        for (DependencyEdge edge : files.outgoingEdgesOf(from)) {
            if (edge.isCoChange()) {
                continue;
            }
            FileInfo to = files.getEdgeTarget(edge);
            FileDocEntry entry = byPath.get(to.getFilePath());
            if (entry != null && isAuxiliary(entry.getRelativePath())) {
                continue;
            }
            next.add(to);
        }
        addImplementors(files, from, byPath, next);
        next.sort(Comparator
                .comparingInt((FileInfo f) -> role(f, byPath))
                .thenComparing(f -> f.getFilePath().toString()));
        if (next.size() > MAX_CALLEES) {
            return next.subList(0, MAX_CALLEES);
        }
        return next;
    }

    /**
     * An interface edge points at the contract. The class that implements it depends on the
     * interface, so the running path continues along that incoming edge.
     */
    private static void addImplementors(Graph<FileInfo, DependencyEdge> files, FileInfo contract,
                                        Map<Path, FileDocEntry> byPath, List<FileInfo> next) {
        FileDocEntry self = byPath.get(contract.getFilePath());
        if (!isInterface(self)) {
            return;
        }
        for (DependencyEdge edge : files.incomingEdgesOf(contract)) {
            if (edge.isCoChange()) {
                continue;
            }
            FileInfo source = files.getEdgeSource(edge);
            if (next.contains(source)) {
                continue;
            }
            FileDocEntry entry = byPath.get(source.getFilePath());
            if (entry == null || isAuxiliary(entry.getRelativePath()) || !implementsContract(entry, self)) {
                continue;
            }
            next.add(source);
        }
    }

    private static boolean isInterface(FileDocEntry entry) {
        if (entry == null) {
            return false;
        }
        for (ExtractedFact fact : entry.getFacts()) {
            if (fact.getKind() == FactKind.DECLARES_TYPE && "interface".equalsIgnoreCase(fact.getObject())) {
                return true;
            }
        }
        String liner = entry.oneLiner().toLowerCase(Locale.ROOT);
        return liner.contains("contract") || liner.contains("interface");
    }

    private static boolean implementsContract(FileDocEntry impl, FileDocEntry contract) {
        String contractType = typeName(contract.getRelativePath());
        String implFile = fileName(impl.getRelativePath());
        if (implFile.startsWith(contractType + "Impl.")) {
            return true;
        }
        for (ExtractedFact fact : impl.getFacts()) {
            if (fact.getKind() == FactKind.IMPLEMENTS && contractType.equals(fact.getObject())) {
                return true;
            }
        }
        return false;
    }

    private static String typeName(String relativePath) {
        String name = fileName(relativePath);
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }

    private static Set<Path> detectedEntries(DependencyGraph graph) {
        if (graph == null) {
            return Set.of();
        }
        Set<Path> paths = new HashSet<>();
        for (FileInfo file : EntryPointDetector.detect(graph)) {
            paths.add(file.getFilePath());
        }
        return paths;
    }

    static boolean isProductEntry(FileDocEntry entry) {
        if (isAuxiliary(entry.getRelativePath()) || !isSourceName(fileName(entry.getRelativePath()))) {
            return false;
        }
        String name = fileName(entry.getRelativePath()).toLowerCase(Locale.ROOT);
        if (name.contains("controller.") || name.endsWith("application.java") || name.endsWith("application.kt")
                || name.equals("main.tsx") || name.equals("app.tsx") || name.equals("index.tsx")
                || name.equals("page.tsx") || name.equals("app.jsx") || name.equals("main.jsx")
                || name.equals("routes.ts") || name.equals("routes.tsx")
                || name.equals("router.ts") || name.equals("router.tsx")
                || name.contains("listener.") || name.contains("consumer.")
                || name.contains("delegateimpl.")) {
            return true;
        }
        for (ExtractedFact fact : entry.getFacts()) {
            if (fact.getKind() == FactKind.ANNOTATED && ENTRY_ANNOTATIONS.contains(fact.getObject())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSourceName(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        return lower.endsWith(".java") || lower.endsWith(".kt") || lower.endsWith(".kts")
                || lower.endsWith(".scala") || lower.endsWith(".ts") || lower.endsWith(".tsx")
                || lower.endsWith(".js") || lower.endsWith(".jsx") || lower.endsWith(".py")
                || lower.endsWith(".go") || lower.endsWith(".cs") || lower.endsWith(".rb")
                || lower.endsWith(".php") || lower.endsWith(".rs");
    }

    static boolean isAuxiliary(String relativePath) {
        String path = relativePath.toLowerCase(Locale.ROOT).replace('\\', '/');
        return path.contains("load_test") || path.contains("loadtest")
                || path.contains("benchmark") || path.contains("/bench")
                || path.contains("/scripts/") || path.contains("/script/");
    }

    static String moduleOf(String relativePath) {
        String normalized = relativePath.replace('\\', '/');
        String[] parts = normalized.split("/");
        String specific = null;
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (part.isBlank() || part.equals("src") || part.equals("main") || part.equals("java")
                    || part.equals("test")) {
                continue;
            }
            String lower = part.toLowerCase(Locale.ROOT);
            if (lower.endsWith("-service") || lower.endsWith("-ui") || lower.endsWith("-server")
                    || lower.contains("gateway") || lower.equals("frontend") || lower.equals("web")) {
                specific = part;
            }
        }
        if (specific != null) {
            return specific;
        }
        for (int i = 0; i < parts.length - 1; i++) {
            if (!parts[i].isBlank()) {
                return parts[i];
            }
        }
        return "(root)";
    }

    private static int moduleRank(String module) {
        String lower = module.toLowerCase(Locale.ROOT);
        if (lower.contains("gateway")) {
            return 0;
        }
        if (lower.endsWith("-ui") || lower.contains("frontend") || lower.equals("web")) {
            return 1;
        }
        return 2;
    }

    private static int entryRank(FileDocEntry entry) {
        String name = fileName(entry.getRelativePath()).toLowerCase(Locale.ROOT);
        if (name.contains("controller") || name.contains("route") || name.contains("router")) {
            return 0;
        }
        if (name.endsWith(".tsx") || name.endsWith(".jsx")) {
            return 1;
        }
        if (name.contains("listener") || name.contains("consumer")) {
            return 2;
        }
        return 3;
    }

    private static int role(FileInfo file, Map<Path, FileDocEntry> byPath) {
        FileDocEntry entry = byPath.get(file.getFilePath());
        String name = file.getFilePath().getFileName().toString().toLowerCase(Locale.ROOT);
        String liner = entry == null ? "" : entry.oneLiner().toLowerCase(Locale.ROOT);
        if (name.contains("service") || liner.contains("service")) {
            return 0;
        }
        if (name.contains("repository") || name.contains("repo")) {
            return 1;
        }
        if (name.contains("client") || liner.contains("client") || liner.contains("feign")) {
            return 2;
        }
        if (name.contains("mapper") || name.contains("validator")) {
            return 3;
        }
        return 4;
    }

    private static boolean isClient(FileDocEntry entry, String relative) {
        String name = fileName(relative).toLowerCase(Locale.ROOT);
        String liner = entry == null ? "" : entry.oneLiner().toLowerCase(Locale.ROOT);
        return name.contains("client") || liner.contains("client") || liner.contains("feign");
    }

    private static boolean isBootstrap(FileDocEntry entry) {
        String name = fileName(entry.getRelativePath()).toLowerCase(Locale.ROOT);
        if (name.contains("application.")) {
            return true;
        }
        for (ExtractedFact fact : entry.getFacts()) {
            if (fact.getKind() == FactKind.ANNOTATED && "SpringBootApplication".equals(fact.getObject())) {
                return true;
            }
        }
        return false;
    }

    private static final Set<String> SKIP_ANNOTATIONS = Set.of(
            "Getter", "Setter", "Data", "Value", "Builder", "Slf4j", "Override", "SuppressWarnings",
            "NoArgsConstructor", "AllArgsConstructor", "RequiredArgsConstructor",
            "EqualsAndHashCode", "ToString", "Accessors");

    private static List<String> notes(FileDocEntry entry) {
        if (entry == null) {
            return List.of();
        }
        String typeName = fileName(entry.getRelativePath());
        int dot = typeName.lastIndexOf('.');
        if (dot > 0) {
            typeName = typeName.substring(0, dot);
        }
        List<String> notes = new ArrayList<>();
        int methods = 0;
        int annotations = 0;
        for (ExtractedFact fact : entry.getFacts()) {
            if (fact.getKind() == FactKind.ANNOTATED && !fact.getObject().isEmpty()
                    && !SKIP_ANNOTATIONS.contains(fact.getObject()) && annotations < 5) {
                notes.add("annotation: " + fact.getObject());
                annotations++;
            } else if (fact.getKind() == FactKind.DECLARES_METHOD && methods < 4
                    && !fact.getObject().isEmpty() && !fact.getObject().equals(typeName)) {
                notes.add("method: " + fact.getObject());
                methods++;
            } else if (fact.getKind() == FactKind.HAS_JAVADOC && !fact.getObject().isEmpty()
                    && notes.stream().noneMatch(n -> n.startsWith("doc:"))) {
                String text = fact.getObject().replace('\n', ' ').trim();
                if (text.length() > 160) {
                    text = text.substring(0, 160) + "...";
                }
                notes.add("doc: " + text);
            }
        }
        return notes;
    }

    private static String fileName(String relativePath) {
        String normalized = relativePath.replace('\\', '/');
        int slash = normalized.lastIndexOf('/');
        return slash < 0 ? normalized : normalized.substring(slash + 1);
    }

    private static String capitalize(String note) {
        if (note.isEmpty()) {
            return note;
        }
        return Character.toUpperCase(note.charAt(0)) + note.substring(1);
    }

    private static final class Model {
        final List<Trace> traces;
        final List<Trace> central;
        final List<FileDocEntry> auxiliary;

        Model(List<Trace> traces, List<Trace> central, List<FileDocEntry> auxiliary) {
            this.traces = traces;
            this.central = central;
            this.auxiliary = auxiliary;
        }
    }

    private static final class Trace {
        final String module;
        final FileDocEntry entry;
        final List<Step> steps;

        Trace(String module, FileDocEntry entry, List<Step> steps) {
            this.module = module;
            this.entry = entry;
            this.steps = steps;
        }
    }

    private static final class Step {
        final int depth;
        final String module;
        final String relativePath;
        final String oneLiner;
        final List<String> notes;
        final boolean client;

        Step(int depth, String module, String relativePath, String oneLiner, List<String> notes, boolean client) {
            this.depth = depth;
            this.module = module;
            this.relativePath = relativePath;
            this.oneLiner = oneLiner;
            this.notes = notes;
            this.client = client;
        }
    }
}
