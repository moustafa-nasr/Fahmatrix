package com.fahmatrix;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.io.PrintStream;

class SeriesTest {

    @Test
    void testConstructor() {
        List<Object> values = Arrays.asList(1, 2, 3);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        assertEquals(3, series.count());
        assertEquals(OptionalDouble.of(2.0), series.mean());
        assertEquals(OptionalDouble.of(3.0), series.max());
    }

    @Test
    void testGet() {
        List<Object> values = Arrays.asList(1, 2, 3);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        assertEquals(OptionalDouble.of(1.0), series.min());
        assertEquals(OptionalDouble.of(3.0), series.max());

        // test get by position
        for (int i = 0; i < series.count(); i++) {
            assertEquals(values.get(i), (int) series.get(i));
        }
    }

    @Test
    void testPrint() {
        List<Object> values = Arrays.asList(1, 2, 3);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        PrintStream originalOut = System.out; // Save original System.out

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        System.setOut(new PrintStream(out));

        series.print();

        String expectedOutput = "Series \r\n" +
                "Index\t| Value\r\n" +
                "----------------\r\n" +
                "a\t| 1\r\n" +
                "b\t| 2\r\n" +
                "c\t| 3\r\n";

        String outString = "";
        try {
            outString = out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        System.setOut(originalOut); // Restore the original

        assertEquals(expectedOutput.trim(), outString.trim());
    }

    @Test
    void testCount() {
        List<Object> values = Arrays.asList(1, 2, 3);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        assertEquals(3, series.count());
    }

    @Test
    void testMin() {
        List<Object> values = Arrays.asList(1, 2, 3);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        assertEquals(OptionalDouble.of(1.0), series.min());
    }

    @Test
    void testMax() {
        List<Object> values = Arrays.asList(1, 2, 3);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        assertEquals(OptionalDouble.of(3.0), series.max());
    }

    @Test
    void testSum() {
        List<Object> values = Arrays.asList(1, 2, 3);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        assertEquals(OptionalDouble.of(6.0), series.sum());
    }

    @Test
    void testMean() {
        List<Object> values = Arrays.asList(1, 3, 4);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        assertEquals(8.0/3.0, series.mean().getAsDouble(), 0.0001);
    }

    @Test
    void testMedian() {
        List<Object> values = Arrays.asList(1, 3, 4);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        assertEquals(OptionalDouble.of(3.0), series.median());
    }

    @Test
    void testStdDev() {
        List<Object> values = Arrays.asList(1, 3, 4);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        assertEquals(OptionalDouble.of(1.247219128924647), series.stdDev());
    }

    @Test
    void testPercentiles() {
        List<Object> values = Arrays.asList(1, 2, 3);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        assertEquals(Map.of(25.0, 1.5, 50.0, 2.0, 75.0, 2.5), series.percentiles());
    }

    @Test
    void testQuantile25() {
        List<Object> values = Arrays.asList(1, 3, 4);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        assertEquals(OptionalDouble.of(2.0), series.quantile25());
    }

    @Test
    void testQuantile50() {
        List<Object> values = Arrays.asList(1, 3, 4);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        assertEquals(OptionalDouble.of(3.0), series.quantile50());
    }

    @Test
    void testQuantile75() {
        List<Object> values = Arrays.asList(1, 3, 4);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        assertEquals(OptionalDouble.of(3.5), series.quantile75());
    }

    @Test
    void testPercentile() {
        List<Object> values = Arrays.asList(1, 3, 4);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        assertEquals(OptionalDouble.of(3.0), series.percentile(50));
    }

    @Test
    void testProcessInParallel() {
        List<Object> values = Arrays.asList(1, 3, 4);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        DoubleUnaryOperator function = x -> (double) x + 1;
        double[] result = series.processInParallel(function);

        assertArrayEquals(new double[] { 2.0, 4.0, 5.0 }, result);
    }

    @Test
    void testProcessInParallelUsesMultipleThreads() {
        // Use more elements to increase chance of parallel execution
        List<Object> values = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        List<String> index = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l");
        Series series = new Series(values, index);

        Set<String> threadNames = Collections.synchronizedSet(new HashSet<>());
        DoubleUnaryOperator function = x -> {
            threadNames.add(Thread.currentThread().getName());
            // Add small delay to encourage parallel execution
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            return x + 1;
        };

        double[] result = series.processInParallel(function);

        // Verify the function worked correctly
        double[] expected = { 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0 };
        assertArrayEquals(expected, result);

        // Verify multiple threads were used (indicates parallel execution)
        assertTrue(threadNames.size() > 1, "Should use multiple threads for parallel execution");
    }

    @Test
    void testProcessInParallelHandlesExceptions() {
        List<Object> values = Arrays.asList(1, 3, 4);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        DoubleUnaryOperator function = x -> {
            if (x == 3.0) {
                throw new RuntimeException("Test exception for value: " + x);
            }
            return x + 1;
        };

        // Verify that exceptions are properly handled
        Exception exception = assertThrows(RuntimeException.class, () -> {
            series.processInParallel(function);
        });

        assertTrue(exception.getMessage().contains("Test exception for value: 3"));
    }

    @Test
    void testForEachParallel() {
        List<Object> values = Arrays.asList(1, 3, 4);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);
        PrintStream originalOut = System.out; // Save original System.out

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        System.setOut(new PrintStream(out));

        Consumer<Object> action = x -> System.out.println(x);

        series.forEachParallel(action);

        String outString = "";
        try {
            outString = out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.setOut(originalOut); // Restore the original

        assertTrue(outString.contains("1"));
        assertTrue(outString.contains("3"));
        assertTrue(outString.contains("4"));
    }

    @Test
    void testForEachParallelProcessesAllElements() {
        List<Object> values = Arrays.asList(1, 3, 4);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        // Use a thread-safe collection to capture processed values
        Set<Object> processedValues = Collections.synchronizedSet(new HashSet<>());
        Consumer<Object> action = x -> processedValues.add(x);

        series.forEachParallel(action);

        // Verify all values were processed
        assertEquals(3, processedValues.size());
        assertTrue(processedValues.contains(1));
        assertTrue(processedValues.contains(3));
        assertTrue(processedValues.contains(4));
    }

    @Test
    void testForEachParallelUsesMultipleThreads() {
        // Use more elements to increase chance of parallel execution
        List<Object> values = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<String> index = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
        Series series = new Series(values, index);

        Set<String> threadNames = Collections.synchronizedSet(new HashSet<>());
        Consumer<Object> action = x -> {
            threadNames.add(Thread.currentThread().getName());
            // Add small delay to encourage parallel execution
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        };

        series.forEachParallel(action);

        // Verify multiple threads were used (indicates parallel execution)
        assertTrue(threadNames.size() > 1, "Should use multiple threads for parallel execution");
    }

    @Test
    void testForEachParallelHandlesExceptions() {
        List<Object> values = Arrays.asList(1, 3, 4);
        List<String> index = Arrays.asList("a", "b", "c");
        Series series = new Series(values, index);

        Consumer<Object> action = x -> {
            if (x.equals(3)) {
                throw new RuntimeException("Test exception for value: " + x);
            }
        };

        // Verify that exceptions are properly handled
        Exception exception = assertThrows(RuntimeException.class, () -> {
            series.forEachParallel(action);
        });

        assertTrue(exception.getMessage().contains("Test exception for value: 3"));
    }

}