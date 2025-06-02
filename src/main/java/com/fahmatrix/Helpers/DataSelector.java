package com.fahmatrix.Helpers;

import com.fahmatrix.DataFrame;
import java.util.function.Predicate;

public class DataSelector {
    private String[] rowLabels;
    private String[] colLabels;
    private int[] rowIndices;
    private int[] colIndices;
    private DataFrame filteredData; // Store filtered data if any filters are applied

    /**
     * Main Constructor
     * <br>
     * This is a data selector used to achieve builder pattern data selection.
     * <br>
     * Can be accesed by calling DataFrame.select();
     * <br>
     * 
     * @param df main data to select from
     */
    public DataSelector(DataFrame df) {
        this.filteredData = df; // Initially no filtering
    }

    /**
     * Rows to select by name
     * <br>
     * 
     * @param labels Rows name
     * @return this
     */
    public DataSelector rows(String... labels) {
        this.rowLabels = labels;
        return this;
    }

    /**
     * Columns to select by name
     * <br>
     * 
     * @param labels Columns name
     * @return this
     */
    public DataSelector columns(String... labels) {
        this.colLabels = labels;
        return this;
    }

    /**
     * Rows to select by position
     * <br>
     * 
     * @param indices Rows position
     * @return this
     */
    public DataSelector rows(int... indices) {
        this.rowIndices = indices;
        return this;
    }

    /**
     * Columns to select by position
     * <br>
     * 
     * @param indices Columns position
     * @return this
     */
    public DataSelector columns(int... indices) {
        this.colIndices = indices;
        return this;
    }

    /**
     * Filter rows where the specified column contains the given substring
     * <br>
     * 
     * @param columnName the name of the column to filter
     * @param substring  the substring to search for
     * @return this
     */
    public DataSelector filterContains(String columnName, String substring) {
        this.filteredData = this.filteredData.filterContains(columnName, substring);
        return this;
    }

    /**
     * Filter rows where the specified column equals the given value
     * (case-sensitive)
     * <br>
     * 
     * @param columnName the name of the column to filter
     * @param value      the value to match exactly
     * @return this
     */
    public DataSelector filterEquals(String columnName, String value) {
        this.filteredData = this.filteredData.filterEquals(columnName, value);
        return this;
    }

    /**
     * Filter rows where the specified column equals the given value
     * (case-insensitive)
     * <br>
     * 
     * @param columnName the name of the column to filter
     * @param value      the value to match (ignoring case)
     * @return this
     */
    public DataSelector filterEqualsIgnoreCase(String columnName, String value) {
        this.filteredData = this.filteredData.filterEqualsIgnoreCase(columnName, value);
        return this;
    }

    /**
     * Filter rows based on a custom string predicate
     * <br>
     * 
     * @param columnName the name of the column to filter
     * @param predicate  a function that takes a string and returns true if the row
     *                   should be included
     * @return this
     */
    public DataSelector filterByStringPredicate(String columnName, Predicate<String> predicate) {
        this.filteredData = this.filteredData.filterByStringPredicate(columnName, predicate);
        return this;
    }

    /**
     * Filter rows where the specified column starts with the given prefix
     * <br>
     * 
     * @param columnName the name of the column to filter
     * @param prefix     the prefix to match
     * @return this
     */
    public DataSelector filterStartsWith(String columnName, String prefix) {
        return filterByStringPredicate(columnName, s -> s.startsWith(prefix));
    }

    /**
     * Filter rows where the specified column ends with the given suffix
     * <br>
     * 
     * @param columnName the name of the column to filter
     * @param suffix     the suffix to match
     * @return this
     */
    public DataSelector filterEndsWith(String columnName, String suffix) {
        return filterByStringPredicate(columnName, s -> s.endsWith(suffix));
    }

    /**
     * Filter rows where the specified column matches the given regex pattern
     * <br>
     * 
     * @param columnName the name of the column to filter
     * @param regex      the regular expression pattern to match
     * @return this
     */
    public DataSelector filterRegex(String columnName, String regex) {
        return filterByStringPredicate(columnName, s -> s.matches(regex));
    }

    /**
     * Filter rows where the specified column is not null and not empty
     * <br>
     * 
     * @param columnName the name of the column to filter
     * @return this
     */
    public DataSelector filterNotEmpty(String columnName) {
        return filterByStringPredicate(columnName, s -> s != null && !s.trim().isEmpty());
    }

    /**
     * Fetch selected data
     * <br>
     * this is the end of builder chain
     * <br>
     * 
     * @return final data as DataFrame
     */
    public DataFrame get() {
        DataFrame dataToSelect = this.filteredData; // Use filtered data if any filters were applied

        if (rowLabels != null || colLabels != null) {
            return dataToSelect.getByLabels(
                    rowLabels != null ? rowLabels : new String[0],
                    colLabels != null ? colLabels : new String[0]);
        } else if (rowIndices != null || colIndices != null) {
            return dataToSelect.getByPositions(
                    rowIndices != null ? rowIndices : new int[0],
                    colIndices != null ? colIndices : new int[0]);
        } else {
            return dataToSelect; // Return filtered data if no row/column selection
        }
    }

    /**
     * Fetch a single cell value
     * <br>
     * this is the end of builder chain
     * <br>
     * 
     * @return final value
     */
    public Object getValue() {
        DataFrame dataToSelect = this.filteredData; // Use filtered data if any filters were applied

        if (rowLabels != null && colLabels != null && rowLabels.length == 1 && colLabels.length == 1) {
            return dataToSelect.getByLabel(rowLabels[0], colLabels[0]);
        } else if (rowIndices != null && colIndices != null && rowIndices.length == 1 && colIndices.length == 1) {
            return dataToSelect.getByPosition(rowIndices[0], colIndices[0]);
        }
        throw new IllegalStateException("Single value access requires exactly one row and one column");
    }
}