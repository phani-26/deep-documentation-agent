package com.deepdoc.docgen;

import com.deepdoc.ranker.DocLevel;
import com.deepdoc.understand.ExtractedFact;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Text evidence pack for one subsystem/package — what we send to the LLM. */
public final class EvidencePack {
    private final String subsystem;
    private final String architecture;
    private final List<FileDocEntry> files;

    public EvidencePack(String subsystem, String architecture, List<FileDocEntry> files) {
        this.subsystem = subsystem;
        this.architecture = architecture == null ? "Unknown" : architecture;
        this.files = List.copyOf(files);
    }

    public String getSubsystem() {
        return subsystem;
    }

    public List<FileDocEntry> getFiles() {
        return files;
    }

    /** Human-readable pack used as the user message to the model. */
    public String toPromptBody() {
        StringBuilder sb = new StringBuilder();
        sb.append("Subsystem: ").append(subsystem).append('\n');
        sb.append("Detected architecture: ").append(architecture).append('\n');
        sb.append('\n');

        appendLevel(sb, DocLevel.DEEP, "DEEP (explain in detail; methods are available)");
        appendLevel(sb, DocLevel.SUMMARY, "SUMMARY (one short paragraph each)");
        appendLevel(sb, DocLevel.LISTED, "LISTED (name + one-liner only)");
        return sb.toString();
    }

    private void appendLevel(StringBuilder sb, DocLevel level, String heading) {
        List<FileDocEntry> subset = new ArrayList<>();
        for (FileDocEntry f : files) {
            if (f.getLevel() == level) {
                subset.add(f);
            }
        }
        if (subset.isEmpty()) {
            return;
        }
        sb.append(heading).append('\n');
        for (FileDocEntry f : subset) {
            sb.append("- ").append(f.getRelativePath())
                    .append(" — ").append(f.oneLiner()).append('\n');
            if (level != DocLevel.LISTED && !f.getFacts().isEmpty()) {
                int limit = level == DocLevel.DEEP ? 12 : 6;
                int n = 0;
                for (ExtractedFact fact : f.getFacts()) {
                    if (n++ >= limit) {
                        sb.append("    …\n");
                        break;
                    }
                    sb.append("    fact: ").append(fact).append('\n');
                }
            }
        }
        sb.append('\n');
    }

    public static EvidencePack overview(String architecture, DocumentationPlan plan) {
        List<FileDocEntry> highlights = new ArrayList<>();
        highlights.addAll(plan.at(DocLevel.DEEP));
        for (FileDocEntry e : plan.at(DocLevel.SUMMARY)) {
            if (highlights.size() >= 40) {
                break;
            }
            highlights.add(e);
        }
        return new EvidencePack("(repository overview)", architecture, highlights);
    }

    public static List<EvidencePack> forPackages(String architecture, DocumentationPlan plan) {
        List<EvidencePack> packs = new ArrayList<>();
        for (Map.Entry<String, List<FileDocEntry>> e : plan.byPackage().entrySet()) {
            boolean hasDepth = e.getValue().stream()
                    .anyMatch(f -> f.getLevel() == DocLevel.DEEP || f.getLevel() == DocLevel.SUMMARY);
            if (!hasDepth) {
                continue;
            }
            packs.add(new EvidencePack(e.getKey(), architecture, e.getValue()));
        }
        return packs;
    }
}
