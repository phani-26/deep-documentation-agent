package com.deepdoc.docgen;

import com.deepdoc.describe.DescriptionResult;
import com.deepdoc.describe.FileDescription;
import com.deepdoc.graph.DependencyGraph;
import com.deepdoc.model.FileInfo;
import com.deepdoc.model.RepositoryManifest;
import com.deepdoc.ranker.DocLevel;
import com.deepdoc.ranker.FileRank;
import com.deepdoc.ranker.RankingResult;
import com.deepdoc.understand.ExtractedFact;
import com.deepdoc.understand.UnderstandingResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Merges ranking levels, one-line descriptions, and Java understanding facts into one plan
 * covering every discovered file.
 */
public class DocumentationPlanner {

    public DocumentationPlan plan(RepositoryManifest manifest,
                                  DependencyGraph graph,
                                  RankingResult ranking,
                                  DescriptionResult descriptions,
                                  UnderstandingResult understanding) {
        List<FileDocEntry> entries = new ArrayList<>();
        for (FileInfo file : manifest.getFiles()) {
            FileDescription description = descriptions.of(file);
            if (description == null) {
                continue;
            }
            FileRank rank = ranking.rankOf(file);
            DocLevel level = rank == null ? DocLevel.LISTED : rank.getLevel();
            String packageName = rank == null ? "" : rank.getPackageRank().getName();
            String relative = graph.relativePath(file);
            List<ExtractedFact> facts = List.of();
            if (understanding != null && (level == DocLevel.DEEP || level == DocLevel.SUMMARY)) {
                facts = understanding.getKnowledge().factsOf(file);
            }
            entries.add(new FileDocEntry(file, relative, level, packageName, description, facts));
        }
        entries.sort(Comparator
                .comparing((FileDocEntry e) -> e.getLevel().ordinal())
                .thenComparing(FileDocEntry::getRelativePath));
        return new DocumentationPlan(entries);
    }
}
