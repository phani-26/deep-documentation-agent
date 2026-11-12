package com.deepdoc.describe;

import com.deepdoc.graph.TestFileDetector;
import com.deepdoc.model.FileInfo;

import java.nio.file.Path;
import java.util.Locale;

/**
 * Builds a short role from path and file name when no documentation comment exists.
 * Deterministic and language-independent — no LLM.
 */
final class NameHeuristicDescriber {

    private NameHeuristicDescriber() {
    }

    static FileDescription describe(FileInfo file, Path relative) {
        String fileName = relative.getFileName() != null
                ? relative.getFileName().toString()
                : file.getFilePath().getFileName().toString();
        String base = stripExtension(fileName);
        String lowerName = fileName.toLowerCase(Locale.ROOT);
        String pathLower = relative.toString().replace('\\', '/').toLowerCase(Locale.ROOT);

        if (TestFileDetector.isTest(relative)) {
            String subject = humanize(base
                    .replaceAll("(?i)(Test|Tests|IT|TestCase|Spec)$", "")
                    .replaceAll("(?i)\\.(test|spec)$", "")
                    .replaceAll("(?i)^test_", "")
                    .replaceAll("(?i)_test$", ""));
            String text = subject.isEmpty()
                    ? "Test file"
                    : "Tests for " + subject;
            return heuristic(file, text, 0.72);
        }

        String special = specialFile(lowerName, pathLower, file.getLanguage());
        if (special != null) {
            return heuristic(file, special, 0.80);
        }

        String role = roleFromName(base);
        if (role != null) {
            return heuristic(file, role, 0.75);
        }

        String pathRole = roleFromPath(pathLower, base);
        if (pathRole != null) {
            return heuristic(file, pathRole, 0.70);
        }

        String language = file.getLanguage() == null || file.getLanguage().isBlank()
                ? "Source"
                : file.getLanguage();
        return new FileDescription(file,
                language + " file: " + humanize(base),
                DescriptionOrigin.FALLBACK,
                0.45,
                null);
    }

    private static FileDescription heuristic(FileInfo file, String text, double confidence) {
        return new FileDescription(file, text, DescriptionOrigin.HEURISTIC, confidence, null);
    }

    private static String specialFile(String lowerName, String pathLower, String language) {
        if (lowerName.equals("dockerfile") || lowerName.startsWith("dockerfile.")) {
            return "Container image definition";
        }
        if (lowerName.equals("docker-compose.yml") || lowerName.equals("docker-compose.yaml")
                || lowerName.equals("compose.yml") || lowerName.equals("compose.yaml")) {
            return "Multi-service container orchestration";
        }
        if (lowerName.equals("package.json")) {
            return "Node.js package manifest and scripts";
        }
        if (lowerName.equals("package-lock.json") || lowerName.equals("yarn.lock")
                || lowerName.equals("pnpm-lock.yaml")) {
            return "Locked dependency versions";
        }
        if (lowerName.equals("pom.xml")) {
            return "Maven project build definition";
        }
        if (lowerName.equals("build.gradle") || lowerName.equals("build.gradle.kts")
                || lowerName.equals("settings.gradle") || lowerName.equals("settings.gradle.kts")) {
            return "Gradle build definition";
        }
        if (lowerName.equals("go.mod")) {
            return "Go module definition";
        }
        if (lowerName.equals("cargo.toml")) {
            return "Rust package manifest";
        }
        if (lowerName.equals("pyproject.toml") || lowerName.equals("requirements.txt")
                || lowerName.equals("setup.py") || lowerName.equals("setup.cfg")) {
            return "Python project dependencies and packaging";
        }
        if (lowerName.equals("tsconfig.json") || lowerName.equals("jsconfig.json")) {
            return "TypeScript/JavaScript compiler configuration";
        }
        if (lowerName.startsWith("readme")) {
            return "Project overview and usage notes";
        }
        if (lowerName.equals("codeowners")) {
            return "Code ownership map";
        }
        if (pathLower.contains(".github/workflows/") && (lowerName.endsWith(".yml") || lowerName.endsWith(".yaml"))) {
            return "CI workflow definition";
        }
        if (lowerName.equals("application.yml") || lowerName.equals("application.yaml")
                || lowerName.equals("application.properties")
                || lowerName.matches("application-[\\w.-]+\\.(yml|yaml|properties)")) {
            return "Application runtime configuration";
        }
        if (lowerName.equals("openapi.yaml") || lowerName.equals("openapi.yml")
                || lowerName.equals("swagger.yaml") || lowerName.equals("swagger.yml")
                || pathLower.contains("/openapi/")) {
            return "API contract (OpenAPI/Swagger)";
        }
        if (pathLower.contains("/migrations/") || lowerName.endsWith(".sql")) {
            return "Database schema or migration";
        }
        if (lowerName.equals(".env.example") || lowerName.equals("env.example")) {
            return "Example environment variables for local setup";
        }
        if ("YAML".equalsIgnoreCase(language) && pathLower.contains("config")) {
            return "Configuration file";
        }
        return null;
    }

    private static String roleFromName(String base) {
        String[][] suffixes = {
                {"Controller", "HTTP API controller"},
                {"RestController", "HTTP API controller"},
                {"Resource", "HTTP API resource"},
                {"Service", "Application service"},
                {"Repository", "Data access repository"},
                {"Dao", "Data access object"},
                {"Entity", "Domain entity"},
                {"Model", "Domain model"},
                {"Dto", "Data transfer object"},
                {"Request", "Request payload"},
                {"Response", "Response payload"},
                {"Config", "Configuration wiring"},
                {"Configuration", "Configuration wiring"},
                {"Exception", "Error type"},
                {"Error", "Error type"},
                {"Filter", "Request/response filter"},
                {"Interceptor", "Call interceptor"},
                {"Middleware", "Request middleware"},
                {"Handler", "Event or request handler"},
                {"Listener", "Event listener"},
                {"Consumer", "Message consumer"},
                {"Producer", "Message producer"},
                {"Publisher", "Event publisher"},
                {"Subscriber", "Event subscriber"},
                {"Client", "External service client"},
                {"Gateway", "Integration gateway"},
                {"Mapper", "Object mapper"},
                {"Converter", "Type converter"},
                {"Validator", "Validation logic"},
                {"Factory", "Object factory"},
                {"Builder", "Object builder"},
                {"Util", "Shared helpers"},
                {"Utils", "Shared helpers"},
                {"Helper", "Shared helpers"},
                {"Constants", "Shared constants"},
                {"Enum", "Enumerated values"},
                {"Application", "Application entry point"},
                {"Main", "Program entry point"},
                {"App", "Application entry component"},
        };
        for (String[] pair : suffixes) {
            if (base.endsWith(pair[0]) && base.length() > pair[0].length()) {
                String subject = humanize(base.substring(0, base.length() - pair[0].length()));
                return pair[1] + " for " + subject;
            }
            if (base.equals(pair[0])) {
                return pair[1];
            }
        }
        return null;
    }

    private static String roleFromPath(String pathLower, String base) {
        if (pathLower.contains("/controller") || pathLower.contains("/controllers/")
                || pathLower.contains("/web/") || pathLower.contains("/api/")) {
            return "Web/API layer: " + humanize(base);
        }
        if (pathLower.contains("/service") || pathLower.contains("/services/")) {
            return "Service layer: " + humanize(base);
        }
        if (pathLower.contains("/repository") || pathLower.contains("/repositories/")
                || pathLower.contains("/dao/")) {
            return "Persistence layer: " + humanize(base);
        }
        if (pathLower.contains("/domain/") || pathLower.contains("/model/")
                || pathLower.contains("/entity")) {
            return "Domain model: " + humanize(base);
        }
        if (pathLower.contains("/config/") || pathLower.contains("/configuration/")) {
            return "Configuration: " + humanize(base);
        }
        if (pathLower.contains("/handler") || pathLower.contains("/handlers/")) {
            return "Handler: " + humanize(base);
        }
        if (pathLower.contains("/workflow")) {
            return "Workflow definition: " + humanize(base);
        }
        return null;
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot <= 0) {
            return fileName;
        }
        // Keep compound test extensions meaningful before strip in TestFileDetector path
        return fileName.substring(0, dot);
    }

    static String humanize(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String spaced = raw
                .replaceAll("([a-z0-9])([A-Z])", "$1 $2")
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2")
                .replaceAll("[_\\-.]+", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (spaced.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(spaced.charAt(0))
                + (spaced.length() > 1 ? spaced.substring(1) : "");
    }
}
