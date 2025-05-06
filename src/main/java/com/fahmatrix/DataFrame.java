package com.fahmatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

import com.fahmatrix.Importers.CsvImporter;

/**
 * DataFrame is the basic object for hadling data
 * <br>
 * <br>
 * Current Features: <br>
 * Select/Add column data <br>
 * Print in System Console <br>
 * Import From CSV/TSV <br>
 */
public class DataFrame {

    /*
     * Current Features
     * Print in System Console
     * Import From CSV/TSV
     */

    private Map<String, List<Object>> columns;
    private List<String> index;

    /**
     * Constructor with empty indexes and columns
     * <br>
     */
    public DataFrame() {
        this.columns = new LinkedHashMap<>();
        this.index = new ArrayList<>();
    }

    /**
     * Constructor with known indexes and empty columns
     * <br>
     * 
     * @param index Array of indexes
     */
    public DataFrame(List<String> index) {
        this.columns = new LinkedHashMap<>();
        this.index = index;
    }

    /**
     * Constructor with known indexes and columns
     * <br>
     * 
     * @param index   Array of indexes
     * @param columns Map of data where key is the column name and the value is
     *                Array of cells data
     */
    public DataFrame(List<String> index, Map<String, List<Object>> columns) {
        if (index.size() != columns.size()) {
            throw new IllegalArgumentException("Values and index must be same length");
        }
        this.columns = columns;
        this.index = index;
    }

    /**
     * Add column to the end of data table
     * <br>
     * 
     * @param name index name
     * @param data column data (Array of cells data)
     */
    public void addColumn(String name, List<Object> data) {
        columns.put(name, new ArrayList<>(data));
        // Automatically generate index if empty
        if (index.isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                index.add(String.valueOf(i));
            }
        }
    }

    /**
     * return a new Series Object with data of only one column
     * <br>
     * 
     * @param name column name
     * @return Series data with index
     */
    public Series getColumn(String name) {
        return new Series(columns.get(name), new ArrayList<>(index));
    }

    /**
     * Return the first 5 rows as DataFrame Object
     * 
     * @return first 5 rows
     */
    public DataFrame head() {
        return head(5);
    }

    /**
     * Return the first n rows as DataFrame Object
     * 
     * @param n the max number of rows to return
     * @return rows
     */
    public DataFrame head(int n) {
        DataFrame subset = new DataFrame();
        int rows = Math.min(n, index.size());

        for (Map.Entry<String, List<Object>> entry : columns.entrySet()) {
            List<Object> data = entry.getValue();
            List<Object> subsetData = new ArrayList<>();

            for (int i = 0; i < Math.min(rows, data.size()); i++) {
                subsetData.add(data.get(i));
            }

            subset.addColumn(entry.getKey(), subsetData);
        }

        return subset;
    }

    /**
     * Return the last 5 rows as DataFrame Object
     * 
     * @return last 5 rows
     */
    public DataFrame tail() {
        return tail(5);
    }

    /**
     * Return the last n rows as DataFrame Object
     * 
     * @param n the max number of rows to return
     * @return rows
     */
    public DataFrame tail(int n) {
        DataFrame subset = new DataFrame();
        int rows = Math.min(n, index.size());

        for (Map.Entry<String, List<Object>> entry : columns.entrySet()) {
            List<Object> data = entry.getValue();
            int start = Math.max(data.size() - rows, 0);
            List<Object> subsetData = new ArrayList<>(data.subList(start, data.size()));
            subset.addColumn(entry.getKey(), subsetData);
        }

        return subset;
    }

    /**
     * Pretty Print in System Console
     * <br>
     */
    public void print() {
        if (columns.isEmpty() || index.isEmpty()) {
            System.out.println("Empty DataFrame");
            return;
        }

        // Print header
        System.out.print("Index\t");
        for (String colName : columns.keySet()) {
            System.out.print("| " + colName + "\t");
        }
        System.out.println();

        // Print separator line
        System.out.print("--------");
        for (int i = 0; i < columns.size(); i++) {
            System.out.print("+-------");
        }
        System.out.println();

        // Print each row
        for (int i = 0; i < index.size(); i++) {
            System.out.print(index.get(i) + "\t");

            for (List<Object> column : columns.values()) {
                // Safe access with bounds checking
                String value = (i < column.size()) ? String.valueOf(column.get(i)) : "null";
                System.out.print("| " + value + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Pretty Print Data Summery in System Console
     */
    public void describe() {
        if (columns.isEmpty()) {
            System.out.println("Empty DataFrame");
            return;
        }

        System.out.println("DataFrame Description:");
        System.out.print("|\t");
        for (Map.Entry<String, List<Object>> entry : columns.entrySet()) {
            System.out.print("| " + entry.getKey() + "\t");
        }
        System.out.println();
        for (int i = 0; i <= columns.size(); i++) {
            System.out.print("+-------");
        }
        System.out.println();

        System.out.print("| Count\t");
        for (Map.Entry<String, List<Object>> entry : columns.entrySet()) {
            System.out.print("| " + getColumn(entry.getKey()).count() + "\t");
        }
        System.out.println();
        for (int i = 0; i <= columns.size(); i++) {
            System.out.print("+-------");
        }
        System.out.println();

        System.out.print("| Min \t");
        for (Map.Entry<String, List<Object>> entry : columns.entrySet()) {
            System.out.print("| " + getColumn(entry.getKey()).min().orElse(0.0) + "\t");
        }
        System.out.println();
        for (int i = 0; i <= columns.size(); i++) {
            System.out.print("+-------");
        }
        System.out.println();

        System.out.print("| Max \t");
        for (Map.Entry<String, List<Object>> entry : columns.entrySet()) {
            System.out.print("| " + getColumn(entry.getKey()).max().orElse(0.0) + "\t");
        }
        System.out.println();
        for (int i = 0; i <= columns.size(); i++) {
            System.out.print("+-------");
        }
        System.out.println();

        System.out.print("| Sum \t");
        for (Map.Entry<String, List<Object>> entry : columns.entrySet()) {
            System.out.print("| " + getColumn(entry.getKey()).sum().orElse(0.0) + "\t");
        }
        System.out.println();
        for (int i = 0; i <= columns.size(); i++) {
            System.out.print("+-------");
        }
        System.out.println();

        System.out.print("| Mean \t");
        for (Map.Entry<String, List<Object>> entry : columns.entrySet()) {
            System.out.print("| " + getColumn(entry.getKey()).mean().orElse(0.0) + "\t");
        }
        System.out.println();
        for (int i = 0; i <= columns.size(); i++) {
            System.out.print("+-------");
        }
        System.out.println();

        System.out.print("| Stdev\t");
        for (Map.Entry<String, List<Object>> entry : columns.entrySet()) {
            System.out.print("| " + getColumn(entry.getKey()).stdDev().orElse(0.0) + "\t");
        }
        System.out.println();
        for (int i = 0; i <= columns.size(); i++) {
            System.out.print("+-------");
        }
        System.out.println();

        System.out.print("| 25%\t");
        for (Map.Entry<String, List<Object>> entry : columns.entrySet()) {
            System.out.print("| " + getColumn(entry.getKey()).quantile25().orElse(0.0) + "\t");
        }
        System.out.println();
        for (int i = 0; i <= columns.size(); i++) {
            System.out.print("+-------");
        }
        System.out.println();

        System.out.print("| 50%\t");
        for (Map.Entry<String, List<Object>> entry : columns.entrySet()) {
            System.out.print("| " + getColumn(entry.getKey()).median().orElse(0.0) + "\t");
        }
        System.out.println();
        for (int i = 0; i <= columns.size(); i++) {
            System.out.print("+-------");
        }
        System.out.println();

        System.out.print("| 75%\t");
        for (Map.Entry<String, List<Object>> entry : columns.entrySet()) {
            System.out.print("| " + getColumn(entry.getKey()).quantile75().orElse(0.0) + "\t");
        }
        System.out.println();
        for (int i = 0; i <= columns.size(); i++) {
            System.out.print("+-------");
        }
        System.out.println();

    }

    /**
     * Read , Parse and save the CSV file<br>
     * Make sure the file is found before calling. And it has a proper CSV/TSV
     * format <br>
     * All data are saved in the same object no need to create a new one <br>
     * <br>
     * Note: it replace any old data <br>
     * <br>
     * 
     * @param filePath CSV file path
     * @return the same object after saving data (this) if successful
     *         <br>
     */
    public DataFrame readCSV(String filePath) {
        try {
            CsvImporter csvObject = new CsvImporter();
            csvObject.readCSV(filePath);
            columns = csvObject.getColumns();
            index = csvObject.getIndex();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return this;
    }

    // Add more operations as needed
}