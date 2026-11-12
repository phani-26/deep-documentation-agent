package com.deepdoc.model;

/**
 * Classification of a code fact by how it was obtained.
 * Matches design document confidence scoring (section 7.2.3 / 7.3).
 */
public enum FactType {
    /** Extracted directly from AST (imports, signatures, declarations). */
    STRUCTURAL,
    /** Inferred from language/framework patterns (e.g. React component naming). */
    INFERRED,
    /** LLM-produced or weakly supported speculation. */
    SPECULATIVE
}
