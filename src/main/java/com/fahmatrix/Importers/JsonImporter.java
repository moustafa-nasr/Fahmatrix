package com.fahmatrix.Importers;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class JsonImporter {

    private static final long MEMORY_EFFICIENT_THRESHOLD = 10_000_000; // 10MB
    private Map<String, List<Object>> columns = new LinkedHashMap<>();
    private List<String> index = new ArrayList<>();

    /**
     * Reads JSON data from a text file.
     * <br>
     * This method attempts to read the entire file into memory if it is under the
     * specified threshold. If the file size exceeds this threshold, it will switch
     * to streaming mode to avoid OutOfMemoryErrors.
     * <br>
     * 
     * @param filePath the path to the JSON file
     * @throws IOException if there is an issue reading the file
     */
    public void readJSON(String filePath) throws IOException {
        long fileSize = Files.size(Paths.get(filePath));

        if (fileSize < MEMORY_EFFICIENT_THRESHOLD) {
            try {
                readJSONInMemory(filePath);
            } catch (OutOfMemoryError e) {
                // If we run out of memory, clear and switch to streaming

                if (columns != null) {
                    columns.clear();
                    columns = null;
                }
                if (index != null) {
                    index.clear();
                    index = null;
                }

                // Give JVM a moment to clean up naturally
                try {
                    Thread.sleep(100); // Brief pause to allow natural GC
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

                columns = new HashMap<>();
                index = new ArrayList<>();

                readJSONStreaming(filePath);
            }
        } else {
            readJSONStreaming(filePath);
        }
    }

    /**
     * Reads JSON from a text file into memory.
     * <br>
     * This method reads the entire file into memory and parses it as a JSON array
     * of objects. It is only used when the file size is under the specified
     * threshold.
     * <br>
     * 
     * @param filePath the path to the JSON file
     * @throws IOException if there is an issue reading the file
     */
    private void readJSONInMemory(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        content = content.trim();

        if (content.startsWith("[")) {
            parseJsonArray(content);
        } else {
            throw new IOException("Unsupported JSON structure. Must be a JSON array of objects.");
        }
    }

    /**
     * Reads JSON data from a text file in streaming mode.
     * <br>
     * This method reads the file line by line, parses each object as a flat JSON
     * object (i.e., with no nesting), and adds it to the internal data structure.
     * <br>
     *
     * @param filePath the path to the JSON file
     * @throws IOException if there is an issue reading the file
     */
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
                        if (c == '{')
                            braceCount++;
                        else if (c == '}')
                            braceCount--;
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

    /**
     * Parses a JSON array from the given content.
     * <br>
     * This method iterates through the content, identifying and parsing individual
     * objects within the array.
     * <br>
     * 
     * @param content the JSON array content
     */
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

    /**
     * Parses a flat JSON object from the given content.
     * <br>
     * This method iterates through the content, identifying and parsing individual
     * key-value pairs within the object.
     * <br>
     * 
     * @param json the JSON object content
     * @return a Map of key-value pairs representing the parsed JSON object
     */
    private Map<String, Object> parseFlatJsonObject(String json) {

        Map<String, Object> result = new LinkedHashMap<>();
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}"))
            return result;

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
            if (colonIdx == -1)
                continue;

            String rawKey = pair.substring(0, colonIdx).trim();
            String rawValue = pair.substring(colonIdx + 1).trim();

            rawKey = unquote(rawKey);
            Object parsedValue = parseValue(rawValue);
            result.put(rawKey, parsedValue);
        }

        return result;
    }

    /**
     * Adds a new row to the internal data structure.
     * <br>
     * This method takes a parsed JSON object and adds its key-value pairs to the
     * columns Map, as well as adding a new index entry.
     * <br>
     * 
     * @param row   the parsed JSON object
     * @param rowId the ID of the row being added
     */
    private void addRow(Map<String, Object> row, int rowId) {
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            columns.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(entry.getValue());
        }
        index.add("row_" + rowId);
    }

    /**
     * Parses a value from the given raw string.
     * <br>
     * This method attempts to parse the raw string as a number or boolean, and
     * returns it as an Object. If the parsing fails, it will return the raw string
     * itself.
     * <br>
     *
     * @param rawValue the raw string to be parsed
     * @return the parsed value as an Object
     */
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

    /**
     * Removes double quotes from a string.
     * <br>
     * 
     * @param str the input string
     * @return the input string with double quotes removed
     */
    private String unquote(String str) {
        if (str == null || str.length() < 2)
            return str;
        if (str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1).replace("\\\"", "\"");
        }
        return str;
    }

    /**
     * Returns the columns map containing all the data.
     * <br>
     * 
     * @return the columns map
     */
    public Map<String, List<Object>> getColumns() {
        return columns;
    }

    /**
     * Returns the index list containing all the row IDs.
     * <br>
     * 
     * @return the index list
     */
    public List<String> getIndex() {
        return index;
    }
}
