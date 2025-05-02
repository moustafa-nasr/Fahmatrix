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
    
    public void print() {
        if (columns.isEmpty()) {
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
                System.out.print("| " + column.get(i) + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    // Add more operations as needed
}