package com.fahmatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;

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
     * 
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
     * 
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
     * 
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

    /**
     * Returns count of non-null values
     * <br>
     * 
     * @return count
     */
    public long count() {
        return values.parallelStream()
                .filter(Objects::nonNull)
                .count();
    }

    /**
     * Returns minimum value (works with Number types)
     * <br>
     * 
     * @return minimum value
     */
    public OptionalDouble min() {
        return values.parallelStream()
                .filter(Number.class::isInstance)
                .mapToDouble(v -> ((Number) v).doubleValue())
                .min();
    }

    //
    /**
     * Returns maximum value (works with Number types)
     * <br>
     * 
     * @return maximum value
     */
    public OptionalDouble max() {
        return values.parallelStream()
                .filter(Number.class::isInstance)
                .mapToDouble(v -> ((Number) v).doubleValue())
                .max();
    }

    /**
     * Sum of values (works with Number types)
     * <br>
     * 
     * @return sum
     */
    public OptionalDouble sum() {
        return numericValues().parallel().reduce(Double::sum);
    }

    /**
     * Average of values (works with Number types)
     * <br>
     * 
     * @return Average
     */
    public OptionalDouble mean() {
        return numericValues().parallel().average();
    }

    /**
     * Median of values (works with Number types)
     * <br>
     * 
     * @return Median
     */
    public OptionalDouble median() {
        double[] sorted = numericValues().parallel().sorted().toArray();
        if (sorted.length == 0)
            return OptionalDouble.empty();
        if (sorted.length % 2 == 0) {
            return OptionalDouble.of((sorted[sorted.length / 2] + sorted[sorted.length / 2 - 1]) / 2);
        }
        return OptionalDouble.of(sorted[sorted.length / 2]);
    }

    /**
     * Standard deviation of values (works with Number types)
     * <br>
     * 
     * @return Standard deviation
     */
    public OptionalDouble stdDev() {
        DoubleSummaryStatistics stats = numericValues().parallel().summaryStatistics();
        if (stats.getCount() == 0)
            return OptionalDouble.empty();

        double[] squaredDiffs = numericValues().parallel()
                .map(x -> Math.pow(x - stats.getAverage(), 2))
                .toArray();

        double variance = Arrays.stream(squaredDiffs).parallel().sum() / stats.getCount();
        return OptionalDouble.of(Math.sqrt(variance));
    }

    /**
     * Calculate percentiles (25%, 50%, 75% by default)
     * <br>
     * 
     * @return Map of percentiles (e.g., {25=Q1, 50=median, 75=Q3})
     */
    public Map<Double, Double> percentiles() {
        return percentiles(new double[] { 25.0, 50.0, 75.0 });
    }

    /**
     * Calculate custom percentiles using parallel processing (works with Number
     * types)
     * <br>
     * 
     * @param percentiles array of percentiles to compute (e.g., 25.0, 50.0, 75.0)
     * @return Map of requested percentiles
     */
    public Map<Double, Double> percentiles(double[] percentiles) {
        double[] sorted = numericValues().parallel().sorted().toArray();
        if (sorted.length == 0)
            return Collections.emptyMap();

        Map<Double, Double> result = new ConcurrentSkipListMap<>();
        Arrays.stream(percentiles).parallel().forEach(p -> {
            if (p < 0 || p > 100)
                return;
            double pos = p * (sorted.length - 1) / 100;
            int lower = (int) Math.floor(pos);
            int upper = (int) Math.ceil(pos);
            if (lower == upper) {
                result.put(p, sorted[lower]);
            } else {
                result.put(p, sorted[lower] + (pos - lower) * (sorted[upper] - sorted[lower]));
            }
        });
        return result;
    }

    // Convenience methods for common percentiles

    /**
     * Calculate percentile 25% (works with Number types)
     * <br>
     * 
     * @return percentile 25%
     */
    public OptionalDouble quantile25() {
        return percentile(25.0);
    }

    /**
     * Calculate percentile 50% -aka median- (works with Number types)
     * <br>
     * 
     * @return percentile 50%
     */
    public OptionalDouble quantile50() {
        return percentile(50.0);
    }

    /**
     * Calculate percentile 75% (works with Number types)
     * <br>
     * 
     * @return percentile 75%
     */
    public OptionalDouble quantile75() {
        return percentile(75.0);
    }

    /**
     * Calculate percentile for certain value (works with Number types)
     * <br>
     * 
     * @param p percent 0% - 100%
     * @return percentile p%
     */
    public OptionalDouble percentile(double p) {
        if (p < 0 || p > 100)
            return OptionalDouble.empty();
        double[] sorted = numericValues().parallel().sorted().toArray();
        if (sorted.length == 0)
            return OptionalDouble.empty();

        double pos = p * (sorted.length - 1) / 100;
        int lower = (int) Math.floor(pos);
        int upper = (int) Math.ceil(pos);

        if (lower == upper) {
            return OptionalDouble.of(sorted[lower]);
        }
        return OptionalDouble.of(
                sorted[lower] + (pos - lower) * (sorted[upper] - sorted[lower]));
    }

    /**
     * Parallel value processing with custom function (works with Number types)
     * <br>
     * 
     * @param function custom mapping/processing function
     * @return array of proccessed data
     */
    public double[] processInParallel(DoubleUnaryOperator function) {
        return numericValues().parallel()
                .map(function)
                .toArray();
    }

    /**
     * Thread-safe value processing using Consumer action (works with Number types)
     * <br>
     * 
     * @param action custom mapping/processing action
     */
    public void forEachParallel(Consumer<Object> action) {
        values.parallelStream().forEach(action);
    }

    /**
     * Helper method to get all values as Numbers
     * <br>
     * Used in all arithmetic operations to extract the number values. before making
     * any operation
     * <br>
     * 
     * @return number values as stream
     */
    private DoubleStream numericValues() {
        return values.parallelStream()
                .filter(Number.class::isInstance)
                .mapToDouble(v -> ((Number) v).doubleValue());
    }

    // Add more arithmetic operations, filtering, etc.
}
