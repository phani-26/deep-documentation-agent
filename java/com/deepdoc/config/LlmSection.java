package com.deepdoc.config;

public class LlmSection {
    private boolean enabled = true;
    private String provider = "openai";
    private String model = "gpt-4o-mini";
    private int maxTokens = 2000;
    private double temperature = 0.3;
    /** Optional; otherwise OPENAI_API_KEY / DEEPDOC_LLM_API_KEY env vars are used. */
    private String apiKey = "";
    /** OpenAI-compatible base URL, e.g. https://api.openai.com/v1 */
    private String baseUrl = "https://api.openai.com/v1";
    /** Max subsystem packs sent to the model in one analyze run. */
    private int maxSubsystems = 8;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getMaxSubsystems() {
        return maxSubsystems;
    }

    public void setMaxSubsystems(int maxSubsystems) {
        this.maxSubsystems = maxSubsystems;
    }
}
