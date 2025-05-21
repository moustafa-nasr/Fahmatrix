package com.fahmatrix.Importers;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class JsonImporter {

    private static final long MEMORY_EFFICIENT_THRESHOLD = 10_000_000; // 10MB
    private Map<String, List<Object>> columns = new LinkedHashMap<>();
    private List<String> index = new ArrayList<>();

    public void readJSON(String filePath) throws IOException {
        long fileSize = Files.size(Paths.get(filePath));

        if (fileSize < MEMORY_EFFICIENT_THRESHOLD) {
            try {
                readJSONInMemory(filePath);
            } catch (OutOfMemoryError e) {
                columns.clear();
                index.clear();
                System.gc();
                readJSONStreaming(filePath);
            }
        } else {
            readJSONStreaming(filePath);
        }
    }

    private void readJSONInMemory(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        content = content.trim();
        
        if (content.startsWith("[")) {
            parseJsonArray(content);
        } else {
            throw new IOException("Unsupported JSON structure. Must be a JSON array of objects.");
        }
    }

    private void readJSONStreaming(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            boolean insideObject = false;
            int braceCount = 0;
            int rowId = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("{")) {
                    insideObject = true;
                    braceCount = 0;
                    sb.setLength(0); // reset buffer
                }

                if (insideObject) {
                    sb.append(line);
                    for (char c : line.toCharArray()) {
                        if (c == '{') braceCount++;
                        else if (c == '}') braceCount--;
                    }

                    if (braceCount == 0) {
                        Map<String, Object> parsed = parseFlatJsonObject(sb.toString());
                        addRow(parsed, rowId++);
                        insideObject = false;
                    }
                }
            }
        }
    }

    private void parseJsonArray(String content) {
        int rowId = 0;
        int braceCount = 0;
        boolean insideObject = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < content.length() - 1; i++) {
            char c = content.charAt(i);

            if (c == '{') {
                braceCount++;
                insideObject = true;
            }

            if (insideObject) {
                sb.append(c);
            }

            if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    Map<String, Object> parsed = parseFlatJsonObject(sb.toString());
                    addRow(parsed, rowId++);
                    sb.setLength(0);
                    insideObject = false;
                }
            }
        }
    }

    private Map<String, Object> parseFlatJsonObject(String json) {
        Map<String, Object> result = new LinkedHashMap<>();
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) return result;

        json = json.substring(1, json.length() - 1); // remove braces

        boolean inQuotes = false;
        boolean escaping = false;
        // StringBuilder key = new StringBuilder();
        // StringBuilder value = new StringBuilder();
        // boolean parsingKey = true;
        List<String> pairs = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            current.append(c);

            if (c == '"' && !escaping) {
                inQuotes = !inQuotes;
            }

            if (c == '\\' && !escaping) {
                escaping = true;
                continue;
            }

            if (!inQuotes && c == ',') {
                pairs.add(current.substring(0, current.length() - 1).trim());
                current.setLength(0);
            }

            escaping = false;
        }

        if (current.length() > 0) {
            pairs.add(current.toString().trim());
        }

        for (String pair : pairs) {
            int colonIdx = pair.indexOf(':');
            if (colonIdx == -1) continue;

            String rawKey = pair.substring(0, colonIdx).trim();
            String rawValue = pair.substring(colonIdx + 1).trim();

            rawKey = unquote(rawKey);
            Object parsedValue = parseValue(rawValue);
            result.put(rawKey, parsedValue);
        }

        return result;
    }

    private void addRow(Map<String, Object> row, int rowId) {
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            columns.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(entry.getValue());
        }
        index.add("row_" + rowId);
    }

    private Object parseValue(String rawValue) {
        if (rawValue.startsWith("\"") && rawValue.endsWith("\"")) {
            return unquote(rawValue);
        } else if ("null".equalsIgnoreCase(rawValue)) {
            return null;
        } else if ("true".equalsIgnoreCase(rawValue) || "false".equalsIgnoreCase(rawValue)) {
            return Boolean.parseBoolean(rawValue);
        } else {
            try {
                if (rawValue.contains(".") || rawValue.contains("e") || rawValue.contains("E")) {
                    return Double.parseDouble(rawValue);
                } else {
                    return Long.parseLong(rawValue);
                }
            } catch (NumberFormatException e) {
                return rawValue;
            }
        }
    }

    private String unquote(String str) {
        if (str == null || str.length() < 2) return str;
        if (str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1).replace("\\\"", "\"");
        }
        return str;
    }

    public Map<String, List<Object>> getColumns() {
        return columns;
    }

    public List<String> getIndex() {
        return index;
    }
}
