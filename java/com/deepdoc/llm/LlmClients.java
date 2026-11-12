package com.deepdoc.llm;

import com.deepdoc.config.LlmSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Factory for the configured LLM backend. */
public final class LlmClients {
    private static final Logger logger = LoggerFactory.getLogger(LlmClients.class);

    private LlmClients() {
    }

    public static LlmClient create(LlmSection config, boolean forceDisable) {
        if (forceDisable || config == null || !config.isEnabled()) {
            logger.info("LLM disabled (--no-llm or llm.enabled=false)");
            return new UnavailableLlmClient("LLM disabled");
        }
        String provider = config.getProvider() == null ? "openai" : config.getProvider().toLowerCase();
        // openai, groq, NVIDIA NIM, and other OpenAI-compatible gateways share the same client.
        if ("openai".equals(provider) || "openai-compatible".equals(provider)
                || "groq".equals(provider) || "xai".equals(provider) || "grok".equals(provider)
                || "nvidia".equals(provider) || "nim".equals(provider)) {
            OpenAiLlmClient client = new OpenAiLlmClient(config);
            if (!client.isAvailable()) {
                logger.warn("LLM enabled but no API key found; documentation will be factual-only. "
                        + "Set OPENAI_API_KEY or llm.apiKey.");
                return new UnavailableLlmClient("No API key");
            }
            logger.info("LLM client ready (provider={}, model={}, baseUrl={})",
                    provider, config.getModel(), config.getBaseUrl());
            return client;
        }
        logger.warn("Unknown llm.provider '{}'; documentation will be factual-only", provider);
        return new UnavailableLlmClient("Unknown provider: " + provider);
    }

    static final class UnavailableLlmClient implements LlmClient {
        private final String reason;

        UnavailableLlmClient(String reason) {
            this.reason = reason;
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public String complete(String systemPrompt, String userPrompt) throws LlmException {
            throw new LlmException(reason);
        }
    }
}
