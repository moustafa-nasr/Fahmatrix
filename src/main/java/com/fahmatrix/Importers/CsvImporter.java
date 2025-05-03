package com.fahmatrix.Importers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CsvImporter {
    
    private Map<String, List<Object>> columns = new LinkedHashMap<>();
    private List<String> index = new ArrayList<>();

    public void readCSV(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // First detect the format
            CSVFormat format = detectCSVFormat(reader);
            
            // Reset reader after detection
            reader.close();
            BufferedReader newReader = new BufferedReader(new FileReader(filePath));
            
            // Read header
            String headerLine = newReader.readLine();
            if (headerLine == null){ 
                newReader.close();
                return;
            }
            
            String[] headers = parseCSVLine(headerLine, format);
            
            // Initialize columns
            for (String header : headers) {
                columns.put(header.trim(), new ArrayList<>());
            }
            
            // Read data rows
            String line;
            while ((line = newReader.readLine()) != null) {
                String[] values = parseCSVLine(line, format);
                
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    columns.get(headers[i].trim()).add(parseValue(values[i].trim()));
                }
            }
            
            // Generate index
            if (!columns.isEmpty()) {
                int rowCount = columns.values().iterator().next().size();
                for (int i = 0; i < rowCount; i++) {
                    index.add(String.valueOf(i));
                }
            }
            newReader.close();
        }
    }

    private CSVFormat detectCSVFormat(BufferedReader reader) throws IOException {
        reader.mark(10000); // Mark to reset later
        
        // Read first few lines to detect format
        String firstLine = reader.readLine();
        if (firstLine == null) {
            throw new IOException("Empty CSV file");
        }
        
        // Check for common delimiters
        char[] delimiters = {',', '\t', ';', '|', '#'};
        int[] delimiterCounts = new int[delimiters.length];
        
        for (int i = 0; i < delimiters.length; i++) {
            delimiterCounts[i] = countOccurrences(firstLine, delimiters[i]);
        }
        
        // Find most common delimiter
        int maxIndex = 0;
        for (int i = 1; i < delimiterCounts.length; i++) {
            if (delimiterCounts[i] > delimiterCounts[maxIndex]) {
                maxIndex = i;
            }
        }
        
        char delimiter = delimiters[maxIndex];
        boolean hasQuotes = firstLine.contains("\"");
        
        reader.reset();
        return new CSVFormat(delimiter, hasQuotes);
    }

    private int countOccurrences(String str, char ch) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }

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

    private Object parseValue(String value) {
        if (value.isEmpty()) {
            return null;
        }
        
        // Try parsing as number
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            // Not a number, return as string
            return value;
        }
    }
    
    public Map<String, List<Object>> getColumns() {
        return columns;
    }

    public List<String> getIndex() {
        return index;
    }

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
