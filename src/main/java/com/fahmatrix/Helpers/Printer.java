package com.fahmatrix.Helpers;

import java.util.List;
import java.util.Map;

import com.fahmatrix.Series;

public class Printer {

    private static Map<String, List<Object>> columns;
    private static List<String> index;

    /**
     * Pretty Print Data Summary in System Console
     * <br>
     * 
     */
    public static void describe(List<String> index, Map<String, List<Object>> columns) {
        if (columns.isEmpty()) {
            System.out.println("Empty DataFrame");
            return;
        }

        if (index.isEmpty()) {
            System.out.println("Empty DataFrame");
            return;
        }

        Printer.columns = columns;
        Printer.index = index;

        System.out.println("DataFrame Description:");
        printHeader();
        printSeparator();

        // Array of statistics to display
        StatisticRow[] statistics = {
                new StatisticRow("Count", columnName -> String.valueOf(getColumn(columnName).count())),
                new StatisticRow("Min", columnName -> String.valueOf(getColumn(columnName).min().orElse(0.0))),
                new StatisticRow("Max", columnName -> String.valueOf(getColumn(columnName).max().orElse(0.0))),
                new StatisticRow("Sum", columnName -> String.valueOf(getColumn(columnName).sum().orElse(0.0))),
                new StatisticRow("Mean", columnName -> String.valueOf(getColumn(columnName).mean().orElse(0.0))),
                new StatisticRow("Stdev", columnName -> String.valueOf(getColumn(columnName).stdDev().orElse(0.0))),
                new StatisticRow("25%", columnName -> String.valueOf(getColumn(columnName).quantile25().orElse(0.0))),
                new StatisticRow("50%", columnName -> String.valueOf(getColumn(columnName).median().orElse(0.0))),
                new StatisticRow("75%", columnName -> String.valueOf(getColumn(columnName).quantile75().orElse(0.0)))
        };

        // Print each statistic row
        for (StatisticRow stat : statistics) {
            printStatisticRow(stat);
            printSeparator();
        }
    }

    /**
     * Prints the header row with column names
     */
    private static void printHeader() {
        System.out.print("|\t");
        for (Map.Entry<String, List<Object>> entry : columns.entrySet()) {
            System.out.print("| " + entry.getKey() + "\t");
        }
        System.out.println();
    }

    /**
     * Prints a separator line
     */
    private static void printSeparator() {
        for (int i = 0; i <= columns.size(); i++) {
            System.out.print("+-------");
        }
        System.out.println();
    }

    /**
     * Prints a row of statistics
     */
    private static void printStatisticRow(StatisticRow statisticRow) {
        System.out.print("| " + statisticRow.label + "\t");
        for (Map.Entry<String, List<Object>> entry : columns.entrySet()) {
            String value = statisticRow.valueExtractor.apply(entry.getKey());
            System.out.print("| " + value + "\t");
        }
        System.out.println();
    }

    /*
     * Helper method to get column as a Series
     */
    private static Series getColumn(String name) {
        return new Series(columns.get(name), index);
    }

    /**
     * Helper class to represent a statistic row
     */
    private static class StatisticRow {
        final String label;
        final java.util.function.Function<String, String> valueExtractor;

        StatisticRow(String label, java.util.function.Function<String, String> valueExtractor) {
            this.label = label;
            this.valueExtractor = valueExtractor;
        }
    }

}
