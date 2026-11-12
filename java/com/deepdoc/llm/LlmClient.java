package com.deepdoc.llm;

/**
 * Thin chat completion interface. DeepDoc supplies verified evidence; the model returns prose.
 */
public interface LlmClient {
    /** True when a real model backend is configured and reachable enough to attempt calls. */
    boolean isAvailable();

    /**
     * Ask the model to turn an evidence pack into Markdown documentation.
     *
     * @param systemPrompt role / rules
     * @param userPrompt   evidence pack body
     * @return model Markdown (may be empty on failure)
     */
    String complete(String systemPrompt, String userPrompt) throws LlmException;

    /**
     * Same as {@link #complete(String, String)} with an explicit output budget.
     * Callers that only implement the two-argument form ignore {@code maxTokens}.
     */
    default String complete(String systemPrompt, String userPrompt, int maxTokens) throws LlmException {
        return complete(systemPrompt, userPrompt);
    }
}
