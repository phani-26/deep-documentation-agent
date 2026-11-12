package com.deepdoc.cli;

import com.deepdoc.analyzer.ArchitectureDetector;
import com.deepdoc.analyzer.RepositoryAnalyzer;
import com.deepdoc.config.AppConfig;
import com.deepdoc.config.ConfigLoader;
import com.deepdoc.config.GitSection;
import com.deepdoc.describe.DescriptionOrigin;
import com.deepdoc.describe.DescriptionResult;
import com.deepdoc.describe.FileDescription;
import com.deepdoc.describe.FileDescriptionService;
import com.deepdoc.docgen.DocumentationGenerator;
import com.deepdoc.docgen.DocumentationPlan;
import com.deepdoc.docgen.DocumentationPlanner;
import com.deepdoc.docgen.FileDocEntry;
import com.deepdoc.graph.DependencyGraph;
import com.deepdoc.llm.LlmClient;
import com.deepdoc.llm.LlmClients;
import com.deepdoc.graph.DependencyGraphBuilder;
import com.deepdoc.graph.PackageEdge;
import com.deepdoc.graph.PackageNode;
import com.deepdoc.model.DetectedModule;
import com.deepdoc.model.DetectedPattern;
import com.deepdoc.model.FileInfo;
import com.deepdoc.model.RepositoryContext;
import com.deepdoc.model.RepositoryManifest;
import com.deepdoc.model.SourceReference;
import com.deepdoc.ranker.ActivityScores;
import com.deepdoc.ranker.DocLevel;
import com.deepdoc.ranker.EntryPointDetector;
import com.deepdoc.ranker.FileRank;
import com.deepdoc.ranker.ImportanceRanker;
import com.deepdoc.ranker.PackageRank;
import com.deepdoc.ranker.RankingResult;
import com.deepdoc.repository.CommitRecord;
import com.deepdoc.repository.GitInterface;
import com.deepdoc.understand.ExtractedFact;
import com.deepdoc.understand.FileOutline;
import com.deepdoc.understand.UnderstandingService;
import com.deepdoc.understand.UnderstandingResult;
import org.jgrapht.Graph;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Command(
    name = "deep-doc-agent",
    mixinStandardHelpOptions = true,
    version = "Deep Documentation Agent 1.0.0",
    description = "Generates high-quality, explanatory documentation for software repositories"
)
public class DeepDocCLI implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(DeepDocCLI.class);

    @Command(name = "analyze", description = "Analyze a repository and generate documentation")
    public Integer analyze(
            @Parameters(index = "0", description = "Path to the repository to analyze")
            String repositoryPath,
            
            @Option(names = {"-o", "--output"}, description = "Output directory for documentation (default: <repo>/docs)")
            String outputPath,
            
            @Option(names = {"-v", "--verbose"}, description = "Enable verbose logging")
            boolean verbose,
            
            @Option(names = {"--no-llm"}, description = "Skip LLM inference (structure-only documentation)")
            boolean noLlm,

            @Option(names = {"--top"}, defaultValue = "20", description = "Number of ranked files to print (default: ${DEFAULT-VALUE})")
            int top
    ) {
        try {
            AppConfig config = ConfigLoader.load();
            logger.info("{} v{}", config.getApp().getName(), config.getApp().getVersion());
            logger.info("=================================");
            logger.info("Loaded settings from src/main/resources/application.yml");
            logger.info("");

            Path repoPath = Paths.get(repositoryPath).toAbsolutePath().normalize();
            logger.info("Analyzing repository: {}", repoPath);
            logger.info("");
            
            RepositoryAnalyzer analyzer = new RepositoryAnalyzer(config);
            RepositoryManifest manifest = analyzer.analyze(repoPath);
            
            printManifestSummary(manifest);

            // One-line role for every discovered file (not only ranked ones)
            DescriptionResult descriptions = new FileDescriptionService().describeAll(manifest);
            printFileDescriptions(manifest, descriptions, top);

            GitHistory history = loadHistory(config.getGit(), manifest);
            DependencyGraph graph = new DependencyGraphBuilder(config.getGraph())
                    .build(manifest, history.commits, history.workTree);
            printDependencyGraph(graph);

            ActivityScores activity = history.commits.isEmpty() ? ActivityScores.none()
                    : ActivityScores.fromCommits(graph, history.commits, history.workTree,
                            config.getGit().getHalfLifeDays(), Instant.now().getEpochSecond());
            Set<FileInfo> entryPoints = EntryPointDetector.detect(graph);
            RankingResult ranking = new ImportanceRanker(config.getRanking()).rank(graph, activity, entryPoints);
            printRanking(graph, ranking, top);

            UnderstandingResult understanding = new UnderstandingService(config).understand(ranking);
            printUnderstanding(graph, understanding, top);

            // Merge levels + one-liners + the same facts for every language, then generate docs
            DocumentationPlan docPlan = new DocumentationPlanner()
                    .plan(manifest, graph, ranking, descriptions, understanding);
            printDocumentationPlan(docPlan, top);

            Path docsRoot = resolveDocsRoot(repoPath, outputPath, config.getOutput().getDefaultDirectory());
            LlmClient llm = LlmClients.create(config.getLlm(), noLlm);
            Path archDir = new DocumentationGenerator(llm, config.getLlm(), graph,
                    config.getOutput().isGenerateDiagrams())
                    .generate(docsRoot, manifest.getContext().getDetectedArchitecture(), docPlan,
                            manifest.getContext());
            logger.info("Documentation written to {}", archDir);
            
            logger.info("");
            logger.info("✓ Repository analysis completed successfully");
            logger.info("");
            logger.info("Docs: guide.md (repository guide), what-it-does, how-it-works, coverage, levels, subsystems/, diagrams.md");
            if (!llm.isAvailable()) {
                logger.info("LLM: skipped — factual Markdown only (set OPENAI_API_KEY or omit --no-llm)");
            }
            
            return 0;
        } catch (IOException e) {
            logger.error("Failed to analyze repository: {}", e.getMessage());
            if (verbose) {
                logger.error("Stack trace:", e);
            }
            return 1;
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            if (verbose) {
                logger.error("Stack trace:", e);
            }
            return 1;
        }
    }

    private static Path resolveDocsRoot(Path repoPath, String outputPath, String defaultDirectory) {
        if (outputPath != null && !outputPath.isBlank()) {
            return Paths.get(outputPath).toAbsolutePath().normalize();
        }
        String dir = defaultDirectory == null || defaultDirectory.isBlank() ? "docs" : defaultDirectory;
        Path configured = Paths.get(dir);
        return configured.isAbsolute() ? configured.normalize() : repoPath.resolve(configured).normalize();
    }

    private void printDocumentationPlan(DocumentationPlan plan, int top) {
        logger.info("");
        logger.info("Documentation plan (DEEP / SUMMARY / LISTED + one-liners)");
        logger.info("-------------------------------------------------------");
        logger.info("DEEP: {}  SUMMARY: {}  LISTED: {}  total: {}",
                plan.at(com.deepdoc.ranker.DocLevel.DEEP).size(),
                plan.at(com.deepdoc.ranker.DocLevel.SUMMARY).size(),
                plan.at(com.deepdoc.ranker.DocLevel.LISTED).size(),
                plan.size());
        int shown = 0;
        for (FileDocEntry e : plan.getEntries()) {
            if (e.getLevel() == com.deepdoc.ranker.DocLevel.LISTED) {
                continue;
            }
            if (shown++ >= top) {
                break;
            }
            logger.info("  [{}] {} — {} ({} facts)",
                    e.getLevel(), e.getRelativePath(), e.oneLiner(), e.getFacts().size());
        }
        long listed = plan.at(com.deepdoc.ranker.DocLevel.LISTED).size();
        if (listed > 0) {
            logger.info("  … {} LISTED files covered by one-liners in coverage.md", listed);
        }
    }

    /**
     * Every manifest file has a description. {@code top} only limits how many lines we print.
     */
    private void printFileDescriptions(RepositoryManifest manifest, DescriptionResult descriptions, int top) {
        logger.info("");
        logger.info("File descriptions (all {} files)", descriptions.size());
        logger.info("-------------------------------");
        Map<DescriptionOrigin, Long> counts = descriptions.countsByOrigin();
        logger.info("Origins: comment={}, heuristic={}, fallback={}",
                counts.getOrDefault(DescriptionOrigin.COMMENT, 0L),
                counts.getOrDefault(DescriptionOrigin.HEURISTIC, 0L),
                counts.getOrDefault(DescriptionOrigin.FALLBACK, 0L));
        logger.info("");

        int limit = Math.min(Math.max(top, 0), descriptions.size());
        List<FileDescription> list = descriptions.getDescriptions();
        for (int i = 0; i < limit; i++) {
            FileDescription d = list.get(i);
            Path relative = manifest.getRepositoryPath().relativize(d.getFile().getFilePath());
            logger.info("  {} — {} [{}]", relative, d.getText(), d.getOrigin().name().toLowerCase());
        }
        if (descriptions.size() > limit) {
            logger.info("  … {} more (use --top N to show more)", descriptions.size() - limit);
        }
    }

    private void printManifestSummary(RepositoryManifest manifest) {
        logger.info("Repository Analysis Summary");
        logger.info("---------------------------");
        logger.info("Total Files: {}", manifest.getTotalFiles());
        logger.info("Total Lines: {}", manifest.getTotalLines());
        logger.info("");
        
        logger.info("Language Distribution:");
        Map<String, Integer> languageStats = manifest.getLanguageStats();
        languageStats.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> 
                    logger.info("  {} - {} lines ({} files)", 
                        entry.getKey(), 
                        entry.getValue(),
                        manifest.getFiles().stream()
                            .filter(f -> f.getLanguage().equals(entry.getKey()))
                            .count())
                );
        logger.info("");
        
        RepositoryContext context = manifest.getContext();
        logger.info("Architecture: {} ({}, {})", context.getDetectedArchitecture(),
                ArchitectureDetector.percent(context.getArchitectureConfidence()),
                context.getArchitectureStyle().displayName());
        if (!context.getModules().isEmpty()) {
            logger.info("Modules: {}", context.getModules().size());
            for (DetectedModule module : context.getModules()) {
                logger.info("  {} [{}{}]", module.getRelativePath() + "/", module.getBuildSystem(),
                        module.isDeployable() ? ", deployable" : "");
            }
        }
        if (!context.getFrameworks().isEmpty()) {
            List<String> named = new ArrayList<>();
            for (DetectedPattern pattern : context.getPatterns()) {
                if (pattern.isFramework()) {
                    named.add(pattern.getName() + " " + ArchitectureDetector.percent(pattern.getConfidence()));
                }
            }
            logger.info("Frameworks: {}", named.isEmpty() ? String.join(", ", context.getFrameworks())
                    : String.join(", ", named));
        }
        if (!context.getTechnologies().isEmpty()) {
            logger.info("Technologies:");
            context.getTechnologies().forEach((key, value) ->
                    logger.info("  {} - {}", key, value));
        }
        List<SourceReference> evidence = new ArrayList<>();
        for (DetectedPattern pattern : context.getPatterns()) {
            if (pattern.isFramework()) {
                evidence.addAll(pattern.getEvidence());
            }
        }
        if (!evidence.isEmpty()) {
            logger.info("Evidence:");
            int shown = 0;
            for (SourceReference ref : evidence) {
                if (shown++ == 5) break;
                Path relative = manifest.getRepositoryPath().relativize(ref.getFile());
                logger.info("  {}:{}{}", relative, ref.getStartLine(),
                        ref.getSnippet() == null ? "" : "  " + ref.getSnippet());
            }
        }
        logger.info("Git History: {}", manifest.getContext().hasGitHistory() ? "Available" : "Not available");
        logger.info("");
        
        logger.info("Top 10 Largest Files:");
        List<FileInfo> topFiles = manifest.getFiles().stream()
                .sorted(Comparator.comparingInt(FileInfo::getLinesOfCode).reversed())
                .limit(10)
                .collect(Collectors.toList());
        
        for (int i = 0; i < topFiles.size(); i++) {
            FileInfo file = topFiles.get(i);
            Path relativePath = manifest.getRepositoryPath().relativize(file.getFilePath());
            logger.info("  {}. {} - {} lines ({})", 
                i + 1, 
                relativePath, 
                file.getLinesOfCode(), 
                file.getLanguage());
        }
    }

    /** Coverage counters, then one line per package with its outgoing package edges (strongest first). */
    private void printDependencyGraph(DependencyGraph graph) {
        Graph<PackageNode, PackageEdge> packages = graph.getPackageGraph();
        logger.info("");
        logger.info("Dependency Graph");
        logger.info("----------------");
        logger.info("Files in graph: {} (tests excluded: {}, non-code files: {}, parsed by the generic fallback: {})",
                graph.getFileGraph().vertexSet().size(), graph.getExcludedTestFiles(), graph.getNonCodeFiles(),
                graph.getGenericFallbackFiles());
        logger.info("Languages in graph: {}", graph.getLanguageCounts().entrySet().stream()
                .map(e -> e.getKey() + " " + e.getValue())
                .collect(Collectors.joining(", ")));
        logger.info("File dependencies: {} ({} inferred from co-changes)", graph.getFileGraph().edgeSet().size(),
                graph.getCoChangeEdges());
        logger.info("Packages: {} at depth {}{}", packages.vertexSet().size(), graph.getGroupingDepth(),
                graph.getStrippedPrefixes().isEmpty() ? ""
                        : " (common prefix " + String.join(", ", graph.getStrippedPrefixes()) + " stripped)");
        logger.info("Package dependencies: {}", packages.edgeSet().size());
        logger.info("");

        for (PackageNode node : graph.getPackages()) {
            String targets = packages.outgoingEdgesOf(node).stream()
                    .sorted(Comparator.comparingInt(PackageEdge::getImportCount).reversed())
                    .map(e -> packages.getEdgeTarget(e).getName() + "(" + e.getImportCount() + ")")
                    .collect(Collectors.joining(" "));
            logger.info("  {} [{} files, {} lines, {} internal]{}", node.getName(), node.getFiles().size(),
                    node.getTotalLines(), node.getInternalDependencies(), targets.isEmpty() ? "" : " -> " + targets);
        }
    }

    /** Commits read once and shared by the graph (co-change edges) and the ranking (activity). */
    private static final class GitHistory {
        final List<CommitRecord> commits;
        final Path workTree;

        GitHistory(List<CommitRecord> commits, Path workTree) {
            this.commits = commits;
            this.workTree = workTree;
        }
    }

    private GitHistory loadHistory(GitSection git, RepositoryManifest manifest) {
        GitHistory none = new GitHistory(Collections.emptyList(), manifest.getRepositoryPath());
        if (!git.isEnabled() || !manifest.getContext().hasGitHistory()) {
            return none;
        }
        try (GitInterface gitInterface = new GitInterface(manifest.getRepositoryPath())) {
            return new GitHistory(gitInterface.getCommitHistory(
                    git.getHistoryMonths(), git.getMaxCommits(), git.getBulkCommitFileLimit()),
                    gitInterface.getWorkTree());
        } catch (IOException e) {
            logger.warn("Git history unavailable, ranking on structure only: {}", e.getMessage());
            return none;
        }
    }

    /** Package table (score, structure and activity percentiles, Ca/Ce, role), then the top files. */
    private void printRanking(DependencyGraph graph, RankingResult ranking, int top) {
        logger.info("");
        logger.info("Importance Ranking ({})", ranking.usedActivity()
                ? "structure + Git activity" : "structure only, no usable Git history");
        logger.info("------------------");
        logger.info("Packages:");
        logger.info(String.format("  %-28s %6s %9s %9s %8s  %s", "package", "score", "structure", "activity", "Ca/Ce", "role"));
        for (PackageRank p : ranking.getPackages()) {
            logger.info(String.format("  %-28s %6.2f %9.2f %9.2f %8s  %s", p.getName(), p.getScore(),
                    p.getStructurePercentile(), p.getActivityPercentile(),
                    p.getAfferent() + "/" + p.getEfferent(), p.getRole()));
        }
        logger.info("");
        logger.info("Documentation levels: {} deep, {} summary, {} listed",
                ranking.filesAt(DocLevel.DEEP).size(), ranking.filesAt(DocLevel.SUMMARY).size(),
                ranking.filesAt(DocLevel.LISTED).size());
        logger.info("");
        logger.info("Top {} files:", Math.min(top, ranking.getFiles().size()));
        logger.info(String.format("  %-4s %-60s %8s %6s %-8s %s", "#", "file", "priority", "local", "level", "flags"));
        List<FileRank> files = ranking.getFiles();
        for (int i = 0; i < Math.min(top, files.size()); i++) {
            FileRank f = files.get(i);
            String flags = (f.isEntryPoint() ? "entry " : "") + (f.isBoundary() ? "boundary" : "");
            logger.info(String.format("  %-4d %-60s %8.3f %6.2f %-8s %s", i + 1, graph.relativePath(f.getFile()),
                    f.getPriority(), f.getLocalScore(), f.getLevel(), flags.trim()));
        }
    }

    /**
     * Outlines grouped the way Week 3 will write them: DEEP (full method facts) first,
     * then SUMMARY (paragraph facts). LISTED files are only shown when they were parsed.
     */
    private void printUnderstanding(DependencyGraph graph, UnderstandingResult understanding, int top) {
        logger.info("");
        logger.info("Understanding");
        logger.info("-------------");
        logger.info("DEEP: {} files (method facts); SUMMARY: {} files (paragraph facts); LISTED skipped: {}",
                understanding.deepOutlines().size(), understanding.summaryOutlines().size(),
                understanding.getSkippedListed());
        logger.info("Outlined {} files ({} failed)",
                understanding.getOutlines().size(), understanding.getFailedFiles());
        logger.info("Facts: {}; unique types: {}; cross-file uses: {}",
                understanding.getFactCount(), understanding.getKnowledge().uniqueTypes().size(),
                understanding.getKnowledge().useEdgeCount());
        printOutlineGroup(graph, understanding, "DEEP", understanding.deepOutlines(), top, true);
        printOutlineGroup(graph, understanding, "SUMMARY", understanding.summaryOutlines(), top, false);
        printOutlineGroup(graph, understanding, "LISTED", understanding.listedOutlines(), top, false);
    }

    private void printOutlineGroup(DependencyGraph graph, UnderstandingResult understanding,
                                   String heading, List<FileOutline> outlines, int top, boolean methods) {
        if (outlines.isEmpty()) {
            return;
        }
        logger.info("");
        logger.info("{} ({}):", heading, outlines.size());
        int shown = Math.min(top, outlines.size());
        for (int i = 0; i < shown; i++) {
            FileOutline outline = outlines.get(i);
            logger.info("  {}", graph.relativePath(outline.getFile()));
            if (!outline.typeNames().isEmpty()) {
                logger.info("      types: {}", String.join(", ", outline.typeNames()));
            }
            if (methods) {
                List<String> names = outline.methodNames();
                if (!names.isEmpty()) {
                    logger.info("      methods: {}", String.join(", ",
                            names.size() > 8 ? names.subList(0, 8) : names));
                }
            }
            List<ExtractedFact> facts = understanding.getKnowledge().factsOf(outline.getFile());
            if (!facts.isEmpty()) {
                int limit = Math.min(6, facts.size());
                List<String> labels = new ArrayList<>();
                for (int f = 0; f < limit; f++) {
                    labels.add(facts.get(f).toString());
                }
                logger.info("      facts: {}{}", String.join("; ", labels),
                        facts.size() > limit ? " …" : "");
            }
            List<String> used = new ArrayList<>();
            for (FileInfo target : understanding.getKnowledge().uses(outline.getFile())) {
                used.add(graph.relativePath(target));
            }
            if (!used.isEmpty()) {
                logger.info("      uses: {}", String.join(", ", used));
            }
        }
    }

    @Command(name = "version", description = "Show version information")
    public Integer version() {
        logger.info("Deep Documentation Agent v1.0.0");
        logger.info("Built for autonomous repository documentation generation");
        return 0;
    }

    @Override
    public Integer call() {
        CommandLine.usage(this, System.out);
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new DeepDocCLI()).execute(args);
        System.exit(exitCode);
    }
}
