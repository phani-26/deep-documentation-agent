package com.deepdoc.docgen;

import com.deepdoc.graph.DependencyEdge;
import com.deepdoc.graph.DependencyGraph;
import com.deepdoc.graph.PackageEdge;
import com.deepdoc.graph.PackageNode;
import com.deepdoc.model.FileInfo;
import com.deepdoc.model.SourceReference;
import org.jgrapht.Graph;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mermaid flowcharts drawn from the static dependency graph.
 * Arrows always mean "depends on". The language model never invents these edges.
 */
public final class MermaidDiagrams {
    /** Package diagram stays readable past this many nodes; the busiest packages are kept. */
    static final int MAX_PACKAGE_NODES = 40;
    /** File diagram inside one subsystem page. */
    static final int MAX_FILE_NODES = 24;

    private MermaidDiagrams() {
    }

    /** {@code docs/architecture/diagrams.md}: package flowchart plus the lines that justify each arrow. */
    public static String architecturePage(DependencyGraph graph) {
        Graph<PackageNode, PackageEdge> packages = graph.getPackageGraph();
        List<PackageNode> selected = selectPackages(packages);
        StringBuilder sb = new StringBuilder();
        sb.append("# Dependency diagrams\n\n");
        sb.append("Arrows mean **depends on**. They come from the static dependency graph ");
        sb.append("(imports, references, and co-change edges), not from the language model. ");
        sb.append("The number on an arrow is how many file-level dependencies cross that boundary.\n\n");
        sb.append("## Packages\n\n");
        sb.append(fence(packageFlow(graph, packages, selected)));
        int crossing = 0;
        for (PackageEdge edge : packages.edgeSet()) {
            if (selected.contains(packages.getEdgeSource(edge)) && selected.contains(packages.getEdgeTarget(edge))) {
                crossing++;
            }
        }
        if (crossing == 0) {
            sb.append("\nNo drawn package depends on another package. ");
            sb.append("Imports stay inside the package, which is what the graph shows when services do not share source. ");
            sb.append("The node label is the count of those internal file dependencies. ");
            sb.append("File-level arrows are on each subsystem page.\n\n");
        }
        if (selected.size() < packages.vertexSet().size()) {
            sb.append("\nShowing ").append(selected.size()).append(" of ")
                    .append(packages.vertexSet().size())
                    .append(" packages (the ones with the most crossing dependencies). Omitted:\n\n");
            for (PackageNode node : sorted(packages.vertexSet())) {
                if (!selected.contains(node)) {
                    sb.append("- `").append(node.getName()).append("`\n");
                }
            }
            sb.append('\n');
        }
        sb.append("## Source references\n\n");
        sb.append("Each row is one package edge. Evidence is the source line recorded when the ");
        sb.append("file edge was built (at most three lines per package edge).\n\n");
        sb.append(evidenceTable(graph, packages, selected));
        return sb.toString();
    }

    /**
     * Section appended to a subsystem page: files in that package, the packages they depend on,
     * and the evidence lines for those outgoing package edges.
     */
    public static String subsystemSection(DependencyGraph graph, String packageName) {
        PackageNode node = graph.getPackage(packageName);
        if (node == null) {
            return "";
        }
        Graph<FileInfo, DependencyEdge> files = graph.getFileGraph();
        List<FileInfo> members = new ArrayList<>();
        for (FileInfo file : node.getFiles()) {
            if (files.containsVertex(file)) {
                members.add(file);
            }
        }
        members.sort(Comparator.comparing(graph::relativePath));
        List<FileInfo> shown = selectFiles(files, members);

        StringBuilder sb = new StringBuilder();
        sb.append("\n## Dependencies\n\n");
        sb.append("Arrows mean **depends on**. They are read from the dependency graph. ");
        sb.append("Files outside this package are collapsed to their package name.\n\n");
        if (shown.isEmpty()) {
            sb.append("_No files from this package are in the code graph._\n");
            return sb.toString();
        }
        sb.append(fence(subsystemFlow(graph, node, shown)));
        if (shown.size() < members.size()) {
            sb.append("\nShowing ").append(shown.size()).append(" of ")
                    .append(members.size()).append(" files in this package (highest degree).\n");
        }
        sb.append("\n### Evidence\n\n");
        sb.append(outgoingEvidence(graph, node));
        return sb.toString();
    }

    private static String packageFlow(DependencyGraph graph,
                                      Graph<PackageNode, PackageEdge> packages,
                                      List<PackageNode> selected) {
        StringBuilder sb = new StringBuilder();
        sb.append("flowchart LR\n");
        Map<PackageNode, String> ids = new LinkedHashMap<>();
        int i = 0;
        for (PackageNode node : selected) {
            String id = "p" + i++;
            ids.put(node, id);
            sb.append("  ").append(id).append("[\"").append(label(nodeLabel(node))).append("\"]\n");
        }
        List<PackageNode[]> edges = new ArrayList<>();
        for (PackageEdge edge : packages.edgeSet()) {
            PackageNode from = packages.getEdgeSource(edge);
            PackageNode to = packages.getEdgeTarget(edge);
            if (ids.containsKey(from) && ids.containsKey(to)) {
                edges.add(new PackageNode[] {from, to});
            }
        }
        edges.sort(Comparator
                .comparing((PackageNode[] e) -> e[0].getName())
                .thenComparing(e -> e[1].getName()));
        for (PackageNode[] pair : edges) {
            PackageEdge edge = packages.getEdge(pair[0], pair[1]);
            sb.append("  ").append(ids.get(pair[0]))
                    .append(" -->|").append(edge.getImportCount()).append("| ")
                    .append(ids.get(pair[1])).append('\n');
        }
        if (ids.isEmpty()) {
            sb.append("  empty[\"No packages\"]\n");
        }
        return sb.toString();
    }

    private static String subsystemFlow(DependencyGraph graph, PackageNode self, List<FileInfo> shown) {
        Graph<FileInfo, DependencyEdge> files = graph.getFileGraph();
        StringBuilder sb = new StringBuilder();
        sb.append("flowchart LR\n");
        Map<FileInfo, String> fileIds = new LinkedHashMap<>();
        int i = 0;
        for (FileInfo file : shown) {
            String id = "f" + i++;
            fileIds.put(file, id);
            sb.append("  ").append(id).append("[\"").append(label(file.getFilePath().getFileName().toString()))
                    .append("\"]\n");
        }

        Map<PackageNode, String> external = new LinkedHashMap<>();
        List<String> lines = new ArrayList<>();
        for (FileInfo file : shown) {
            Map<PackageNode, Integer> outside = new LinkedHashMap<>();
            for (DependencyEdge edge : files.outgoingEdgesOf(file)) {
                FileInfo target = files.getEdgeTarget(edge);
                if (fileIds.containsKey(target)) {
                    lines.add("  " + fileIds.get(file) + " -->|" + edge.getReferenceCount() + "| "
                            + fileIds.get(target));
                    continue;
                }
                PackageNode pkg = graph.packageOf(target);
                if (pkg == null || pkg.equals(self)) {
                    continue;
                }
                outside.merge(pkg, edge.getReferenceCount(), Integer::sum);
            }
            List<PackageNode> targets = new ArrayList<>(outside.keySet());
            targets.sort(Comparator.comparing(PackageNode::getName));
            for (PackageNode pkg : targets) {
                String id = external.computeIfAbsent(pkg, p -> "e" + external.size());
                lines.add("  " + fileIds.get(file) + " -->|" + outside.get(pkg) + "| " + id);
            }
        }
        List<PackageNode> extNodes = new ArrayList<>(external.keySet());
        extNodes.sort(Comparator.comparing(PackageNode::getName));
        for (PackageNode pkg : extNodes) {
            sb.append("  ").append(external.get(pkg)).append("[\"").append(label(pkg.getName())).append("\"]\n");
        }
        lines.sort(Comparator.naturalOrder());
        for (String line : lines) {
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    private static String evidenceTable(DependencyGraph graph,
                                        Graph<PackageNode, PackageEdge> packages,
                                        List<PackageNode> selected) {
        StringBuilder sb = new StringBuilder();
        sb.append("| From | To | File dependencies | Evidence |\n|---|---|---:|---|\n");
        List<PackageNode[]> edges = new ArrayList<>();
        for (PackageEdge edge : packages.edgeSet()) {
            PackageNode from = packages.getEdgeSource(edge);
            PackageNode to = packages.getEdgeTarget(edge);
            if (selected.contains(from) && selected.contains(to)) {
                edges.add(new PackageNode[] {from, to});
            }
        }
        edges.sort(Comparator
                .comparing((PackageNode[] e) -> e[0].getName())
                .thenComparing(e -> e[1].getName()));
        if (edges.isEmpty()) {
            sb.append("| — | — | 0 | _no crossing dependencies_ |\n");
            return sb.toString();
        }
        for (PackageNode[] pair : edges) {
            PackageEdge edge = packages.getEdge(pair[0], pair[1]);
            sb.append("| `").append(escapeCell(pair[0].getName())).append("` | `")
                    .append(escapeCell(pair[1].getName())).append("` | ")
                    .append(edge.getImportCount()).append(" | ")
                    .append(formatEvidence(graph, edge.getEvidence())).append(" |\n");
        }
        return sb.toString();
    }

    private static String outgoingEvidence(DependencyGraph graph, PackageNode node) {
        Graph<PackageNode, PackageEdge> packages = graph.getPackageGraph();
        if (!packages.containsVertex(node) || packages.outgoingEdgesOf(node).isEmpty()) {
            return "_This package has no dependencies on other packages._\n";
        }
        StringBuilder sb = new StringBuilder();
        List<PackageEdge> edges = new ArrayList<>(packages.outgoingEdgesOf(node));
        edges.sort(Comparator.comparing(e -> packages.getEdgeTarget(e).getName()));
        for (PackageEdge edge : edges) {
            PackageNode to = packages.getEdgeTarget(edge);
            sb.append("- `").append(node.getName()).append("` → `").append(to.getName())
                    .append("` (").append(edge.getImportCount()).append(" file dependencies): ")
                    .append(formatEvidence(graph, edge.getEvidence())).append('\n');
        }
        return sb.toString();
    }

    private static List<PackageNode> selectPackages(Graph<PackageNode, PackageEdge> packages) {
        List<PackageNode> all = sorted(packages.vertexSet());
        if (all.size() <= MAX_PACKAGE_NODES) {
            return all;
        }
        all.sort(Comparator
                .comparingInt((PackageNode n) -> packages.inDegreeOf(n) + packages.outDegreeOf(n))
                .reversed()
                .thenComparing(PackageNode::getName));
        List<PackageNode> kept = new ArrayList<>(all.subList(0, MAX_PACKAGE_NODES));
        kept.sort(Comparator.comparing(PackageNode::getName));
        return kept;
    }

    private static List<FileInfo> selectFiles(Graph<FileInfo, DependencyEdge> files, List<FileInfo> members) {
        if (members.size() <= MAX_FILE_NODES) {
            return members;
        }
        List<FileInfo> ranked = new ArrayList<>(members);
        ranked.sort(Comparator
                .comparingInt((FileInfo f) -> files.inDegreeOf(f) + files.outDegreeOf(f))
                .reversed()
                .thenComparing(FileInfo::getFilePath));
        List<FileInfo> kept = new ArrayList<>(ranked.subList(0, MAX_FILE_NODES));
        kept.sort(Comparator.comparing(FileInfo::getFilePath));
        return kept;
    }

    private static List<PackageNode> sorted(Iterable<PackageNode> nodes) {
        List<PackageNode> list = new ArrayList<>();
        nodes.forEach(list::add);
        list.sort(Comparator.comparing(PackageNode::getName));
        return list;
    }

    private static String formatEvidence(DependencyGraph graph, List<SourceReference> evidence) {
        if (evidence == null || evidence.isEmpty()) {
            return "_co-change or no line recorded_";
        }
        List<String> parts = new ArrayList<>();
        Path root = graph.getRepositoryRoot();
        for (SourceReference ref : evidence) {
            String path;
            try {
                path = root.relativize(ref.getFile()).toString().replace('\\', '/');
            } catch (IllegalArgumentException ex) {
                path = ref.getFile().toString().replace('\\', '/');
            }
            if (ref.getStartLine() > 0) {
                path = path + ":" + ref.getStartLine();
            }
            parts.add("`" + path + "`");
        }
        return String.join(", ", parts);
    }

    private static String fence(String body) {
        return "```mermaid\n" + body + "```\n";
    }

    private static String nodeLabel(PackageNode node) {
        return node.getName() + " · " + node.getInternalDependencies() + " internal";
    }

    static String label(String raw) {
        String s = raw == null ? "" : raw;
        s = s.replace('\\', '/').replace('"', '\'').replace('[', '(').replace(']', ')');
        s = s.replace('\n', ' ').replace('\r', ' ');
        if (s.length() > 70) {
            s = s.substring(0, 67) + "...";
        }
        if (s.isBlank()) {
            return "unnamed";
        }
        return s;
    }

    static String escapeCell(String text) {
        return text.replace("|", "\\|").replace("\n", " ");
    }
}
