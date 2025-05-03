package com.fahmatrix.Importers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CsvImporter {

    /* Future Features
     * Adjust the threshold based on your typical available memory
     * Consider adding progress tracking for large files
     * Add file encoding detection for international files
     * Implement chunked processing for very large files (e.g., >1GB)
     */

    private static final long MEMORY_EFFICIENT_THRESHOLD = 10_000_000; // 10MB threshold
    private Map<String, List<Object>> columns = new LinkedHashMap<>();
    private List<String> index = new ArrayList<>();

    /**
     * Main Read CSV method
     * @param filePath
     * @throws IOException
     */
    public void readCSV(String filePath) throws IOException {
        long fileSize = Files.size(Paths.get(filePath));
        
        if (fileSize < MEMORY_EFFICIENT_THRESHOLD) {
            try {
                readCSVInMemory(filePath);  // Fast but uses more memory
            } catch (OutOfMemoryError e) {
                // If we run out of memory, clear and switch to streaming
                columns.clear();
                index.clear();
                System.gc();
                readCSVStreaming(filePath);
            }
        } else {
            readCSVStreaming(filePath); // Slower but memory efficient
        }
    }

    /**
     * In-Memory read for fast and small files
     * @param filePath
     * @throws IOException
     */
    private void readCSVInMemory(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        if (lines.isEmpty()) return;
        
        CSVFormat format = detectCSVFormat(lines.get(0));
        String[] headers = parseCSVLine(lines.get(0), format);
        
        initializeColumns(headers);
        
        for (int i = 1; i < lines.size(); i++) {
            processCSVLine(lines.get(i), format, headers);
        }
        
        generateIndex();
    }

    /**
     * Stream read for large files
     * @param filePath
     * @throws IOException
     */
    private void readCSVStreaming(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return;
            
            CSVFormat format = detectCSVFormat(headerLine);
            String[] headers = parseCSVLine(headerLine, format);
            
            initializeColumns(headers);
            
            String line;
            while ((line = reader.readLine()) != null) {
                processCSVLine(line, format, headers);
            }
            
            generateIndex();
        }
    }

    /**
     * Detect CSV format
     * Supports ',', '\t', ';', '|', '#',':' delimiters
     * Handles quotes
     * @param sampleLine
     * @return CSVformat Object
     */
    private CSVFormat detectCSVFormat(String sampleLine) {
        // Check for common delimiters
        char[] delimiters = {',', '\t', ';', '|', '#',':'};
        int[] delimiterCounts = new int[delimiters.length];
        
        for (int i = 0; i < delimiters.length; i++) {
            delimiterCounts[i] = countOccurrences(sampleLine, delimiters[i]);
        }
        
        // Find most common delimiter
        int maxIndex = 0;
        for (int i = 1; i < delimiterCounts.length; i++) {
            if (delimiterCounts[i] > delimiterCounts[maxIndex]) {
                maxIndex = i;
            }
        }
        
        char delimiter = delimiters[maxIndex];
        boolean hasQuotes = sampleLine.contains("\"");
        
        return new CSVFormat(delimiter, hasQuotes);
    }

    /**
     * Parse single line into String Array logic
     * used in header detection and before converting into column
     * @param line
     * @param format
     * @return
     */
    private String[] parseCSVLine(String line, CSVFormat format) {
        if (!format.hasQuotes()) {
            return line.split(String.valueOf(format.getDelimiter()));
        }
        
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i < line.length() - 1 && line.charAt(i + 1) == '"') {
                    // Escaped quote
                    currentField.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == format.getDelimiter() && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }

    /**
     * Initialize Columns according to detected headers
     * @param headers
     */
    private void initializeColumns(String[] headers) {
        columns.clear();
        for (String header : headers) {
            columns.put(header.trim(), new ArrayList<>());
        }
    }

    /**
     * Parse and convert single line into column logic
     * @param line
     * @param format
     * @param headers
     */
    private void processCSVLine(String line, CSVFormat format, String[] headers) {
        String[] values = parseCSVLine(line, format);
        for (int i = 0; i < headers.length && i < values.length; i++) {
            columns.get(headers[i].trim()).add(parseValue(values[i].trim()));
        }
    }

    /**
     * Parse single cell into proper value
     * Supports String, Integer And Double.
     * Future Feature: Date and Time.
     * @param value cell value
     * @return proper object
     */
    private Object parseValue(String value) {
        if (value.isEmpty()) return null;
        
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            return value;
        }
    }

    /**
     * Count the number of occurrences for certain character in string
     * Used to detect the delimiter
     * @param str single line from the csv file
     * @param ch delimiter character
     * @return number of occurances
     */
    private int countOccurrences(String str, char ch) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) count++;
        }
        return count;
    }

    /**
     * Generate index from data
     */
    private void generateIndex() {
        index.clear();
        if (!columns.isEmpty()) {
            int rowCount = columns.values().iterator().next().size();
            for (int i = 0; i < rowCount; i++) {
                index.add(String.valueOf(i));
            }
        }
    }
    
    /**
     * Get the parsed columns
     * @return parsed columns
     */
    public Map<String, List<Object>> getColumns() {
        return columns;
    }

    /**
     * Get the parsed index
     * @return parsed index
     */
    public List<String> getIndex() {
        return index;
    }

    /**
     * CSV format object
     */
    private class CSVFormat {
        private final char delimiter;
        private final boolean hasQuotes;
        
        public CSVFormat(char delimiter, boolean hasQuotes) {
            this.delimiter = delimiter;
            this.hasQuotes = hasQuotes;
        }
        
        public char getDelimiter() {
            return delimiter;
        }
        
        public boolean hasQuotes() {
            return hasQuotes;
        }
    }

}
