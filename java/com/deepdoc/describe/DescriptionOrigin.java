package com.deepdoc.describe;

/**
 * Where a one-line file description came from. Prefer quoted comments; fall back to path/name
 * heuristics; use a generic label only when nothing better is available.
 */
public enum DescriptionOrigin {
    /** First sentence of a leading doc / module comment. */
    COMMENT,
    /** Built from file name, path conventions, or well-known config roles. */
    HEURISTIC,
    /** Generic language + file-name label when no comment or role matched. */
    FALLBACK
}
