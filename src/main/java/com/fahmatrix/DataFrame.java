package com.fahmatrix;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.fahmatrix.Exporters.CsvExporter;
import com.fahmatrix.Exporters.JsonExporter;
import com.fahmatrix.Exporters.OdsExporter;
import com.fahmatrix.Exporters.XlsxExporter;
import com.fahmatrix.Helpers.DataSelector;
import com.fahmatrix.Importers.CsvImporter;
import com.fahmatrix.Importers.JsonImporter;
import com.fahmatrix.Importers.SimpleOdsImporter;
import com.fahmatrix.Importers.SimpleXlsxImporter;

/**
 * DataFrame is the basic object for hadling data
 * <br>
 * <br>
 * Current Features: <br>
 * Select/Add column data <br>
 * Print in System Console <br>
 * Print data Summary <br>
 * Import From CSV/TSV, xlsx, Ods, JSON <br>
 * Export To CSV/TSV, xlsx, Ods, JSON <br>
 * Reverse (transpose) data <br>
 * Select Row/Column by Label or Position <br>
 */
public class DataFrame {


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
        // if (index.size() != columns.size()) {
        // throw new IllegalArgumentException("Values and index must be same length
        // "+index.size()+" != "+columns.size());
        // }
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
     * Select Columns by name
     * <br>
     * this method assumes you selected all rows
     * <br>
     * 
     * @param columnLabels Basic String Array (String[]) for column names
     * @return New Dataframe with only the selected data
     */
    public DataFrame getColumnsByLabel(String... columnLabels) {
        return getByLabels(new String[0], columnLabels);
    }

    /**
     * Select certains columns position
     * <br>
     * 
     * @param columnIndices Basic Integer Array (int[]) for columns position
     * @return New Dataframe with only the selected datas
     */
    public DataFrame getColumnsByPosition(int... columnIndices) {
        return getByPositions(new int[0], columnIndices);
    }

    /**
     * Return the first 5 rows as DataFrame Object
     * <br>
     * 
     * @return first 5 rows
     */
    public DataFrame head() {
        return head(5);
    }

    /**
     * Return the first n rows as DataFrame Object
     * <br>
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
     * <br>
     * 
     * @return last 5 rows
     */
    public DataFrame tail() {
        return tail(5);
    }

    /**
     * Return the last n rows as DataFrame Object
     * <br>
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
     * Reverse Rows and Columns
     * <br>
     * 
     * @return new DataFrame with transposed data
     */
    public DataFrame transpose() {
        DataFrame transposed = new DataFrame();
        if (columns.isEmpty())
            return transposed;

        // Create new index from original column names
        List<String> newIndex = new ArrayList<>(columns.keySet());

        // Determine number of rows in original (use first column's size)
        int numRows = columns.values().iterator().next().size();

        // Create transposed columns (original rows become columns)
        for (int i = 0; i < numRows; i++) {
            String colName = index.size() > i ? index.get(i) : String.valueOf(i);
            List<Object> newColumn = new ArrayList<>();

            // Add all values from this original row
            for (List<Object> originalColumn : columns.values()) {
                newColumn.add(originalColumn.size() > i ? originalColumn.get(i) : "null");
            }

            transposed.addColumn(colName, newColumn);
        }

        // Set the new index (original column names)
        transposed.index = newIndex;

        return transposed;
    }

    /**
     * Select Cell by row and column name
     * <br>
     * 
     * @param rowLabel row name
     * @param colLabel column name
     * @return Object for cell value (String, Float, Double)
     */
    public Object getByLabel(String rowLabel, String colLabel) {
        int rowIdx = index.indexOf(rowLabel);
        if (rowIdx == -1)
            throw new IllegalArgumentException("Row label not found");
        List<Object> column = columns.get(colLabel);
        if (column == null)
            throw new IllegalArgumentException("Column label not found");
        return column.get(rowIdx);
    }

    /**
     * Select Rows by name
     * <br>
     * this method assumes you selected all columns
     * <br>
     * 
     * @param rowLabels Basic String Array (String[]) for rows name
     * @return New Dataframe with only the selected data
     */
    public DataFrame getRowsByLabel(String... rowLabels) {
        return getByLabels(rowLabels, columns.keySet().toArray(new String[0]));
    }

    /**
     * Select certains rows and columns by name
     * <br>
     * these names are not Excel A1,B1 names.
     * <br>
     * 
     * @param rowLabels Basic String Array (String[]) for row names
     * @param colLabels Basic String Array (String[]) for column names
     * @return New Dataframe with only the selected data
     */
    public DataFrame getByLabels(String[] rowLabels, String[] colLabels) {
        List<String> newIndex = new ArrayList<>();
        Map<String, List<Object>> newColumns = new LinkedHashMap<>();

        // Filter rows
        List<Integer> rowIndices = new ArrayList<>();
        for (String label : rowLabels) {
            int idx = index.indexOf(label);
            if (idx != -1)
                rowIndices.add(idx);
        }

        // Filter columns
        for (String col : colLabels) {
            if (columns.containsKey(col)) {
                List<Object> newColumn = new ArrayList<>();
                for (int rowIdx : rowIndices) {
                    newColumn.add(columns.get(col).get(rowIdx));
                }
                newColumns.put(col, newColumn);
            }
        }

        // Create new index
        for (int rowIdx : rowIndices) {
            newIndex.add(index.get(rowIdx));
        }

        return new DataFrame(newIndex, newColumns);
    }

    /**
     * Select Cell by row and column positions
     * <br>
     * 
     * @param rowIdx row position
     * @param colIdx column position
     * @return Object for cell value (String, Float, Double)
     */
    public Object getByPosition(int rowIdx, int colIdx) {
        if (rowIdx < 0 || rowIdx >= index.size())
            throw new IndexOutOfBoundsException("Row index out of bounds");
        String colName = new ArrayList<>(columns.keySet()).get(colIdx);
        return columns.get(colName).get(rowIdx);
    }

    /**
     * Select certains rows position
     * <br>
     * 
     * @param rowIndices Basic Integer Array (int[]) for row positions
     * @return New Dataframe with only the selected data
     */
    public DataFrame getRowsByPosition(int... rowIndices) {
        return getByPositions(rowIndices, IntStream.range(0, columns.size()).toArray());
    }

    /**
     * Select certains rows and columns by position
     * <br>
     * 
     * @param rowIndices Basic Integer Array (int[]) for row positions
     * @param colIndices Basic Integer Array (int[]) for column positions
     * @return New Dataframe with only the selected data
     */
    public DataFrame getByPositions(int[] rowIndices, int[] colIndices) {
        List<String> newIndex = new ArrayList<>();
        Map<String, List<Object>> newColumns = new LinkedHashMap<>();

        // Get column names in order
        List<String> columnNames = new ArrayList<>(columns.keySet());

        if (rowIndices.length == 0) {
            rowIndices = IntStream.range(0, columns.keySet().size() - 1).toArray();
        }
        // Filter rows
        for (int rowIdx : rowIndices) {
            if (rowIdx >= 0 && rowIdx < index.size()) {
                newIndex.add(index.get(rowIdx));
            }
        }

        // Filter columns
        for (int colIdx : colIndices) {
            if (colIdx >= 0 && colIdx < columnNames.size()) {
                String colName = columnNames.get(colIdx);
                List<Object> newColumn = new ArrayList<>();
                for (int rowIdx : rowIndices) {
                    if (rowIdx >= 0 && rowIdx < index.size()) {
                        newColumn.add(columns.get(colName).get(rowIdx));
                    }
                }
                newColumns.put(colName, newColumn);
            }
        }

        return new DataFrame(newIndex, newColumns);
    }

    /**
     * Select using builder pattern
     * <br>
     * use like that example <br>
     * select().rows(new int[]{1,2,3}).columns(new int[]{1,2,3}).get() <br>
     * or <br>
     * select().rows(new String[]{"row1","row2","row3"}).columns(new
     * String[]{"column1","column2","column3"}).get()
     * <br>
     * 
     * @return Selection Builder Object
     */
    public DataSelector select() {
        return new DataSelector(this);
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
     * Pretty Print Data Summary in System Console
     * <br>
     * 
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

    /**
     * Exports data to a csv file<br>
     * It useds default delimiter "," and hasQoutes is set to true
     * <br>
     * 
     * @param filePath full file path to save ex:
     *                 ".\\examples\\exampleFiles\\customers-edited.csv"
     */
    public void writeCSV(String filePath) {
        writeCSV(filePath, ',', true);
    }

    /**
     * Exports data to a csv file<br>
     * <br>
     * 
     * @param filePath  full file path to save ex:
     *                  ".\\examples\\exampleFiles\\customers-edited.csv"
     * @param delimiter the csv value delimter ex: ',' or ';' or '|'
     * @param hasQuotes either wrap the values within qoutes or not
     */
    public void writeCSV(String filePath, char delimiter, boolean hasQuotes) {
        try {
            CsvExporter csvExporter = new CsvExporter(filePath, delimiter, hasQuotes);
            csvExporter.saveCSV(columns);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Read , Parse and save the Microsoft Excel SpreadSheet xlsx file<br>
     * Make sure the file is found before calling. <br>
     * All data are saved in the same object no need to create a new one <br>
     * <br>
     * Note: it replace any old data <br>
     * <br>
     * 
     * @param filePath xlsx file path
     * @return the same object after saving data (this) if successful
     *         <br>
     */
    public DataFrame readXlsx(String filePath) {
        try {
            SimpleXlsxImporter xlsxObject = new SimpleXlsxImporter();
            xlsxObject.readExcel(filePath);
            columns = xlsxObject.getColumns();
            index = xlsxObject.getIndex();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Exports data to a Microsoft Excel SpreadSheet xlsx file<br>
     * The data is in the default sheet with name "sheet1"
     * <br>
     * 
     * @param filePath full file path to save ex:
     *                 ".\\examples\\exampleFiles\\small_data.xlsx"
     */
    public void writeXlsx(String filePath) {
        try {
            XlsxExporter xlsxExporter = new XlsxExporter(filePath);
            xlsxExporter.saveXLSX(columns);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Read , Parse and save ODS (OpenDocument Spreadsheet) file<br>
     * Make sure the file is found before calling. <br>
     * All data are saved in the same object no need to create a new one <br>
     * <br>
     * Note: it replace any old data <br>
     * <br>
     * 
     * @param filePath ods file path
     * @return the same object after saving data (this) if successful
     *         <br>
     */
    public DataFrame readOds(String filePath) {
        try {
            SimpleOdsImporter odsObject = new SimpleOdsImporter();
            odsObject.readOds(filePath);
            columns = odsObject.getColumns();
            index = odsObject.getIndex();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Exports data to an ODS (OpenDocument Spreadsheet) file<br>
     * The data is in the default sheet with name "sheet1"
     * <br>
     * 
     * @param filePath full file path to save ex:
     *                 ".\\examples\\exampleFiles\\small_data.ods"
     */
    public void writeOds(String filePath) {
        try {
            OdsExporter odsExporter = new OdsExporter(filePath);
            odsExporter.saveODS(columns);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Read , Parse and save the JSON file<br>
     * Make sure the file is found before calling. And it has a proper JSON format
     * <br>
     * All data are saved in the same object no need to create a new one <br>
     * <br>
     * Note: it replace any old data <br>
     * Note: it supports simple JSON data. (no nesting) <br>
     * <br>
     * example of JSON data.
     * [
     * {"age": 25, "name": "Alice"},
     * {"age": 30, "name": "Bob"},
     * {"age": 35, "name": "Charlie"}
     * ]
     * <br>
     * 
     * @param filePath JSON file path
     * @return the same object after saving data (this) if successful <br>
     */
    public DataFrame readJson(String filePath) {
        try {
            JsonImporter jsonObject = new JsonImporter();
            jsonObject.readJSON(filePath);
            columns = jsonObject.getColumns();
            index = jsonObject.getIndex();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Exports data to a JSON txt file<br>
     * <br>
     * 
     * @param filePath full file path to save ex:
     *                 ".\\examples\\exampleFiles\\small_data.json"
     */
    public void writeJson(String filePath) {
        try {
            JsonExporter jsonExporter = new JsonExporter(filePath);
            jsonExporter.saveJSON(columns);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    // Add more operations as needed
}