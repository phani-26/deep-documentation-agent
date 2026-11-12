package com.deepdoc.llm;

import com.deepdoc.config.LlmSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * OpenAI-compatible Chat Completions client ({@code /v1/chat/completions}).
 * Tries Java {@link HttpClient} first; on SSL/PKIX failures falls back to {@code curl}
 * (common when the JRE truststore is incomplete but system curl works).
 */
public class OpenAiLlmClient implements LlmClient {
    private static final Logger logger = LoggerFactory.getLogger(OpenAiLlmClient.class);

    private final LlmSection config;
    private final String apiKey;
    private final String baseUrl;
    private final HttpClient http;

    public OpenAiLlmClient(LlmSection config) {
        this.config = config;
        this.apiKey = firstNonBlank(
                config.getApiKey(),
                System.getenv("OPENAI_API_KEY"),
                System.getenv("DEEPDOC_LLM_API_KEY"),
                System.getenv("GROQ_API_KEY"));
        String url = firstNonBlank(config.getBaseUrl(), "https://api.openai.com/v1");
        this.baseUrl = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
    }

    @Override
    public boolean isAvailable() {
        return config.isEnabled() && apiKey != null && !apiKey.isBlank();
    }

    @Override
    public String complete(String systemPrompt, String userPrompt) throws LlmException {
        return complete(systemPrompt, userPrompt, config.getMaxTokens());
    }

    @Override
    public String complete(String systemPrompt, String userPrompt, int maxTokens) throws LlmException {
        if (!isAvailable()) {
            throw new LlmException("LLM is disabled or API key is missing "
                    + "(set OPENAI_API_KEY / GROQ_API_KEY or llm.apiKey)");
        }
        String body = buildRequestJson(systemPrompt, userPrompt, maxTokens);
        LlmException last = null;
        for (int attempt = 1; attempt <= 4; attempt++) {
            try {
                return completeOnce(body);
            } catch (LlmException e) {
                last = e;
                long waitMs = retryAfterMs(e);
                if (waitMs <= 0 || attempt == 4) {
                    throw e;
                }
                logger.warn("LLM rate-limited (attempt {}/4), sleeping {} ms", attempt, waitMs);
                try {
                    Thread.sleep(waitMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new LlmException("LLM retry interrupted", ie);
                }
            }
        }
        throw last != null ? last : new LlmException("LLM failed");
    }

    private String completeOnce(String body) throws LlmException {
        try {
            return parseContent(sendJava(body));
        } catch (LlmException primary) {
            if (!looksLikeSslProblem(primary)) {
                throw primary;
            }
            logger.warn("Java HTTPS failed ({}), retrying LLM call via curl", rootMessage(primary));
            return parseContent(sendCurl(body));
        }
    }

    /** @return positive millis to wait on HTTP 429, otherwise 0 */
    private static long retryAfterMs(LlmException e) {
        String msg = e.getMessage() == null ? "" : e.getMessage();
        if (!msg.contains("429") && !msg.toLowerCase().contains("rate_limit")) {
            return 0;
        }
        // Groq: "Please try again in 29.5125s"
        int idx = msg.indexOf("try again in ");
        if (idx >= 0) {
            int start = idx + "try again in ".length();
            int end = msg.indexOf('s', start);
            if (end > start) {
                try {
                    double seconds = Double.parseDouble(msg.substring(start, end).trim());
                    return Math.min(90_000L, Math.max(5_000L, (long) (seconds * 1000L) + 1500L));
                } catch (NumberFormatException ignored) {
                    // fall through
                }
            }
        }
        return 20_000L;
    }

    private String sendJava(String body) throws LlmException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/chat/completions"))
                .timeout(Duration.ofSeconds(120))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("User-Agent", "deep-documentation-agent/1.0")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new LlmException("LLM HTTP " + response.statusCode() + ": "
                        + truncate(response.body(), 400));
            }
            return response.body();
        } catch (IOException e) {
            throw new LlmException("LLM request failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LlmException("LLM request interrupted", e);
        }
    }

    private String sendCurl(String body) throws LlmException {
        Path temp = null;
        try {
            temp = Files.createTempFile("deepdoc-llm-", ".json");
            Files.writeString(temp, body, StandardCharsets.UTF_8);
            ProcessBuilder pb = new ProcessBuilder(
                    "curl", "-sS", "-w", "\n__HTTP_STATUS__:%{http_code}",
                    "--max-time", "120",
                    "-X", "POST", baseUrl + "/chat/completions",
                    "-H", "Authorization: Bearer " + apiKey,
                    "-H", "Content-Type: application/json",
                    "-H", "User-Agent: deep-documentation-agent/1.0",
                    "--data-binary", "@" + temp.toAbsolutePath()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            String response;
            try (InputStream in = process.getInputStream()) {
                response = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
            boolean finished = process.waitFor(130, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new LlmException("LLM curl timed out");
            }
            int status = 0;
            String payload = response;
            int marker = response.lastIndexOf("\n__HTTP_STATUS__:");
            if (marker >= 0) {
                payload = response.substring(0, marker);
                try {
                    status = Integer.parseInt(response.substring(marker + "\n__HTTP_STATUS__:".length()).trim());
                } catch (NumberFormatException ignored) {
                    status = process.exitValue() == 0 ? 200 : 500;
                }
            } else if (process.exitValue() != 0) {
                throw new LlmException("LLM curl exit " + process.exitValue() + ": " + truncate(response, 400));
            }
            if (status < 200 || status >= 300) {
                throw new LlmException("LLM HTTP " + status + ": " + truncate(payload, 400));
            }
            return payload;
        } catch (IOException e) {
            throw new LlmException("LLM curl failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LlmException("LLM curl interrupted", e);
        } finally {
            if (temp != null) {
                try {
                    Files.deleteIfExists(temp);
                } catch (IOException ignored) {
                    // best-effort cleanup
                }
            }
        }
    }

    private String parseContent(String responseBody) throws LlmException {
        String content = extractContent(responseBody);
        if (content == null || content.isBlank()) {
            throw new LlmException("LLM response contained no content");
        }
        return content.trim();
    }

    private static boolean looksLikeSslProblem(Throwable t) {
        String msg = rootMessage(t).toLowerCase();
        return msg.contains("pkix") || msg.contains("ssl") || msg.contains("certificate")
                || msg.contains("handshake") || msg.contains("unable to find valid certification");
    }

    private static String rootMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        return cur.getMessage() == null ? String.valueOf(cur) : cur.getMessage();
    }

    private String buildRequestJson(String systemPrompt, String userPrompt, int maxTokens) {
        int budget = maxTokens > 0 ? maxTokens : config.getMaxTokens();
        return "{"
                + "\"model\":" + jsonString(config.getModel()) + ","
                + "\"temperature\":" + config.getTemperature() + ","
                + "\"max_tokens\":" + budget + ","
                + "\"messages\":["
                + "{\"role\":\"system\",\"content\":" + jsonString(systemPrompt) + "},"
                + "{\"role\":\"user\",\"content\":" + jsonString(userPrompt) + "}"
                + "]}";
    }

    /**
     * Linear scan for {@code "content":"..."} — avoids catastrophic regex backtracking on large replies.
     */
    static String extractContent(String json) {
        if (json == null) {
            return null;
        }
        String last = null;
        int from = 0;
        while (true) {
            int key = json.indexOf("\"content\"", from);
            if (key < 0) {
                break;
            }
            int colon = json.indexOf(':', key + 9);
            if (colon < 0) {
                break;
            }
            int i = colon + 1;
            while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
                i++;
            }
            if (i >= json.length() || json.charAt(i) != '"') {
                from = key + 9;
                continue;
            }
            i++;
            StringBuilder value = new StringBuilder();
            while (i < json.length()) {
                char c = json.charAt(i++);
                if (c == '\\' && i < json.length()) {
                    char n = json.charAt(i++);
                    switch (n) {
                        case 'n': value.append('\n'); break;
                        case 'r': value.append('\r'); break;
                        case 't': value.append('\t'); break;
                        case '"': value.append('"'); break;
                        case '\\': value.append('\\'); break;
                        case '/': value.append('/'); break;
                        case 'u':
                            if (i + 3 < json.length()) {
                                value.append((char) Integer.parseInt(json.substring(i, i + 4), 16));
                                i += 4;
                            }
                            break;
                        default: value.append(n); break;
                    }
                } else if (c == '"') {
                    break;
                } else {
                    value.append(c);
                }
            }
            last = value.toString();
            from = i;
        }
        return last;
    }

    static String jsonString(String value) {
        if (value == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"': sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v.trim();
            }
        }
        return null;
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }
}
