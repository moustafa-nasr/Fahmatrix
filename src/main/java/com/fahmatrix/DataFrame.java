package com.fahmatrix;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataFrame {

    private Map<String, List<Object>> columns;
    private List<String> index;
    
    public DataFrame() {
        this.columns = new LinkedHashMap<>();
        this.index = new ArrayList<>();
    }
    
    public void addColumn(String name, List<Object> data) {
        columns.put(name, new ArrayList<>(data));
        // Automatically generate index if empty
        if (index.isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                index.add(String.valueOf(i));
            }
        }
    }
    
    public Series getColumn(String name) {
        return new Series(columns.get(name), new ArrayList<>(index));
    }
    
    // Add more operations as needed
}