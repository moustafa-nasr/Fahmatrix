package com.fahmatrix.Helpers;

import com.fahmatrix.DataFrame;

public class DataSelector {
    private final DataFrame df;
    private String[] rowLabels;
    private String[] colLabels;
    private int[] rowIndices;
    private int[] colIndices;

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
        this.df = df;
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
     * Fetch selected data
     * <br>
     * this is the end of builder chain
     * <br>
     * 
     * @return final data as DataFrame
     */
    public DataFrame get() {
        if (rowLabels != null || colLabels != null) {
            return df.getByLabels(
                    rowLabels != null ? rowLabels : new String[0],
                    colLabels != null ? colLabels : new String[0]);
        } else {
            return df.getByPositions(
                    rowIndices != null ? rowIndices : new int[0],
                    colIndices != null ? colIndices : new int[0]);
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
        if (rowLabels != null && colLabels != null && rowLabels.length == 1 && colLabels.length == 1) {
            return df.getByLabel(rowLabels[0], colLabels[0]);
        } else if (rowIndices != null && colIndices != null && rowIndices.length == 1 && colIndices.length == 1) {
            return df.getByPosition(rowIndices[0], colIndices[0]);
        }
        throw new IllegalStateException("Single value access requires exactly one row and one column");
    }
}