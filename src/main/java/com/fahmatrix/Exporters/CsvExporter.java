package com.fahmatrix.Exporters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.Comparator;

/**
 * A class responsible for exporting data in CSV format.
 * <br>
 * 
 */
public class CsvExporter {

    private String fileName;
    private char delimiter = ',';
    private boolean hasQuotes = true;

    /**
     * Constructs a new CsvExporter instance with the specified file name.
     *
     * @param fileName the file name to export
     */
    public CsvExporter(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Constructs a new CsvExporter instance with the specified file name,
     * delimiter and hasQuotes flags. The delimiter is used to separate fields in the CSV file.
     *
     * @param fileName  the file name to export
     * @param delimiter the character used as field separator (default: ',')
     * @param hasQuotes whether fields are enclosed with quotes (default: true)
     */
    public CsvExporter(String fileName, char delimiter, boolean hasQuotes) {
        this.fileName = fileName;
        this.delimiter = delimiter;
        this.hasQuotes = hasQuotes;
    }

    /**
     * Returns the file name associated with this CsvExporter instance.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the file name for this CsvExporter instance.
     *
     * @param fileName the new file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    
    /**
     * Save CSV data to file
     * <br>
     *
     * @param columns Map of column names to values
     */
    public void saveCSV(Map<String, List<Object>> columns) throws IOException {

        // 1. Create parent directories if they don't exist
        File file = new File(fileName);
        File parentDir = file.getParentFile();
        
        if (parentDir != null) {  // Check if path has parent directories
            parentDir.mkdirs();   // Create all necessary parent directories
        }

        // 2. Create the file if it doesn't exist
        if (!file.exists()) {
            boolean created = file.createNewFile();
            if (!created) {
                throw new IOException("Failed to create file: " + fileName);
            }
        }

        // 3. Write CSV data
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, false))) {
            // Write headers
            String[] headers = columns.keySet().toArray(new String[0]);
            for (int i = 0; i < headers.length; i++) {
                String tmpValue = headers[i];

                if (hasQuotes) {
                        tmpValue.replace("\"", "\"\"");
                        writer.print("\"" + tmpValue + "\"");
                    } else {
                        writer.print(tmpValue);
                    }
                if (i < headers.length - 1) {
                    writer.print(delimiter);
                }
            }
            writer.println();


            
            // iteration 1: trasposed data before output
            // Write data using parallelStream() for better performance with large datasets
            int longestColumnData = columns.values().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

            for (int currentIndex = 0; currentIndex < longestColumnData; currentIndex++) {
                for (int i = 0; i < headers.length; i++) {
                    String string = headers[i];
                    boolean isLast = (i == headers.length - 1);

                    Object value = columns.get(string).get(currentIndex);
                    String tmpValue = value !=null ? value.toString():"null";
                    
                    // TODO escape newline, commas(delimiter) and double qoutes https://datatracker.ietf.org/doc/html/rfc4180#page-2
                    if (hasQuotes) {
                        tmpValue.replace("\"", "\"\"");
                        writer.print("\"" + tmpValue + "\"");
                    } else {
                        writer.print(tmpValue);
                    }

                    if (!isLast)
                        writer.print(delimiter);
                }
                writer.println();
            }


            // iteration 2: data output must be transposed before calling saveCSV
            // Write data using parallelStream() for better performance with large datasets
            // columns.values().stream().parallel().forEachOrdered(values -> {
            //     for (Object value : values) {

            //         String tmpValue = value.toString();

            //         // TODO escape newline, commas(delimiter) and double qoutes https://datatracker.ietf.org/doc/html/rfc4180#page-2
            //         if (hasQuotes) {
            //             tmpValue.replace("\"", "\"\"");
            //             writer.print("\"" + tmpValue + "\"");
            //         } else {
            //             writer.print(tmpValue);
            //         }
            //         if (values.indexOf(value) < values.size() - 1) {
            //             writer.print(delimiter);
            //         }
            //     }
            //     writer.println();
            // });
            
            


        }
    }

}