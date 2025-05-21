package com.fahmatrix.Exporters;

import java.io.*;
import java.util.*;

/**
 * A class responsible for exporting data in JSON format.
 * <br>
 * ex: small_data.json
 * [
 *  {"age": 25, "name": "Alice"},
 *  {"age": 30, "name": "Bob"},
 *  {"age": 35, "name": "Charlie"}
 * ]
 * 
 */
public class JsonExporter {

    private String fileName;

    /**
     * Constructs a new JsonExporter instance with the specified file name.
     *
     * @param fileName the file name to export
     */
    public JsonExporter(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns the file name associated with this JsonExporter instance.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the file name for this JsonExporter instance.
     *
     * @param fileName the new file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Save JSON data to file
     * <br>
     *
     * @param columns Map of column names to values
     */
    public void saveJSON(Map<String, List<Object>> columns) throws IOException {

        // 1. Create parent directories if they don't exist
        File file = new File(fileName);
        File parentDir = file.getParentFile();

        if (parentDir != null) {
            parentDir.mkdirs();
        }

        // 2. Create the file if it doesn't exist
        if (!file.exists()) {
            boolean created = file.createNewFile();
            if (!created) {
                throw new IOException("Failed to create file: " + fileName);
            }
        }

        // 3. Write JSON
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
            writer.write("[\n");

            String[] headers = columns.keySet().toArray(new String[0]);
            int rowCount = columns.values().stream()
                    .mapToInt(List::size)
                    .max()
                    .orElse(0);

            for (int i = 0; i < rowCount; i++) {
                writer.write("  {");

                for (int j = 0; j < headers.length; j++) {
                    String key = headers[j];
                    List<Object> col = columns.get(key);
                    Object value = (i < col.size()) ? col.get(i) : null;

                    writer.write("\"" + escapeString(key) + "\": ");
                    writer.write(serializeJsonValue(value));

                    if (j < headers.length - 1) {
                        writer.write(", ");
                    }
                }

                writer.write("}");
                if (i < rowCount - 1) {
                    writer.write(",");
                }
                writer.newLine();
            }

            writer.write("]");
        }
    }

    /**
     * Converts object to JSON value as a string, So it can be printed easily without losing it's type
     * <br>
     * 
     * @param value Object to be converted
     * @return JSON value as a string ex: "Joe",15,105.5,true
     */
    private String serializeJsonValue(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else {
            return "\"" + escapeString(value.toString()) + "\"";
        }
    }

    /**
     * Helper Class to escape special characters from string
     * <br>
     * 
     * @param input String to escape
     * @return Escapsd string
     */
    private String escapeString(String input) {
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}

