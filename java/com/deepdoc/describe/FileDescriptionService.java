package com.deepdoc.describe;

import com.deepdoc.model.FileInfo;
import com.deepdoc.model.RepositoryManifest;
import com.deepdoc.utils.SourceFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Assigns a one-line description to every file in the repository manifest — not only the
 * ranked DEEP/SUMMARY files. Prefer a leading doc comment; otherwise use path/name heuristics.
 */
public class FileDescriptionService {
    private static final Logger logger = LoggerFactory.getLogger(FileDescriptionService.class);

    /**
     * Describes each file in {@code manifest} exactly once. Order follows path for stable output.
     */
    public DescriptionResult describeAll(RepositoryManifest manifest) {
        Path root = manifest.getRepositoryPath();
        List<FileInfo> files = new ArrayList<>(manifest.getFiles());
        files.sort(Comparator.comparing(f -> root.relativize(f.getFilePath()).toString()));

        List<FileDescription> out = new ArrayList<>(files.size());
        for (FileInfo file : files) {
            out.add(describeOne(file, root.relativize(file.getFilePath())));
        }
        logger.info("Described {} files (comment/heuristic/fallback)", out.size());
        return new DescriptionResult(out);
    }

    FileDescription describeOne(FileInfo file, Path relative) {
        String source = SourceFiles.read(file.getFilePath());
        return DocCommentExtractor.extract(file.getFilePath(), file.getLanguage(), source)
                .map(hit -> new FileDescription(file, hit.text, DescriptionOrigin.COMMENT, 0.92, hit.evidence))
                .orElseGet(() -> NameHeuristicDescriber.describe(file, relative));
    }
}
