package com.deepdoc.understand;

/** Describes whether a file actually received source understanding during this run. */
public enum FileAnalysisStatus {
    ANALYZED,
    FAILED,
    SKIPPED_BY_BUDGET,
    NOT_REQUESTED,
    NOT_ANALYZED
}
