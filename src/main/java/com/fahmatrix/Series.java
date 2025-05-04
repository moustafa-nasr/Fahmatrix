package com.fahmatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Series is the basic object for one column
 * <br>
 * <br>
 * Current Features: <br>
 * Select single cell data <br>
 * Print in System Console <br>
 */
public class Series {

    private List<Object> values;
    private List<String> index;

    /**
     * Constructor
     * <br>
     * @param values column data (Array of cells data)
     * @param index  Array of indexes
     */
    public Series(List<Object> values, List<String> index) {
        if (values.size() != index.size()) {
            throw new IllegalArgumentException("Values and index must be same length");
        }
        this.values = new ArrayList<>(values);
        this.index = new ArrayList<>(index);
    }

    /**
     * Get certain cell data
     * <br>
     * @param position position in numbers starting from 0
     * @return cell value as object (String, Double, Float, etc..)
     */
    public Object get(int position) {
        return values.get(position);
    }

    /**
     * Get certain cell data by index name
     * <br>
     * <br>
     * Note: the index value is different than the column name <br>
     * <br>
     * @param indexValue index by value string
     * @return cell value as object (String, Double, Float, etc..)
     */
    public Object get(String indexValue) {
        int pos = index.indexOf(indexValue);
        return pos >= 0 ? values.get(pos) : null;
    }

    /**
     * Pretty Print in System Console
     * <br>
     */
    public void print() {
        if (values.isEmpty() || index.isEmpty()) {
            System.out.println("Empty Series");
            return;
        }

        System.out.println("Series ");
        System.out.println("Index\t| Value");
        System.out.println("----------------");

        for (int i = 0; i < values.size(); i++) {
            String value = (values.get(i) != null) ? String.valueOf(values.get(i)) : "null";
            System.out.println(index.get(i) + "\t| " + value);
        }

        System.out.println();
    }

    // Add arithmetic operations, filtering, etc.
}
