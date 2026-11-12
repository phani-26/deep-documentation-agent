package com.deepdoc.docgen;

import com.deepdoc.config.LlmSection;
import com.deepdoc.graph.DependencyGraph;
import com.deepdoc.llm.LlmClient;
import com.deepdoc.llm.LlmException;
import com.deepdoc.ranker.DocLevel;
import com.deepdoc.understand.UnderstandingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Generates repository architecture documentation under docs/architecture.
 *
 * The LLM is used to synthesize repository evidence into explanations. Static dependency
 * information is supporting evidence only; it must not be presented as proof of runtime
 * execution order or as a business workflow.
 */
public class DocumentationGenerator {
    private static final Logger logger = LoggerFactory.getLogger(DocumentationGenerator.class);

    private final LlmClient llm;
    private final LlmSection llmConfig;
    private final DependencyGraph graph;
    private final boolean generateDiagrams;

    public DocumentationGenerator(LlmClient llm, LlmSection llmConfig) {
        this(llm, llmConfig, null, false);
    }

    public DocumentationGenerator(LlmClient llm, LlmSection llmConfig,
                                  DependencyGraph graph, boolean generateDiagrams) {
        this.llm = llm;
        this.llmConfig = llmConfig;
        this.graph = graph;
        this.generateDiagrams = generateDiagrams && graph != null;
    }

    public Path generate(Path docsRoot, String architecture, DocumentationPlan plan) throws IOException {
        return generate(docsRoot, architecture, plan, null);
    }

    /**
     * @param docsRoot output directory that will contain architecture/
     * @param context repository-level evidence, when available
     */
    public Path generate(Path docsRoot, String architecture, DocumentationPlan plan,
                         com.deepdoc.model.RepositoryContext context) throws IOException {
        return generate(docsRoot, architecture, plan, context, null);
    }

    public Path generate(Path docsRoot, String architecture, DocumentationPlan plan,
                         com.deepdoc.model.RepositoryContext context,
                         UnderstandingResult understanding) throws IOException {
        return generate(docsRoot, architecture, plan, context, understanding, null);
    }

    /**
     * Generate docs using both the lexical outline and the language-specific structured
     * code-understanding graph (ANTLR-backed for JavaScript/TypeScript in this release).
     */
    public Path generate(Path docsRoot, String architecture, DocumentationPlan plan,
                         com.deepdoc.model.RepositoryContext context,
                         UnderstandingResult understanding,
                         com.deepdoc.model.KnowledgeGraph codeKnowledge) throws IOException {
        Path archDir = docsRoot.resolve("architecture");
        Path subsystems = archDir.resolve("subsystems");
        Files.createDirectories(subsystems);

        writeCoverage(archDir.resolve("coverage.md"), plan);
        writeLevels(archDir.resolve("levels.md"), plan);
        clearDirectory(subsystems);

        List<EvidencePack> packs = new ArrayList<>(EvidencePack.forPackages(architecture, context, plan,
                understanding == null ? null : understanding.getKnowledge(), codeKnowledge));
        packs.sort(Comparator
                .comparingInt((EvidencePack p) -> countLevel(p, DocLevel.DEEP)).reversed()
                .thenComparing(Comparator.comparingInt((EvidencePack p) -> countLevel(p, DocLevel.SUMMARY)).reversed())
                .thenComparing(EvidencePack::getSubsystem));

        int llmLimit = Math.max(1, llmConfig.getMaxSubsystems());
        List<String> written = new ArrayList<>();
        int llmCalls = 0;

        for (int i = 0; i < packs.size(); i++) {
            EvidencePack pack = packs.get(i);
            boolean useLlm = llm.isAvailable() && i < llmLimit;
            String markdown = renderSubsystem(pack, useLlm) + subsystemDiagram(pack);
            if (useLlm) {
                llmCalls++;
            }
            String fileName = sanitize(pack.getSubsystem()) + ".md";
            Files.writeString(subsystems.resolve(fileName), markdown, StandardCharsets.UTF_8);
            written.add(fileName);
        }

        boolean diagrams = false;
        if (generateDiagrams) {
            Files.writeString(archDir.resolve("diagrams.md"), MermaidDiagrams.architecturePage(graph),
                    StandardCharsets.UTF_8);
            diagrams = true;
        }

        EvidencePack overview = EvidencePack.overview(architecture, context, plan,
                understanding == null ? null : understanding.getKnowledge(), codeKnowledge);
        String how = renderHowItWorks(architecture, plan) + limitsNote();
        Files.writeString(archDir.resolve("how-it-works.md"), how, StandardCharsets.UTF_8);

        String what = renderWhatItDoes(architecture, plan, overview) + whatItDoesNext();
        Files.writeString(archDir.resolve("what-it-does.md"), what, StandardCharsets.UTF_8);

        String readme = renderOverview(architecture, plan, overview, written)
                + guideLink() + whatItDoesLink() + howItWorksLink() + diagramLink(diagrams);
        Files.writeString(archDir.resolve("README.md"), readme, StandardCharsets.UTF_8);

        Files.writeString(archDir.resolve("map.md"), renderMap(architecture, plan, written)
                        + guideLink() + whatItDoesLink() + howItWorksLink() + diagramLink(diagrams),
                StandardCharsets.UTF_8);

        String repoName = "repository";
        if (graph != null && graph.getRepositoryRoot() != null && graph.getRepositoryRoot().getFileName() != null) {
            repoName = graph.getRepositoryRoot().getFileName().toString();
        }
        RepositoryGuide.write(archDir, repoName, architecture, plan, graph, context, written);


        System.out.println("VERIFICATION DEBUG: starting");
        System.out.println("VERIFICATION DEBUG: verify called"); Report report = Verification.verify(plan, context, graph, understanding);
        String repoName = "repository";
        if (graph != null && graph.getRepositoryRoot() != null && graph.getRepositoryRoot().getFileName() != null) {
            repoName = graph.getRepositoryRoot().getFileName().toString();
        }
        Files.writeString(archDir.resolve("verification.md"),
                Verification.toMarkdown(report, repoName), StandardCharsets.UTF_8);
        Files.writeString(archDir.resolve("verification.json"),
                Verification.toJson(report, repoName), StandardCharsets.UTF_8);
        logger.info("Wrote documentation under {} ({} subsystem pages, {} LLM calls)",
                archDir, written.size(), llm.isAvailable() ? llmCalls + 3 : 0);
        return archDir;
    }

    private static int countLevel(EvidencePack pack, DocLevel level) {
        int count = 0;
        for (FileDocEntry file : pack.getFiles()) {
            if (file.getLevel() == level) {
                count++;
            }
        }
        return count;
    }

    private static void clearDirectory(Path dir) throws IOException {
        if (!Files.isDirectory(dir)) {
            return;
        }
        try (Stream<Path> children = Files.list(dir)) {
            for (Path child : children.toArray(Path[]::new)) {
                if (Files.isRegularFile(child)) {
                    Files.delete(child);
                }
            }
        }
    }

    private String renderSubsystem(EvidencePack pack, boolean useLlm) {
        if (useLlm) {
            try {
                String user = subsystemInstructions(pack.getSubsystem()) + "\n\nREPOSITORY EVIDENCE:\n"
                        + pack.toPromptBody();
                return llm.complete(SystemPrompt.text(), user, outputBudget(3000));
            } catch (LlmException e) {
                logger.warn("LLM failed for subsystem {}: {} — writing factual page",
                        pack.getSubsystem(), e.getMessage());
            }
        }
        return factualSubsystem(pack);
    }

    /**
     * This page should answer the new developer's first question: what product/system is this,
     * what can it do, and how do its major parts cooperate? Do not turn a dependency list into
     * a user journey.
     */
    private String renderWhatItDoes(String architecture, DocumentationPlan plan, EvidencePack overview) {
        if (llm.isAvailable()) {
            try {
                String user = "Create docs/architecture/what-it-does.md for a developer who has never seen this repository.\n"
                        + "Explain the repository as a working product, not as a list of files.\n"
                        + "Answer, in this order:\n"
                        + "1. What problem/domain does the software appear to address? State only what evidence supports.\n"
                        + "2. Who or what are the main actors/clients, if evidenced?\n"
                        + "3. What are the major user-visible or system capabilities? Group them by business capability.\n"
                        + "4. What are the major services/modules and each one's responsibility?\n"
                        + "5. Give one or more end-to-end workflows only when the evidence shows the actual links.\n"
                        + "6. What important behavior or business intent remains unknown?\n\n"
                        + "WRITING RULES:\n"
                        + "- Start with a clear 2-4 sentence explanation of what the repository does.\n"
                        + "- Prefer meaningful business capability names over package names.\n"
                        + "- Explain each service's responsibility and its boundary; do not merely repeat its class name.\n"
                        + "- Distinguish confirmed facts from reasonable conclusions using VERIFIED and INFERRED labels.\n"
                        + "- Put unsupported business intent under UNKNOWN; never guess from names alone.\n"
                        + "- Do not claim a service communicates with another service unless an API client, route, event, or other concrete evidence supports it.\n"
                        + "- Imports and static references show code dependencies, not necessarily runtime call order.\n"
                        + "- Do not invent actors, screens, endpoints, database behavior, queues, deployment topology, or business rules.\n"
                        + "- Avoid generic statements such as 'the system is scalable, robust and secure' unless evidence proves them.\n"
                        + "- Do not include a long file inventory, generated statistics, or a step-by-step import chain.\n"
                        + "- Cite relevant repository paths inline. Keep the page concise but informative.\n\n"
                        + "DETECTED ARCHITECTURE: " + architecture + "\n\n"
                        + "REPOSITORY EVIDENCE:\n" + overview.toPromptBody();
                String prose = llm.complete(SystemPrompt.text(), user, outputBudget(3000));
                if (isUsefulExplanation(prose)) {
                    return prose;
                }
                logger.warn("what-it-does response was too generic or incomplete — using evidence-based fallback");
            } catch (LlmException e) {
                logger.warn("LLM failed for what-it-does: {} — writing factual page", e.getMessage());
            }
        }
        return factualOverview(architecture, plan, List.of());
    }

    /** A technical walkthrough; deliberately separate from the product summary. */
    private String renderHowItWorks(String architecture, DocumentationPlan plan) {
        if (llm.isAvailable()) {
            try {
                String user = "Write docs/architecture/how-it-works.md as a technical walkthrough of the repository.\n"
                        + "This is not the repository's product summary. Explain the evidence-backed execution paths.\n"
                        + "For each module, identify its entry point, the responsibilities of the important files, "
                        + "and the next statically evidenced dependency.\n"
                        + "Use one section per module and preserve every ENTRY path exactly.\n"
                        + "Do not present an import/reference edge as a runtime call unless the evidence establishes that.\n"
                        + "If a chain reaches an HTTP client, message publisher, gateway route, scheduler or framework hook, "
                        + "describe only the evidenced behavior and label any unproven remote/runtime effect Inferred.\n"
                        + "Do not combine unrelated modules into one end-to-end flow.\n"
                        + "Use numbered steps only for a chain supported by the supplied evidence.\n\n"
                        + HowItWorks.promptBody(architecture, plan, graph);
                String prose = llm.complete(SystemPrompt.text(), user, outputBudget(4000));
                if (!coversModules(prose, user)) {
                    logger.warn("how-it-works prose missed an entry path — writing factual walkthrough");
                    return HowItWorks.factual(architecture, plan, graph);
                }
                return prose;
            } catch (LlmException e) {
                logger.warn("LLM failed for how-it-works: {} — writing factual walkthrough", e.getMessage());
            }
        }
        return HowItWorks.factual(architecture, plan, graph);
    }

    private String renderOverview(String architecture, DocumentationPlan plan,
                                   EvidencePack overview, List<String> subsystemFiles) {
        if (llm.isAvailable()) {
            try {
                String user = "Write docs/architecture/README.md: the main entry point for a developer opening this repository.\n"
                        + "The README must give a useful mental model in under a minute. It is not a file-by-file walkthrough.\n\n"
                        + "REQUIRED CONTENT:\n"
                        + "1. A precise opening paragraph: what this repository builds/does, its domain, and its primary purpose.\n"
                        + "2. Main capabilities: 3-7 meaningful capabilities, phrased as things the system enables.\n"
                        + "3. Architecture at a glance: explain the frontend/client, backend services, infrastructure/support services, "
                        + "and persistence/integrations only when evidenced.\n"
                        + "4. Service responsibility table: service/module, responsibility, important evidence path.\n"
                        + "5. One or two important workflows, only if a complete evidence-backed chain exists. Otherwise say what is known and what is missing.\n"
                        + "6. Key technologies, based on detected evidence, not assumptions.\n"
                        + "7. Important unknowns or evidence gaps that a new developer should know.\n"
                        + "8. Links to the generated subsystem and detailed pages.\n\n"
                        + "QUALITY BAR:\n"
                        + "- The first paragraph must identify the actual product/domain, not say merely 'this is a microservices application'.\n"
                        + "- Explain why each major component exists only when repository evidence supports the reason.\n"
                        + "- Distinguish product capabilities from technical components.\n"
                        + "- Do not infer a business workflow from package dependencies or class names alone.\n"
                        + "- Do not invent a role, actor, feature, API, event, database, deployment arrangement or design decision.\n"
                        + "- Use VERIFIED for directly supported facts, INFERRED for clearly reasoned conclusions, UNKNOWN for missing evidence.\n"
                        + "- Every important claim should cite one or more exact repository paths from the evidence.\n"
                        + "- Avoid generic filler, repeated statements, large file lists, raw evidence dumps and dependency-chain numbering.\n"
                        + "- Do not include token counts or documentation-level counts in the product explanation.\n"
                        + "- Use clear, concise language for a developer who is new to the codebase.\n"
                        + "- If the evidence is insufficient to identify the product's purpose, say so explicitly and explain what evidence is missing.\n\n"
                        + "DETECTED ARCHITECTURE: " + architecture + "\n"
                        + "DOCUMENTED FILE COUNTS (coverage metadata only): DEEP=" + plan.at(DocLevel.DEEP).size()
                        + ", SUMMARY=" + plan.at(DocLevel.SUMMARY).size()
                        + ", LISTED=" + plan.at(DocLevel.LISTED).size() + "\n"
                        + "SUBSYSTEM PAGES: " + String.join(", ", subsystemFiles) + "\n\n"
                        + "REPOSITORY OVERVIEW EVIDENCE:\n" + overview.toPromptBody() + "\n\n"
                        + "Use the evidence pack above to synthesize capabilities and responsibilities. "
                        + "Do not reconstruct a product summary from a sequential file-dependency trace.";

                String prose = llm.complete(SystemPrompt.text(), user, outputBudget(3500));
                if (isUsefulExplanation(prose)) {
                    return prose;
                }
                logger.warn("README response did not meet the explanation quality bar — using factual fallback");
            } catch (LlmException e) {
                logger.warn("LLM failed for overview: {} — writing factual README", e.getMessage());
            }
        }
        return factualOverview(architecture, plan, subsystemFiles);
    }

    /**
     * Lightweight response-quality guard. It cannot prove factual correctness, but prevents
     * an empty response or a response that is only headings/code fences from being accepted.
     */
    private static boolean isUsefulExplanation(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return false;
        }
        String normalized = markdown.replaceAll("(?s)```.*?```", " ")
                .replaceAll("(?m)^#{1,6}\\s*", " ")
                .trim();
        String[] words = normalized.split("\\s+");
        if (words.length < 100) {
            return false;
        }
        String lower = normalized.toLowerCase(Locale.ROOT);
        return lower.contains("verified") || lower.contains("inferred") || lower.contains("unknown")
                || lower.contains("evidence");
    }

    private int outputBudget(int preferred) {
        // Respect a smaller configured output budget; do not silently raise it to 4,000 tokens.
        int configured = llmConfig.getMaxTokens();
        return configured > 0 ? Math.min(configured, preferred) : preferred;
    }

    private static String subsystemInstructions(String subsystem) {
        return "Write only the subsystem documentation page for '" + subsystem + "'.\n"
                + "Explain the subsystem's purpose, responsibilities, boundaries, important components, "
                + "data it owns or handles, external interactions, and evidence-backed workflows.\n"
                + "Organize by behavior/capability, not by a sequential dump of files.\n"
                + "Start with a useful explanation of what this subsystem does.\n"
                + "Include an architecture diagram only if the supplied evidence establishes the components and edges.\n"
                + "A static dependency edge is not automatically a runtime call. Do not invent network calls, "
                + "business rules, data ownership, design motives or execution order.\n"
                + "Mark directly supported claims VERIFIED, reasoned conclusions INFERRED, and unsupported intent UNKNOWN.\n"
                + "Cite exact repository paths inline. Mention important files selectively; do not list every file.\n"
                + "Use concise, descriptive headings and avoid generic filler.\n";
    }

    private static boolean coversModules(String markdown, String prompt) {
        for (String line : prompt.split("\\R")) {
            if (!line.startsWith("ENTRY ")) {
                continue;
            }
            String path = line.substring("ENTRY ".length()).trim();
            if (!path.isEmpty() && !markdown.contains(path)) {
                return false;
            }
        }
        return true;
    }

    private String subsystemDiagram(EvidencePack pack) {
        if (!generateDiagrams) {
            return "";
        }
        return MermaidDiagrams.subsystemSection(graph, pack.getSubsystem());
    }

    private static String guideLink() {
        return "\n## Repository guide\n\n"
                + "The evidence-labeled guide, including knowns and open questions, is in [guide.md](guide.md).\n";
    }

    private static String whatItDoesLink() {
        return "\n## Product explanation\n\n"
                + "See [what-it-does.md](what-it-does.md) for a concise explanation of the repository's purpose and capabilities.\n";
    }

    private static String whatItDoesNext() {
        return "\n## Further reading\n\n"
                + "[how-it-works.md](how-it-works.md) traces the technical paths supported by static repository evidence.\n"
                + "[README.md](README.md) is the index of the generated architecture documentation.\n";
    }

    private static String howItWorksLink() {
        return "\n## Technical walkthrough\n\n"
                + "The entry-point walkthrough is in [how-it-works.md](how-it-works.md).\n";
    }

    private static String limitsNote() {
        return "\n## What this walkthrough does not prove\n\n"
                + "The walkthrough follows static dependencies and references. A dependency edge does not, by itself, "
                + "prove that one component calls another at runtime or establish the order of a business workflow. "
                + "Gateway routes, configuration-driven wiring, scheduled work, messaging and framework callbacks "
                + "may require separate evidence. Unconfirmed runtime effects must be marked Inferred or Unknown.\n";
    }

    private static String diagramLink(boolean diagrams) {
        if (!diagrams) {
            return "";
        }
        return "\n## Diagrams\n\n"
                + "Package dependencies and the source evidence behind each edge are in [diagrams.md](diagrams.md). "
                + "Arrows represent static dependencies, not guaranteed runtime call order.\n";
    }

    static String factualSubsystem(EvidencePack pack) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(pack.getSubsystem()).append("\n\n");
        sb.append("Architecture: ").append(extractArchitectureLine(pack)).append("\n\n");
        sb.append("_Generated without an LLM — this page contains extracted evidence, not a synthesized explanation._\n\n");
        sb.append("## Evidence\n\n");
        sb.append("```text\n").append(pack.toPromptBody()).append("\n```\n");
        return sb.toString();
    }

    private static String extractArchitectureLine(EvidencePack pack) {
        for (String line : pack.toPromptBody().split("\\R")) {
            if (line.startsWith("Detected architecture:")) {
                return line.substring("Detected architecture:".length()).trim();
            }
        }
        return "Unknown";
    }

    static String factualOverview(String architecture, DocumentationPlan plan, List<String> subsystemFiles) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Repository architecture overview\n\n");
        sb.append("Detected architecture: **").append(architecture).append("**\n\n");
        sb.append("The available documentation plan identifies files and their analysis depth, but it does not "
                + "provide enough synthesized evidence to explain the product's business purpose. Do not infer "
                + "that purpose from the architecture label alone.\n\n");
        sb.append("## Documentation coverage\n\n");
        sb.append("| Level | Files |\n|---|---:|\n");
        sb.append("| DEEP | ").append(plan.at(DocLevel.DEEP).size()).append(" |\n");
        sb.append("| SUMMARY | ").append(plan.at(DocLevel.SUMMARY).size()).append(" |\n");
        sb.append("| LISTED | ").append(plan.at(DocLevel.LISTED).size()).append(" |\n\n");
        sb.append("## Subsystem documentation\n\n");
        if (subsystemFiles.isEmpty()) {
            sb.append("No subsystem pages were generated.\n");
        } else {
            for (String file : subsystemFiles) {
                sb.append("- [").append(file.replace(".md", "")).append("](subsystems/")
                        .append(file).append(")\n");
            }
        }
        sb.append("\nSee [coverage.md](coverage.md) for file-level coverage and [levels.md](levels.md) for analysis depth.\n");
        return sb.toString();
    }

    private String renderMap(String architecture, DocumentationPlan plan, List<String> subsystemFiles) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Subsystem map\n\n");
        sb.append("Detected architecture: ").append(architecture).append("\n\n");
        sb.append("This map groups files by the package/subsystem labels assigned during analysis. "
                + "A grouping is not, by itself, proof of a runtime service boundary.\n\n");
        for (String name : plan.byPackage().keySet()) {
            long deep = plan.byPackage().get(name).stream().filter(e -> e.getLevel() == DocLevel.DEEP).count();
            long summary = plan.byPackage().get(name).stream().filter(e -> e.getLevel() == DocLevel.SUMMARY).count();
            long listed = plan.byPackage().get(name).stream().filter(e -> e.getLevel() == DocLevel.LISTED).count();
            sb.append("- `").append(name).append("` — DEEP ").append(deep)
                    .append(", SUMMARY ").append(summary)
                    .append(", LISTED ").append(listed).append('\n');
        }
        sb.append("\nGenerated pages: ")
                .append(subsystemFiles.isEmpty() ? "(none)" : String.join(", ", subsystemFiles))
                .append('\n');
        return sb.toString();
    }

    private void writeCoverage(Path path, DocumentationPlan plan) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("# Coverage\n\n");
        sb.append("Every discovered file with its documentation level and one-line description.\n\n");
        sb.append("| Level | Analysis status | File | Description |\n|---|---|---|---|\n");
        for (FileDocEntry entry : plan.getEntries()) {
            sb.append("| ").append(entry.getLevel())
                    .append(" | ").append(entry.getAnalysisStatus())
                    .append(" | `").append(entry.getRelativePath()).append("` | ")
                    .append(escapeCell(entry.oneLiner())).append(" |\n");
        }
        Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
    }

    private void writeLevels(Path path, DocumentationPlan plan) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("# Documentation levels\n\n");
        for (DocLevel level : DocLevel.values()) {
            sb.append("## ").append(level).append(" (").append(plan.at(level).size()).append(")\n\n");
            for (FileDocEntry entry : plan.at(level)) {
                sb.append("- `").append(entry.getRelativePath()).append("` — ")
                        .append(entry.oneLiner()).append('\n');
                if (level != DocLevel.LISTED && !entry.getFacts().isEmpty()) {
                    int limit = level == DocLevel.DEEP ? 8 : 4;
                    int shown = 0;
                    for (var fact : entry.getFacts()) {
                        if (shown++ >= limit) {
                            break;
                        }
                        sb.append("  - ").append(fact).append('\n');
                    }
                }
            }
            sb.append('\n');
        }
        Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
    }

    private static String sanitize(String name) {
        String value = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]+", "-");
        if (value.isBlank()) {
            return "subsystem";
        }
        return value.length() > 80 ? value.substring(0, 80) : value;
    }

    private static String escapeCell(String text) {
        return text.replace("|", "\\|").replace("\n", " ");
    }
}

        System.out.println("VERIFICATION DEBUG: starting");
        System.out.println("VERIFICATION DEBUG: verify called"); Report report = Verification.verify(plan, context, graph, understanding);
        String repoName = "repository";
        if (graph != null && graph.getRepositoryRoot() != null && graph.getRepositoryRoot().getFileName() != null) {
            repoName = graph.getRepositoryRoot().getFileName().toString();
        }
        Files.writeString(archDir.resolve("verification.md"),
                Verification.toMarkdown(report, repoName), StandardCharsets.UTF_8);
        Files.writeString(archDir.resolve("verification.json"),
                Verification.toJson(report, repoName), StandardCharsets.UTF_8);
