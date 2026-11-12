package com.deepdoc.understand;

import com.deepdoc.model.FileInfo;
import com.deepdoc.ranker.DocLevel;

/** Turns source text into a {@link FileOutline}. The shipped implementation is {@link LexicalOutliner}. */
public interface FileUnderstander {
    FileOutline outline(FileInfo file, DocLevel level, String source);
}
