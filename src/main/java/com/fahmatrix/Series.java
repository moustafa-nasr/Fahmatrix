package com.fahmatrix;

import java.util.ArrayList;
import java.util.List;

public class Series {
    
    private List<Object> values;
    private List<String> index;

    public Series(List<Object> values, List<String> index) {
        if (values.size() != index.size()) {
            throw new IllegalArgumentException("Values and index must be same length");
        }
        this.values = new ArrayList<>(values);
        this.index = new ArrayList<>(index);
    }

    
    public Object get(int position) {
        return values.get(position);
    }
    
    public Object get(String indexValue) {
        int pos = index.indexOf(indexValue);
        return pos >= 0 ? values.get(pos) : null;
    }
    
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
