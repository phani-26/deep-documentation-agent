package com.deepdoc.docgen;

import com.deepdoc.model.DetectedModule;
import com.deepdoc.model.DetectedPattern;
import com.deepdoc.model.RepositoryContext;
import com.deepdoc.model.SourceReference;
import com.deepdoc.ranker.DocLevel;
import com.deepdoc.understand.ExtractedFact;
import com.deepdoc.understand.FactKind;
import org.jgrapht.Graph;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Writes the repository guide in a fixed shape: what the system does, how one path runs,
 * and which claims are verified, inferred, or unknown. Every discovered file is recorded.
 */
public final class RepositoryGuide {
    private RepositoryGuide() {
    }

    public static void write(Path archDir, String repoName, String architecture, DocumentationPlan plan,
                             com.deepdoc.graph.DependencyGraph graph, RepositoryContext context,
                             List<String> subsystemPages) throws IOException {
        write(archDir, repoName, architecture, plan,
                HowItWorks.flows(plan, graph),
                graph == null ? null : graph.getRepositoryRoot(),
                context, subsystemPages);
    }

    /** Test entry: same guide, with an explicit file graph and no detector evidence. */
    static void write(Path archDir, String repoName, String architecture, DocumentationPlan plan,
                      Graph<com.deepdoc.model.FileInfo, com.deepdoc.graph.DependencyEdge> files,
                      RepositoryContext context, List<String> subsystemPages) throws IOException {
        write(archDir, repoName, architecture, plan, HowItWorks.flows(plan, files), null, context, subsystemPages);
    }

    private static void write(Path archDir, String repoName, String architecture, DocumentationPlan plan,
                             HowItWorks.Flows walked, Path root, RepositoryContext context,
                             List<String> subsystemPages) throws IOException {
        String name = repoName == null || repoName.isBlank() ? "repository" : repoName;
        String label = architecture == null || architecture.isBlank() ? "Unknown" : architecture;
        HowItWorks.Flow primary = primary(walked.flows);
        List<Claim> claims = new ArrayList<>();
        List<Row> coverage = coverageRows(plan);
        if (coverage.size() != plan.size()) {
            throw new IllegalStateException("Coverage is missing files: plan has " + plan.size()
                    + " files, coverage has " + coverage.size());
        }

        String why = whySection(primary, claims);
        String runtime = runtimeSection(plan, context, root, claims);
        String dependencies = dependencySection(context, root, walked, claims);
        String questions = questionsSection(context, claims);
        String data = dataSection(plan, context, root, claims);

        StringBuilder guide = new StringBuilder();
        guide.append("# ").append(name).append(" repository guide\n\n");
        guide.append("**Documentation status:** Generated from repository evidence  \n");
        guide.append("**Repository:** `").append(name).append("/`  \n");
        guide.append("**Purpose:** Explain what the system does, how its components work together, ");
        guide.append("and why its architecture is structured this way.\n\n");
        guide.append("Claims use **VERIFIED**, **INFERRED**, or **UNKNOWN**. ");
        guide.append("A **VERIFIED** claim was checked against a file in this repository. ");
        guide.append("An **INFERRED** claim is a reading of those files, not a statement the repository makes. ");
        guide.append("**UNKNOWN** means the scan did not find enough evidence to answer.\n\n");

        guide.append(whatSection(name, label, walked, primary, claims));
        guide.append(architectureSection(walked, primary, claims));
        guide.append(subsystemSection(walked, plan, subsystemPages));
        guide.append(flowSection(primary, walked.centralOnly, claims));
        guide.append(data);
        guide.append(dependencies);
        guide.append(why);
        guide.append(runtime);
        guide.append(testingSection(plan, claims));
        guide.append(securitySection(plan, claims));
        guide.append(questions);
        guide.append(coverageSection(coverage));
        guide.append(whereSection(walked, plan));
        guide.append(conventionsSection(primary));
        guide.append(relatedSection(subsystemPages));

        Files.createDirectories(archDir.resolve("decisions"));
        Files.writeString(archDir.resolve("guide.md"), guide.toString(), StandardCharsets.UTF_8);
        Files.writeString(archDir.resolve("runtime.md"), "# Runtime and deployment\n\n" + runtime, StandardCharsets.UTF_8);
        Files.writeString(archDir.resolve("dependencies.md"), "# External dependencies\n\n" + dependencies, StandardCharsets.UTF_8);
        Files.writeString(archDir.resolve("open-questions.md"), "# Open questions\n\n" + questions, StandardCharsets.UTF_8);
        Files.writeString(archDir.resolve("decisions").resolve("structure.md"),
                "# Architecture structure\n\n" + why, StandardCharsets.UTF_8);
        Files.writeString(archDir.resolve("coverage.json"), coverageJson(coverage), StandardCharsets.UTF_8);
        Files.writeString(archDir.resolve("claims.json"), claimsJson(claims), StandardCharsets.UTF_8);
    }

    private static String whatSection(String name, String label, HowItWorks.Flows walked,
                                      HowItWorks.Flow primary, List<Claim> claims) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 1. What this repository does\n\n");
        String intro = "The scan labels `" + name + "` as " + label + ".";
        sb.append("**VERIFIED:** ").append(intro).append("\n\n");
        claims.add(new Claim("VERIFIED", intro, List.of()));
        if (walked.flows.isEmpty()) {
            sb.append("No controller, application class, listener, or UI root was found, ");
            sb.append("and no ranked source file was available to describe the product behavior.\n\n");
        } else if (walked.centralOnly) {
            sb.append("No HTTP controller, application class, listener, or UI root was found. ");
            sb.append("The notes below describe the highest-ranked files instead of a request path.\n\n");
        } else {
            sb.append("The parts below are the entry points the scan found, with the description taken from each file.\n\n");
        }
        Map<String, List<HowItWorks.Flow>> byModule = group(walked.flows);
        if (!byModule.isEmpty()) {
            sb.append("**What the files are responsible for**\n\n");
            for (Map.Entry<String, List<HowItWorks.Flow>> module : byModule.entrySet()) {
                HowItWorks.Flow first = module.getValue().get(0);
                sb.append("- **").append(module.getKey()).append("** — ")
                        .append(first.entry.oneLiner()).append(" (`")
                        .append(first.entry.getRelativePath()).append("`)\n");
                claims.add(new Claim("VERIFIED", module.getKey() + ": " + first.entry.oneLiner(),
                        List.of(first.entry.getRelativePath())));
            }
            sb.append('\n');
        }
        if (primary != null) {
            sb.append("The longest path the scan could follow ").append(walked.centralOnly
                    ? "from a ranked file" : "from an entry point").append(" is:\n\n");
            int n = 1;
            sb.append(n++).append(". `").append(primary.entry.getRelativePath()).append("` — ")
                    .append(primary.entry.oneLiner()).append('\n');
            for (HowItWorks.Hop hop : primary.hops) {
                sb.append(n++).append(". `").append(hop.path).append('`');
                if (!hop.oneLiner.isEmpty()) {
                    sb.append(" — ").append(hop.oneLiner);
                }
                if (hop.client) {
                    sb.append(" **INFERRED:** this file calls outside the repository.");
                }
                sb.append('\n');
            }
            sb.append('\n');
        }
        sb.append("**What this repository does not establish**\n\n");
        sb.append("**UNKNOWN:** The scan does not establish who owns the product, how many users it serves, ");
        sb.append("its service-level objectives, or the original business reason for the technology choices.\n\n");
        claims.add(new Claim("UNKNOWN",
                "Business ownership, expected scale, service-level objectives, and the original technology rationale are not in the repository evidence.",
                List.of()));
        return sb.toString();
    }

    private static String architectureSection(HowItWorks.Flows walked, HowItWorks.Flow primary, List<Claim> claims) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 2. System architecture\n\n");
        boolean persistence = hasPersistence(walked);
        boolean outside = hasOutsideCall(walked);
        sb.append("```text\n");
        sb.append("                 User / Client\n");
        sb.append("                       |\n");
        sb.append("                       v\n");
        sb.append("              Entry points in this repo\n");
        sb.append("                       |\n");
        sb.append("                       v\n");
        sb.append("           Files those entries reference\n");
        if (persistence || outside) {
            sb.append("                  /            \\\n");
            sb.append("                 v              v\n");
            sb.append("        ").append(persistence ? "Persistence files" : "(no store found)");
            sb.append("     ").append(outside ? "External call" : "(no external call)").append('\n');
        }
        sb.append("```\n\n");
        sb.append("**Diagram explained**\n\n");
        sb.append("- Entry points: controllers, application classes, listeners, and UI roots the scan found.\n");
        sb.append("- Files those entries reference: static dependencies, one file importing or naming another.\n");
        if (persistence) {
            sb.append("- Persistence files: repositories, entities, or SQL files on a traced path.\n");
        }
        if (outside) {
            sb.append("- External call: a client file. **INFERRED:** the other system is not an import in this repository.\n");
            claims.add(new Claim("INFERRED",
                    "A client file calls a system that is not imported as source in this repository.", List.of()));
        }
        if (primary == null) {
            sb.append("\nNo in-repository chain was available to draw a tighter diagram.\n");
        }
        sb.append('\n');
        return sb.toString();
    }

    private static String subsystemSection(HowItWorks.Flows walked, DocumentationPlan plan,
                                           List<String> subsystemPages) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 3. Main subsystems\n\n");
        sb.append("| Subsystem | Responsibility | Important files |\n|---|---|---|\n");
        Map<String, List<HowItWorks.Flow>> byModule = group(walked.flows);
        if (byModule.isEmpty()) {
            sb.append("| — | No subsystem entry was found | — |\n");
        } else {
            for (Map.Entry<String, List<HowItWorks.Flow>> module : byModule.entrySet()) {
                HowItWorks.Flow first = module.getValue().get(0);
                sb.append("| ").append(escape(module.getKey())).append(" | ")
                        .append(escape(first.entry.oneLiner())).append(" | ")
                        .append(importantFiles(module.getValue())).append(" |\n");
            }
        }
        sb.append('\n');
        if (subsystemPages != null && !subsystemPages.isEmpty()) {
            sb.append("Each package with a DEEP or SUMMARY file has a page under `docs/architecture/subsystems/`:\n\n");
            for (String page : subsystemPages) {
                sb.append("- [").append(page).append("](subsystems/").append(page).append(")\n");
            }
            sb.append('\n');
        } else if (plan.at(DocLevel.DEEP).isEmpty() && plan.at(DocLevel.SUMMARY).isEmpty()) {
            sb.append("No package had a DEEP or SUMMARY file, so no subsystem page was written.\n\n");
        }
        return sb.toString();
    }

    private static String flowSection(HowItWorks.Flow primary, boolean centralOnly, List<Claim> claims) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 4. How the main path runs\n\n");
        if (primary == null) {
            sb.append("**UNKNOWN:** The scan did not find an entry file that references another file in this repository.\n\n");
            claims.add(new Claim("UNKNOWN", "No in-repository request path was found.", List.of()));
            return sb.toString();
        }
        if (centralOnly) {
            sb.append("This chain starts at a ranked file, not at an HTTP or UI entry point.\n\n");
        } else if (isHttp(primary.entry)) {
            sb.append("This chain starts at an HTTP entry file.\n\n");
        } else {
            sb.append("This chain starts at an entry file the scan detected.\n\n");
        }
        sb.append("```text\n");
        sb.append(fileName(primary.entry.getRelativePath())).append('\n');
        for (HowItWorks.Hop hop : primary.hops) {
            sb.append("  |\n  v\n");
            sb.append(fileName(hop.path));
            if (hop.client) {
                sb.append("  (client)");
            }
            sb.append('\n');
        }
        sb.append("```\n\n");
        sb.append("**VERIFIED:** `").append(primary.entry.getRelativePath()).append("` — ")
                .append(primary.entry.oneLiner()).append("\n\n");
        List<String> evidence = new ArrayList<>();
        evidence.add(primary.entry.getRelativePath());
        for (HowItWorks.Hop hop : primary.hops) {
            sb.append("**VERIFIED:** uses `").append(hop.path).append('`');
            if (!hop.oneLiner.isEmpty()) {
                sb.append(" — ").append(hop.oneLiner);
            }
            sb.append('\n');
            evidence.add(hop.path);
            if (hop.client) {
                sb.append("**INFERRED:** `").append(hop.path)
                        .append("` is a client. The static graph stops there; the remote system is not an import.\n");
            }
        }
        sb.append('\n');
        claims.add(new Claim("VERIFIED", "Main path starts at " + primary.entry.getRelativePath(), evidence));
        sb.append("**Evidence checked**\n\n");
        for (String path : evidence) {
            sb.append("- `").append(path).append("`\n");
        }
        sb.append("\n**UNKNOWN:** The URL, message name, or command that reaches this entry, and the HTTP status it returns, ");
        sb.append("are not in the extracted facts unless a file above states them.\n\n");
        claims.add(new Claim("UNKNOWN",
                "The public route or message name and the response status for the main path were not extracted.",
                evidence));
        return sb.toString();
    }

    private static String dataSection(DocumentationPlan plan, RepositoryContext context, Path root, List<Claim> claims) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 5. Data and persistence\n\n");
        List<DetectedPattern> stores = patterns(context, DetectedPattern.KIND_DATASTORE);
        if (stores.isEmpty()) {
            sb.append("**UNKNOWN:** The scan did not identify a primary data store from architecture signals.\n\n");
            claims.add(new Claim("UNKNOWN", "No primary data store was identified.", List.of()));
        } else {
            sb.append("| Store | Evidence |\n|---|---|\n");
            for (DetectedPattern store : stores) {
                List<String> cites = cites(store, root);
                sb.append("| ").append(escape(store.getName())).append(" | ");
                sb.append(cites.isEmpty() ? "—" : joinCodes(cites)).append(" |\n");
                boolean manifest = !cites.isEmpty() && cites.stream().allMatch(RepositoryGuide::manifestOnly);
                String label = manifest ? "INFERRED" : "VERIFIED";
                String statement = store.getName() + (manifest
                        ? " is declared in a build file. That declaration alone does not prove a runtime path."
                        : " appears in repository evidence.");
                claims.add(new Claim(label, statement, cites));
            }
            sb.append('\n');
            sb.append("A build-file declaration is **INFERRED** as usage. A configuration or source hit is **VERIFIED** as a mention, ");
            sb.append("not as proof that the store is the source of truth.\n\n");
        }
        List<FileDocEntry> entities = new ArrayList<>();
        for (FileDocEntry entry : plan.getEntries()) {
            String blob = (entry.getRelativePath() + " " + entry.oneLiner()).toLowerCase(Locale.ROOT);
            if (blob.contains("entity") || blob.contains("repository") || entry.getRelativePath().endsWith(".sql")) {
                entities.add(entry);
            }
        }
        if (entities.isEmpty()) {
            sb.append("No entity, repository, or SQL file was named in the file list.\n\n");
        } else {
            sb.append("| File | Role recorded by the scan |\n|---|---|\n");
            int shown = 0;
            for (FileDocEntry entry : entities) {
                if (shown++ >= 12) {
                    sb.append("| … | ").append(entities.size() - 12).append(" more persistence files are in coverage |\n");
                    break;
                }
                sb.append("| `").append(entry.getRelativePath()).append("` | ")
                        .append(escape(entry.oneLiner())).append(" |\n");
            }
            sb.append('\n');
        }
        sb.append("**Source-of-truth rule:** A cache, queue, or client is not treated as the authoritative store ");
        sb.append("unless a file in this repository says so. The scan did not find that statement.\n\n");
        claims.add(new Claim("UNKNOWN",
                "Which system is the source of truth for each piece of data is not stated.", List.of()));
        return sb.toString();
    }

    private static String dependencySection(RepositoryContext context, Path root,
                                            HowItWorks.Flows walked, List<Claim> claims) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 6. External dependencies\n\n");
        sb.append("| Dependency | How it showed up | Evidence |\n|---|---|---|\n");
        int rows = 0;
        if (context != null) {
            for (DetectedPattern pattern : context.getPatterns()) {
                if (DetectedPattern.KIND_DATASTORE.equals(pattern.getKind())
                        || DetectedPattern.KIND_FRAMEWORK.equals(pattern.getKind())
                        || DetectedPattern.KIND_BUILD.equals(pattern.getKind())) {
                    List<String> cites = cites(pattern, root);
                    boolean manifest = !cites.isEmpty() && cites.stream().allMatch(RepositoryGuide::manifestOnly);
                    sb.append("| ").append(escape(pattern.getName())).append(" | ")
                            .append(manifest ? "Build or package file" : pattern.getKind()).append(" | ")
                            .append(cites.isEmpty() ? "—" : joinCodes(cites)).append(" |\n");
                    rows++;
                }
            }
        }
        for (HowItWorks.Flow flow : walked.flows) {
            for (HowItWorks.Hop hop : flow.hops) {
                if (!hop.client) {
                    continue;
                }
                sb.append("| Client `").append(fileName(hop.path)).append("` | On an entry-point path | `")
                        .append(hop.path).append("` |\n");
                claims.add(new Claim("VERIFIED", hop.path + " is a client on a traced path.", List.of(hop.path)));
                rows++;
            }
        }
        if (rows == 0) {
            sb.append("| — | No framework, datastore, or client was detected | — |\n");
        }
        sb.append('\n');
        sb.append("A dependency named only in a package or build file is not treated as proof that the running system uses it. ");
        sb.append("Confirm it through configuration and a path from an entry point.\n\n");
        return sb.toString();
    }

    private static String whySection(HowItWorks.Flow primary, List<Claim> claims) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 7. Why the architecture is structured this way\n\n");
        sb.append("This section separates what the files do from why someone might have split them.\n\n");
        if (primary == null || primary.hops.isEmpty()) {
            sb.append("**UNKNOWN:** There is no entry-to-file chain to explain.\n\n");
            claims.add(new Claim("UNKNOWN", "No chain was available to explain the structure.", List.of()));
            return sb.toString();
        }
        HowItWorks.Hop first = primary.hops.get(0);
        sb.append("### Entry and the file it uses\n\n");
        sb.append("**VERIFIED**\n\n");
        sb.append('`').append(primary.entry.getRelativePath()).append("` references `").append(first.path).append("`.\n\n");
        sb.append("Evidence:\n\n");
        sb.append("- `").append(primary.entry.getRelativePath()).append("`\n");
        sb.append("- `").append(first.path).append("`\n\n");
        claims.add(new Claim("VERIFIED",
                primary.entry.getRelativePath() + " references " + first.path,
                List.of(primary.entry.getRelativePath(), first.path)));
        sb.append("**INFERRED**\n\n");
        sb.append("The entry file and the next file are separate, so the work of `")
                .append(fileName(first.path)).append("` is not written entirely inside the entry file.\n\n");
        sb.append("Basis:\n\n");
        sb.append("- The entry file's description is: ").append(primary.entry.oneLiner()).append('\n');
        sb.append("- The next file's description is: ").append(first.oneLiner.isEmpty() ? "(none)" : first.oneLiner).append('\n');
        sb.append("- The dependency is a static reference, not a comment about design intent.\n\n");
        claims.add(new Claim("INFERRED",
                "Separating " + fileName(primary.entry.getRelativePath()) + " from " + fileName(first.path)
                        + " keeps that work out of the entry file. The repository does not state this as the original motive.",
                List.of(primary.entry.getRelativePath(), first.path)));
        sb.append("**UNKNOWN**\n\n");
        sb.append("The repository does not record whether this split was chosen for testing, reuse, team boundaries, or another reason.\n\n");
        claims.add(new Claim("UNKNOWN",
                "The original reason for the entry/next-file split is not recorded.",
                List.of(primary.entry.getRelativePath(), first.path)));
        return sb.toString();
    }

    private static String runtimeSection(DocumentationPlan plan, RepositoryContext context, Path root, List<Claim> claims) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 8. Runtime and deployment\n\n");
        List<FileDocEntry> ops = new ArrayList<>();
        List<FileDocEntry> entries = new ArrayList<>();
        for (FileDocEntry entry : plan.getEntries()) {
            String path = entry.getRelativePath().replace('\\', '/');
            String lower = path.toLowerCase(Locale.ROOT);
            if (lower.endsWith("dockerfile") || lower.contains("docker-compose") || lower.endsWith("compose.yml")
                    || lower.endsWith("compose.yaml") || lower.contains(".github/workflows/")
                    || lower.contains("/k8s/") || lower.contains("/helm/") || lower.endsWith(".yaml") && lower.contains("deploy")) {
                ops.add(entry);
            }
            if (isApplication(entry)) {
                entries.add(entry);
            }
        }
        sb.append("```text\n");
        sb.append("Repository\n");
        sb.append("    |\n");
        if (entries.isEmpty() && ops.isEmpty()) {
            sb.append("    v\n(no application entry or deployment file found)\n");
        } else {
            sb.append("    +-- application entry files\n");
            for (FileDocEntry entry : entries) {
                sb.append("    |     ").append(entry.getRelativePath()).append('\n');
            }
            sb.append("    +-- deployment and workflow files\n");
            for (FileDocEntry entry : ops) {
                sb.append("          ").append(entry.getRelativePath()).append('\n');
            }
        }
        sb.append("```\n\n");
        if (context != null && !context.getModules().isEmpty()) {
            sb.append("| Module | Build | Deployable flag |\n|---|---|---|\n");
            for (DetectedModule module : context.getModules()) {
                sb.append("| `").append(module.getRelativePath()).append("` | ")
                        .append(escape(module.getBuildSystem())).append(" | ")
                        .append(module.isDeployable() ? "yes" : "no").append(" |\n");
            }
            sb.append('\n');
            sb.append("**VERIFIED:** A module is marked deployable only when the detector saw a main class, Dockerfile, or start script.\n\n");
            claims.add(new Claim("VERIFIED",
                    "Deployable modules are those with a main class, Dockerfile, or start script.", List.of()));
        }
        if (ops.isEmpty()) {
            sb.append("**UNKNOWN:** No Dockerfile, Compose file, or CI workflow was discovered, so the deployment topology is not established.\n\n");
        } else {
            sb.append("**VERIFIED:** Deployment or workflow files exist:\n\n");
            for (FileDocEntry entry : ops) {
                sb.append("- `").append(entry.getRelativePath()).append("`\n");
            }
            sb.append('\n');
        }
        sb.append("**UNKNOWN:** Separate source directories are not separate production services unless a deployment file says so. ");
        sb.append("Replica counts, health checks, and startup commands were not read from those files.\n\n");
        claims.add(new Claim("UNKNOWN",
                "Production process topology, replica counts, and startup commands were not established.", List.of()));
        return sb.toString();
    }

    private static String testingSection(DocumentationPlan plan, List<Claim> claims) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 9. Testing and quality checks\n\n");
        List<FileDocEntry> tests = new ArrayList<>();
        List<FileDocEntry> workflows = new ArrayList<>();
        for (FileDocEntry entry : plan.getEntries()) {
            String path = entry.getRelativePath().replace('\\', '/').toLowerCase(Locale.ROOT);
            if (path.contains("/test/") || path.contains("/tests/") || path.endsWith("test.java")
                    || path.endsWith(".spec.ts") || path.endsWith(".spec.js") || path.contains("load_test")) {
                tests.add(entry);
            }
            if (path.contains(".github/workflows/")) {
                workflows.add(entry);
            }
        }
        sb.append("| Category | Files the scan found |\n|---|---|\n");
        sb.append("| Tests and load scripts | ").append(tests.isEmpty() ? "None" : tests.size() + " files").append(" |\n");
        sb.append("| CI workflows | ").append(workflows.isEmpty() ? "None" : workflows.size() + " files").append(" |\n\n");
        int shown = 0;
        for (FileDocEntry entry : tests) {
            if (shown++ >= 8) {
                break;
            }
            sb.append("- `").append(entry.getRelativePath()).append("` — ").append(entry.oneLiner()).append('\n');
        }
        if (!tests.isEmpty()) {
            sb.append('\n');
            claims.add(new Claim("VERIFIED", tests.size() + " test or load-script files were discovered.",
                    List.of(tests.get(0).getRelativePath())));
        }
        if (workflows.isEmpty()) {
            sb.append("**UNKNOWN:** No CI workflow file was discovered, so the scan cannot show which tests run in automation.\n\n");
            claims.add(new Claim("UNKNOWN", "No CI workflow was discovered.", List.of()));
        } else {
            sb.append("**VERIFIED:** Workflow files exist. **UNKNOWN:** Their steps were not parsed, so a listed test file is not proof that CI runs it.\n\n");
            claims.add(new Claim("UNKNOWN", "Workflow steps were not parsed, so CI execution of tests is not established.",
                    List.of(workflows.get(0).getRelativePath())));
        }
        return sb.toString();
    }

    private static String securitySection(DocumentationPlan plan, List<Claim> claims) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 10. Security boundaries\n\n");
        sb.append("The scan records files that look related to a boundary. It does not claim the control is complete.\n\n");
        sb.append("| Area | What was found |\n|---|---|\n");
        sb.append("| Authentication and access | ").append(matching(plan, "auth", "security", "permission")).append(" |\n");
        sb.append("| Validation | ").append(matching(plan, "validator", "validation")).append(" |\n");
        sb.append("| Configuration and secrets files | ").append(matching(plan, ".env", "application.yml", "application.yaml", "secrets")).append(" |\n\n");
        sb.append("**UNKNOWN:** Whether tokens are validated, whether queries are parameterized, and whether logs omit secrets ");
        sb.append("was not decided from file names alone.\n\n");
        claims.add(new Claim("UNKNOWN",
                "Security control completeness was not established from file names.", List.of()));
        return sb.toString();
    }

    private static String questionsSection(RepositoryContext context, List<Claim> claims) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 11. Open questions\n\n");
        sb.append("| Question | Why it matters | Where an answer would live |\n|---|---|---|\n");
        sb.append("| Why is the system split this way? | Records the design motive | Design notes or an ADR in the repository |\n");
        sb.append("| What scale and availability are required? | Separates code structure from production needs | Operations docs, not source layout |\n");
        if (context != null && !patterns(context, DetectedPattern.KIND_DATASTORE).isEmpty()) {
            sb.append("| Why were the detected data stores chosen? | A mention is not a decision record | Design notes next to the configuration |\n");
        }
        sb.append("| Which tests run in CI? | A test file can exist without being automated | The workflow file's steps |\n\n");
        sb.append("An unanswered question is not a documentation failure. Inventing an owner or a motive would be.\n\n");
        claims.add(new Claim("UNKNOWN", "Design motives, scale, and CI test execution remain open questions.", List.of()));
        return sb.toString();
    }

    private static String coverageSection(List<Row> coverage) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 12. File coverage\n\n");
        sb.append("Every discovered file is listed. **Explained** files are DEEP or SUMMARY. ");
        sb.append("**Mentioned** files are recorded with a one-line role. **Skipped** files are lockfiles or vendored minified assets.\n\n");
        sb.append("| File | Treatment | Subsystem |\n|---|---|---|\n");
        for (Row row : coverage) {
            sb.append("| `").append(row.path).append("` | ").append(row.treatment)
                    .append(" | ").append(escape(row.subsystem)).append(" |\n");
        }
        sb.append("\n**Coverage rule:** This table has one row per discovered file (")
                .append(coverage.size()).append("). Generation fails if a file is missing.\n\n");
        sb.append("The same rows are in [coverage.json](coverage.json).\n\n");
        return sb.toString();
    }

    private static String whereSection(HowItWorks.Flows walked, DocumentationPlan plan) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 13. Where to start when changing the system\n\n");
        sb.append("| If you want to... | Start here |\n|---|---|\n");
        Map<String, List<HowItWorks.Flow>> byModule = group(walked.flows);
        if (byModule.isEmpty()) {
            sb.append("| Read the highest-ranked file | See section 4 |\n");
        } else {
            for (Map.Entry<String, List<HowItWorks.Flow>> module : byModule.entrySet()) {
                sb.append("| Change ").append(escape(module.getKey())).append(" | `")
                        .append(module.getValue().get(0).entry.getRelativePath()).append("` |\n");
            }
        }
        for (FileDocEntry entry : plan.getEntries()) {
            String lower = entry.getRelativePath().toLowerCase(Locale.ROOT);
            if (lower.endsWith("dockerfile") || lower.contains("docker-compose")) {
                sb.append("| Understand deployment files | `").append(entry.getRelativePath()).append("` |\n");
                break;
            }
        }
        for (FileDocEntry entry : plan.getEntries()) {
            if (entry.getRelativePath().replace('\\', '/').contains(".github/workflows/")) {
                sb.append("| Understand automated checks | `").append(entry.getRelativePath()).append("` |\n");
                break;
            }
        }
        sb.append("\nFollow the subsystem page and the files that entry references before changing it.\n\n");
        return sb.toString();
    }

    private static String conventionsSection(HowItWorks.Flow primary) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 14. Evidence and confidence conventions\n\n");
        sb.append("| Label | Meaning |\n|---|---|\n");
        sb.append("| **VERIFIED** | Checked against a file, path, or detector evidence |\n");
        sb.append("| **INFERRED** | A reading of verified facts. The repository does not state it |\n");
        sb.append("| **UNKNOWN** | The scan does not contain enough evidence to answer |\n\n");
        if (primary != null && !primary.hops.isEmpty()) {
            sb.append("> **VERIFIED:** `").append(primary.entry.getRelativePath()).append("` references `")
                    .append(primary.hops.get(0).path).append("`.\n>\n");
            sb.append("> Evidence: `").append(primary.entry.getRelativePath()).append("`, `")
                    .append(primary.hops.get(0).path).append("`.\n\n");
            sb.append("> **INFERRED:** Those two files are separate, so the second file's work is not written entirely in the entry file.\n\n");
            sb.append("> **UNKNOWN:** The original reason for that split is not recorded.\n\n");
        }
        sb.append("Machine-readable copies of the claims are in [claims.json](claims.json).\n\n");
        return sb.toString();
    }

    private static String relatedSection(List<String> subsystemPages) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 15. Related architecture documents\n\n");
        sb.append("- [map.md](map.md) — package counts.\n");
        sb.append("- [runtime.md](runtime.md) — entry points and deployment files.\n");
        sb.append("- [dependencies.md](dependencies.md) — detected external dependencies.\n");
        sb.append("- [subsystems/](subsystems/) — package pages");
        if (subsystemPages != null && !subsystemPages.isEmpty()) {
            sb.append(" (").append(subsystemPages.size()).append(")");
        }
        sb.append(".\n");
        sb.append("- [decisions/structure.md](decisions/structure.md) — the verified, inferred, and unknown structure notes.\n");
        sb.append("- [open-questions.md](open-questions.md) — questions the scan cannot answer.\n");
        sb.append("- [coverage.json](coverage.json) — one outcome for every discovered file.\n");
        sb.append("- [claims.json](claims.json) — claims and evidence.\n");
        sb.append("- [how-it-works.md](how-it-works.md) — the longer file-by-file walk.\n");
        sb.append("- [what-it-does.md](what-it-does.md) — a shorter overview.\n");
        sb.append("- [diagrams.md](diagrams.md) — package dependency diagram, when diagrams are enabled.\n\n");
        sb.append("Regenerate this guide by running the agent again. Do not fill **UNKNOWN** items without a file that states them.\n");
        return sb.toString();
    }

    private static List<Row> coverageRows(DocumentationPlan plan) {
        List<Row> rows = new ArrayList<>();
        for (FileDocEntry entry : plan.getEntries()) {
            rows.add(new Row(entry.getRelativePath(), treatment(entry), subsystemOf(entry)));
        }
        rows.sort(Comparator.comparing((Row row) -> row.treatment).thenComparing(row -> row.path));
        return rows;
    }

    static String treatment(FileDocEntry entry) {
        String path = entry.getRelativePath().replace('\\', '/').toLowerCase(Locale.ROOT);
        String name = fileName(path);
        if (name.equals("package-lock.json") || name.equals("yarn.lock") || name.equals("pnpm-lock.yaml")
                || name.endsWith(".min.js") || name.endsWith(".min.css") || path.contains("/vendor/")) {
            return "Skipped";
        }
        if (HowItWorks.isAuxiliary(entry.getRelativePath()) || path.contains("/test/") || path.contains("/tests/")
                || name.endsWith("test.java") || name.endsWith(".spec.ts") || name.endsWith(".spec.js")
                || name.endsWith("_test.py") || name.startsWith("test_")) {
            return "Mentioned";
        }
        if (entry.getLevel() == DocLevel.DEEP || entry.getLevel() == DocLevel.SUMMARY) {
            return "Explained";
        }
        return "Mentioned";
    }

    private static String subsystemOf(FileDocEntry entry) {
        if (entry.getPackageName() != null && !entry.getPackageName().isBlank()) {
            return entry.getPackageName();
        }
        return HowItWorks.moduleOf(entry.getRelativePath());
    }

    private static HowItWorks.Flow primary(List<HowItWorks.Flow> flows) {
        HowItWorks.Flow best = null;
        int bestScore = -1;
        for (HowItWorks.Flow flow : flows) {
            if (flow.hops.isEmpty()) {
                continue;
            }
            int score = flow.hops.size() + (isHttp(flow.entry) ? 10 : 0);
            if (score > bestScore) {
                best = flow;
                bestScore = score;
            }
        }
        return best;
    }

    private static boolean isHttp(FileDocEntry entry) {
        String name = fileName(entry.getRelativePath()).toLowerCase(Locale.ROOT);
        if (name.contains("controller.") || name.contains("route")) {
            return true;
        }
        for (ExtractedFact fact : entry.getFacts()) {
            if (fact.getKind() == FactKind.ANNOTATED
                    && ("RestController".equals(fact.getObject()) || "Controller".equals(fact.getObject())
                    || "GetMapping".equals(fact.getObject()) || "PostMapping".equals(fact.getObject()))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isApplication(FileDocEntry entry) {
        String name = fileName(entry.getRelativePath()).toLowerCase(Locale.ROOT);
        if (name.endsWith("application.java") || name.equals("main.tsx") || name.equals("main.py") || name.equals("main.go")) {
            return true;
        }
        for (ExtractedFact fact : entry.getFacts()) {
            if (fact.getKind() == FactKind.ANNOTATED && "SpringBootApplication".equals(fact.getObject())) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasPersistence(HowItWorks.Flows walked) {
        for (HowItWorks.Flow flow : walked.flows) {
            for (HowItWorks.Hop hop : flow.hops) {
                String blob = (hop.path + " " + hop.oneLiner).toLowerCase(Locale.ROOT);
                if (blob.contains("repository") || blob.contains("entity") || blob.endsWith(".sql")) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasOutsideCall(HowItWorks.Flows walked) {
        for (HowItWorks.Flow flow : walked.flows) {
            for (HowItWorks.Hop hop : flow.hops) {
                if (hop.client) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Map<String, List<HowItWorks.Flow>> group(List<HowItWorks.Flow> flows) {
        Map<String, List<HowItWorks.Flow>> grouped = new LinkedHashMap<>();
        for (HowItWorks.Flow flow : flows) {
            grouped.computeIfAbsent(flow.module, key -> new ArrayList<>()).add(flow);
        }
        return grouped;
    }

    private static String importantFiles(List<HowItWorks.Flow> flows) {
        List<String> paths = new ArrayList<>();
        for (HowItWorks.Flow flow : flows) {
            addPath(paths, flow.entry.getRelativePath());
            for (HowItWorks.Hop hop : flow.hops) {
                if (paths.size() >= 3) {
                    break;
                }
                addPath(paths, hop.path);
            }
            if (paths.size() >= 3) {
                break;
            }
        }
        if (paths.isEmpty()) {
            return "—";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paths.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append('`').append(paths.get(i)).append('`');
        }
        return sb.toString();
    }

    private static void addPath(List<String> paths, String path) {
        if (!paths.contains(path)) {
            paths.add(path);
        }
    }

    private static List<DetectedPattern> patterns(RepositoryContext context, String kind) {
        List<DetectedPattern> found = new ArrayList<>();
        if (context == null || context.getPatterns() == null) {
            return found;
        }
        for (DetectedPattern pattern : context.getPatterns()) {
            if (kind.equals(pattern.getKind())) {
                found.add(pattern);
            }
        }
        return found;
    }

    private static List<String> cites(DetectedPattern pattern, Path root) {
        List<String> cites = new ArrayList<>();
        for (SourceReference reference : pattern.getEvidence()) {
            String cite = cite(reference, root);
            if (!cite.isEmpty()) {
                cites.add(cite);
            }
        }
        return cites;
    }

    private static String cite(SourceReference reference, Path root) {
        if (reference == null || reference.getFile() == null) {
            return "";
        }
        Path file = reference.getFile();
        String relative;
        if (root != null && file.isAbsolute() && root.isAbsolute() && file.startsWith(root)) {
            relative = root.relativize(file).toString().replace('\\', '/');
        } else {
            relative = file.toString().replace('\\', '/');
        }
        if (reference.getStartLine() > 0) {
            relative += ":" + reference.getStartLine();
        }
        return relative;
    }

    private static boolean manifestOnly(String cite) {
        String path = cite;
        int colon = path.lastIndexOf(':');
        if (colon > 1) {
            path = path.substring(0, colon);
        }
        String name = fileName(path).toLowerCase(Locale.ROOT);
        return name.equals("pom.xml") || name.equals("package.json") || name.equals("build.gradle")
                || name.equals("build.gradle.kts") || name.equals("go.mod") || name.equals("cargo.toml")
                || name.equals("requirements.txt") || name.equals("pyproject.toml");
    }

    private static String matching(DocumentationPlan plan, String... needles) {
        List<String> hits = new ArrayList<>();
        for (FileDocEntry entry : plan.getEntries()) {
            String blob = entry.getRelativePath().toLowerCase(Locale.ROOT);
            for (String needle : needles) {
                if (blob.contains(needle)) {
                    hits.add(entry.getRelativePath());
                    break;
                }
            }
            if (hits.size() >= 3) {
                break;
            }
        }
        if (hits.isEmpty()) {
            return "No matching file name";
        }
        return joinCodes(hits);
    }

    private static String joinCodes(List<String> paths) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paths.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append('`').append(paths.get(i)).append('`');
        }
        return sb.toString();
    }

    private static String coverageJson(List<Row> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"files\": [\n");
        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);
            sb.append("    {\"path\": ").append(json(row.path))
                    .append(", \"treatment\": ").append(json(row.treatment))
                    .append(", \"subsystem\": ").append(json(row.subsystem)).append('}');
            if (i + 1 < rows.size()) {
                sb.append(',');
            }
            sb.append('\n');
        }
        sb.append("  ]\n}\n");
        return sb.toString();
    }

    private static String claimsJson(List<Claim> claims) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < claims.size(); i++) {
            Claim claim = claims.get(i);
            sb.append("  {\"label\": ").append(json(claim.label))
                    .append(", \"statement\": ").append(json(claim.statement))
                    .append(", \"evidence\": [");
            for (int e = 0; e < claim.evidence.size(); e++) {
                if (e > 0) {
                    sb.append(", ");
                }
                sb.append(json(claim.evidence.get(e)));
            }
            sb.append("]}");
            if (i + 1 < claims.size()) {
                sb.append(',');
            }
            sb.append('\n');
        }
        sb.append("]\n");
        return sb.toString();
    }

    private static String json(String value) {
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    sb.append(c);
            }
        }
        sb.append('"');
        return sb.toString();
    }

    private static String fileName(String relativePath) {
        String normalized = relativePath.replace('\\', '/');
        int slash = normalized.lastIndexOf('/');
        return slash < 0 ? normalized : normalized.substring(slash + 1);
    }

    private static String escape(String text) {
        return text == null ? "" : text.replace("|", "\\|").replace("\n", " ");
    }

    private static final class Row {
        final String path;
        final String treatment;
        final String subsystem;

        Row(String path, String treatment, String subsystem) {
            this.path = path;
            this.treatment = treatment;
            this.subsystem = subsystem;
        }
    }

    private static final class Claim {
        final String label;
        final String statement;
        final List<String> evidence;

        Claim(String label, String statement, List<String> evidence) {
            this.label = label;
            this.statement = statement;
            this.evidence = evidence;
        }
    }
}
