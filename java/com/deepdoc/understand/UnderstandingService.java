package com.deepdoc.understand;

import com.deepdoc.config.AppConfig;
import com.deepdoc.config.UnderstandSection;
import com.deepdoc.model.FileInfo;
import com.deepdoc.ranker.DocLevel;
import com.deepdoc.ranker.FileRank;
import com.deepdoc.ranker.RankingResult;
import com.deepdoc.utils.SourceFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Outlines every ranked file with {@link LexicalOutliner}.
 * DEEP and SUMMARY are outlined. LISTED stays a name unless {@code understand.includeListed}
 * is on. The language does not change which patterns run.
 */
public class UnderstandingService {
    private static final Logger logger = LoggerFactory.getLogger(UnderstandingService.class);

    private final UnderstandSection config;
    private final FileUnderstander outliner;

    public UnderstandingService(AppConfig config) {
        this(config.getUnderstand(), new LexicalOutliner());
    }

    UnderstandingService(UnderstandSection config, FileUnderstander outliner) {
        this.config = config;
        this.outliner = outliner;
    }

    public UnderstandingResult understand(RankingResult ranking) {
        List<FileOutline> outlines = new ArrayList<>();
        int outlined = 0;
        int skippedListed = 0;
        int failed = 0;
        for (FileRank rank : ranking.getFiles()) {
            if (rank.getLevel() == DocLevel.LISTED && !config.isIncludeListed()) {
                skippedListed++;
                continue;
            }
            if (outlines.size() >= config.getMaxFiles()) {
                break;
            }
            FileOutline outline = parse(rank);
            if (!outline.isParsed()) {
                failed++;
            } else {
                outlined++;
            }
            outlines.add(outline);
        }
        KnowledgeGraph knowledge = KnowledgeGraph.from(outlines);
        int deep = 0;
        int summary = 0;
        for (FileOutline outline : outlines) {
            if (outline.getLevel() == DocLevel.DEEP) {
                deep++;
            } else if (outline.getLevel() == DocLevel.SUMMARY) {
                summary++;
            }
        }
        logger.info("Understood {} files ({} DEEP, {} SUMMARY, {} LISTED skipped, {} failed); {} facts, {} use edges",
                outlines.size(), deep, summary, skippedListed, failed,
                knowledge.getFacts().size(), knowledge.useEdgeCount());
        return new UnderstandingResult(knowledge, outlined, 0, skippedListed, failed);
    }

    private FileOutline parse(FileRank rank) {
        FileInfo file = rank.getFile();
        String source = SourceFiles.read(file.getFilePath());
        if (source == null) {
            return new FileOutline(file, rank.getLevel(), LexicalOutliner.ENGINE, false, "",
                    new ArrayList<>(), new ArrayList<>());
        }
        return outliner.outline(file, rank.getLevel(), source);
    }
}
