package com.fahmatrix;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.lang.reflect.Field;

class DataFrameConstructorTest {
    
    @Test
    @DisplayName("Test default constructor creates empty DataFrame")
    void testDefaultConstructor() throws Exception {
        // Arrange & Act
        DataFrame df = new DataFrame();
        
        // Assert - Use reflection to access private fields for testing
        Field columnsField = DataFrame.class.getDeclaredField("columns");
        Field indexField = DataFrame.class.getDeclaredField("index");
        columnsField.setAccessible(true);
        indexField.setAccessible(true);
        
        Map<String, List<Object>> columns = (Map<String, List<Object>>) columnsField.get(df);
        List<String> index = (List<String>) indexField.get(df);
        
        assertNotNull(columns, "Columns map should not be null");
        assertNotNull(index, "Index list should not be null");
        assertTrue(columns.isEmpty(), "Columns map should be empty");
        assertTrue(index.isEmpty(), "Index list should be empty");
        assertTrue(columns instanceof LinkedHashMap, "Columns should be LinkedHashMap to preserve order");
        assertTrue(index instanceof ArrayList, "Index should be ArrayList");
    }

    @Test
    @DisplayName("Test constructor with index parameter")
    void testConstructorWithIndex() throws Exception {
        // Arrange
        List<String> inputIndex = new ArrayList<>(Arrays.asList("row1", "row2", "row3"));
        
        // Act
        DataFrame df = new DataFrame(inputIndex);
        
        // Assert - Use reflection to access private fields
        Field columnsField = DataFrame.class.getDeclaredField("columns");
        Field indexField = DataFrame.class.getDeclaredField("index");
        columnsField.setAccessible(true);
        indexField.setAccessible(true);
        
        Map<String, List<Object>> columns = (Map<String, List<Object>>) columnsField.get(df);
        List<String> index = (List<String>) indexField.get(df);
        
        assertNotNull(columns, "Columns map should not be null");
        assertNotNull(index, "Index list should not be null");
        assertTrue(columns.isEmpty(), "Columns map should be empty");
        assertEquals(3, index.size(), "Index should have 3 elements");
        assertEquals(Arrays.asList("row1", "row2", "row3"), index, "Index should match input");
        
        // Verify it's a copy, not the same reference
        assertNotSame(inputIndex, index, "Index should be a copy, not the same reference");
        
        // Verify modifying original doesn't affect DataFrame
        inputIndex.add("row4");
        assertEquals(3, index.size(), "DataFrame index should not be affected by external changes");
    }

    @Test
    @DisplayName("Test constructor with empty index list")
    void testConstructorWithEmptyIndex() throws Exception {
        // Arrange
        List<String> emptyIndex = new ArrayList<>();
        
        // Act
        DataFrame df = new DataFrame(emptyIndex);
        
        // Assert
        Field indexField = DataFrame.class.getDeclaredField("index");
        indexField.setAccessible(true);
        List<String> index = (List<String>) indexField.get(df);
        
        assertNotNull(index, "Index should not be null");
        assertTrue(index.isEmpty(), "Index should be empty");
        assertNotSame(emptyIndex, index, "Should be a copy, not same reference");
    }

    @Test
    @DisplayName("Test constructor with null index")
    void testConstructorWithNullIndex() {
        // This test depends on implementation behavior
        // If null handling is not implemented, this will throw NullPointerException
        assertThrows(NullPointerException.class, () -> {
            new DataFrame(null);
        }, "Constructor should handle null index appropriately");
    }

    @Test
    @DisplayName("Test constructor with index and columns")
    void testConstructorWithIndexAndColumns() throws Exception {
        // Arrange
        List<String> inputIndex = new ArrayList<>(Arrays.asList("row1", "row2", "row3"));
        Map<String, List<Object>> inputColumns = new LinkedHashMap<>();
        inputColumns.put("col1", new ArrayList<>(Arrays.asList("a", "b", "c")));
        inputColumns.put("col2", new ArrayList<>(Arrays.asList(1, 2, 3)));
        inputColumns.put("col3", new ArrayList<>(Arrays.asList(true, false, true)));
        
        // Act
        DataFrame df = new DataFrame(inputIndex, inputColumns);
        
        // Assert - Use reflection to access private fields
        Field columnsField = DataFrame.class.getDeclaredField("columns");
        Field indexField = DataFrame.class.getDeclaredField("index");
        columnsField.setAccessible(true);
        indexField.setAccessible(true);
        
        Map<String, List<Object>> columns = (Map<String, List<Object>>) columnsField.get(df);
        List<String> index = (List<String>) indexField.get(df);
        
        // Verify index
        assertNotNull(index, "Index should not be null");
        assertEquals(3, index.size(), "Index should have 3 elements");
        assertEquals(Arrays.asList("row1", "row2", "row3"), index, "Index should match input");
        assertNotSame(inputIndex, index, "Index should be a copy");
        
        // Verify columns
        assertNotNull(columns, "Columns should not be null");
        assertEquals(3, columns.size(), "Should have 3 columns");
        assertTrue(columns.containsKey("col1"), "Should contain col1");
        assertTrue(columns.containsKey("col2"), "Should contain col2");
        assertTrue(columns.containsKey("col3"), "Should contain col3");
        
        // Verify column data
        assertEquals(Arrays.asList("a", "b", "c"), columns.get("col1"), "col1 data should match");
        assertEquals(Arrays.asList(1, 2, 3), columns.get("col2"), "col2 data should match");
        assertEquals(Arrays.asList(true, false, true), columns.get("col3"), "col3 data should match");
        
        // Verify deep copy - columns should be copies, not same references
        assertNotSame(inputColumns, columns, "Columns map should be a copy");
        assertNotSame(inputColumns.get("col1"), columns.get("col1"), "Column lists should be copies");
        
        // Verify order is preserved (LinkedHashMap)
        List<String> columnNames = new ArrayList<>(columns.keySet());
        assertEquals(Arrays.asList("col1", "col2", "col3"), columnNames, "Column order should be preserved");
    }

    @Test
    @DisplayName("Test constructor with empty columns map")
    void testConstructorWithEmptyColumns() throws Exception {
        // Arrange
        List<String> inputIndex = new ArrayList<>(Arrays.asList("row1", "row2"));
        Map<String, List<Object>> emptyColumns = new HashMap<>();
        
        // Act
        DataFrame df = new DataFrame(inputIndex, emptyColumns);
        
        // Assert
        Field columnsField = DataFrame.class.getDeclaredField("columns");
        columnsField.setAccessible(true);
        Map<String, List<Object>> columns = (Map<String, List<Object>>) columnsField.get(df);
        
        assertNotNull(columns, "Columns should not be null");
        assertTrue(columns.isEmpty(), "Columns should be empty");
        assertNotSame(emptyColumns, columns, "Should be a copy");
    }

    @Test
    @DisplayName("Test constructor handles null values in data")
    void testConstructorWithNullValues() throws Exception {
        // Arrange
        List<String> inputIndex = new ArrayList<>(Arrays.asList("row1", "row2", "row3"));
        Map<String, List<Object>> inputColumns = new LinkedHashMap<>();
        inputColumns.put("col1", new ArrayList<>(Arrays.asList("a", null, "c")));
        inputColumns.put("col2", new ArrayList<>(Arrays.asList(1, 2, null)));
        
        // Act
        DataFrame df = new DataFrame(inputIndex, inputColumns);
        
        // Assert
        Field columnsField = DataFrame.class.getDeclaredField("columns");
        columnsField.setAccessible(true);
        Map<String, List<Object>> columns = (Map<String, List<Object>>) columnsField.get(df);
        
        assertEquals(Arrays.asList("a", null, "c"), columns.get("col1"), "Should handle null values in col1");
        assertEquals(Arrays.asList(1, 2, null), columns.get("col2"), "Should handle null values in col2");
    }

    @Test
    @DisplayName("Test constructor with mixed data types")
    void testConstructorWithMixedDataTypes() throws Exception {
        // Arrange
        List<String> inputIndex = new ArrayList<>(Arrays.asList("row1", "row2", "row3", "row4"));
        Map<String, List<Object>> inputColumns = new LinkedHashMap<>();
        inputColumns.put("mixed", new ArrayList<>(Arrays.asList("string", 42, 3.14, true)));
        
        // Act
        DataFrame df = new DataFrame(inputIndex, inputColumns);
        
        // Assert
        Field columnsField = DataFrame.class.getDeclaredField("columns");
        columnsField.setAccessible(true);
        Map<String, List<Object>> columns = (Map<String, List<Object>>) columnsField.get(df);
        
        List<Object> mixedColumn = columns.get("mixed");
        assertEquals("string", mixedColumn.get(0), "Should handle String");
        assertEquals(42, mixedColumn.get(1), "Should handle Integer");
        assertEquals(3.14, mixedColumn.get(2), "Should handle Double");
        assertEquals(true, mixedColumn.get(3), "Should handle Boolean");
    }

    @Test
    @DisplayName("Test constructor creates defensive copies")
    void testConstructorDefensiveCopies() throws Exception {
        // Arrange
        List<String> inputIndex = new ArrayList<>(Arrays.asList("row1", "row2"));
        List<Object> col1Data = new ArrayList<>(Arrays.asList("a", "b"));
        Map<String, List<Object>> inputColumns = new LinkedHashMap<>();
        inputColumns.put("col1", col1Data);
        
        // Act
        DataFrame df = new DataFrame(inputIndex, inputColumns);
        
        // Assert - Modify original data
        inputIndex.add("row3");
        col1Data.add("c");
        inputColumns.put("col2", new ArrayList<>(Arrays.asList(1, 2)));
        
        // Get DataFrame internal state
        Field columnsField = DataFrame.class.getDeclaredField("columns");
        Field indexField = DataFrame.class.getDeclaredField("index");
        columnsField.setAccessible(true);
        indexField.setAccessible(true);
        
        Map<String, List<Object>> columns = (Map<String, List<Object>>) columnsField.get(df);
        List<String> index = (List<String>) indexField.get(df);
        
        // Verify DataFrame is unaffected by external changes
        assertEquals(2, index.size(), "Index should not be affected by external changes");
        assertEquals(1, columns.size(), "Columns count should not be affected");
        assertEquals(2, columns.get("col1").size(), "Column data should not be affected");
        assertEquals(Arrays.asList("a", "b"), columns.get("col1"), "Column data should remain unchanged");
        assertFalse(columns.containsKey("col2"), "New columns should not appear");
    }

    @Test
    @DisplayName("Test constructor with null parameters")
    void testConstructorWithNullParameters() {
        // Test null index with null columns
        assertThrows(NullPointerException.class, () -> {
            new DataFrame(null, null);
        }, "Should handle null parameters appropriately");
        
        // Test valid index with null columns
        List<String> validIndex = new ArrayList<>(Arrays.asList("row1", "row2"));
        assertThrows(NullPointerException.class, () -> {
            new DataFrame(validIndex, null);
        }, "Should handle null columns parameter appropriately");
    }

    @Test
    @DisplayName("Test constructor preserves insertion order")
    void testConstructorPreservesOrder() throws Exception {
        // Arrange - Use LinkedHashMap to ensure specific order
        List<String> inputIndex = Arrays.asList("row1", "row2");
        Map<String, List<Object>> inputColumns = new LinkedHashMap<>();
        inputColumns.put("zebra", Arrays.asList(1, 2));
        inputColumns.put("alpha", Arrays.asList(3, 4));
        inputColumns.put("beta", Arrays.asList(5, 6));
        
        // Act
        DataFrame df = new DataFrame(inputIndex, inputColumns);
        
        // Assert
        Field columnsField = DataFrame.class.getDeclaredField("columns");
        columnsField.setAccessible(true);
        Map<String, List<Object>> columns = (Map<String, List<Object>>) columnsField.get(df);
        
        List<String> actualOrder = new ArrayList<>(columns.keySet());
        List<String> expectedOrder = Arrays.asList("zebra", "alpha", "beta");
        
        assertEquals(expectedOrder, actualOrder, "Column insertion order should be preserved");
    }
}