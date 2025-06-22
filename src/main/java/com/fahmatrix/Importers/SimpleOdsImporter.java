package com.fahmatrix.Importers;

import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fahmatrix.Helpers.FileHelpers;

public class SimpleOdsImporter {

    private Map<String, List<Object>> columns = new LinkedHashMap<>();
    private List<String> index = new ArrayList<>();

    /**
     * Main Read ODS method
     * <br>
     * 
     * @param filePath file path as a string
     * @throws Exception error parsing file or data
     */
    public void readOds(String filePath) throws Exception {
        columns = readOdsAsColumns(filePath);

        if (index.isEmpty()) {
            for (int i = 0; i < getMaxRowCount(); i++) {
                index.add(String.valueOf(i));
            }
        }
    }

    /**
     * Get the parsed columns
     * <br>
     * 
     * @return parsed columns
     */
    public Map<String, List<Object>> getColumns() {
        return columns;
    }

    /**
     * Get the parsed index
     * <br>
     * 
     * @return parsed index
     */
    public List<String> getIndex() {
        return index;
    }

    /**
     * Get maximum row count across all columns
     * <br>
     * 
     * @return maximum row count
     */
    private int getMaxRowCount() {
        return columns.values().stream().mapToInt(List::size).max().orElse(0);
    }

    /**
     * Main reading ODS method
     * <br>
     * 
     * @param filePath file path as a string
     * @return Map of columns data
     * @throws Exception error parsing file or data
     */
    private Map<String, List<Object>> readOdsAsColumns(String filePath) throws Exception {
        try (ZipFile zip = new ZipFile(filePath)) {
            Document contentDoc = extractContentDocument(zip);
            Element table = findFirstTable(contentDoc);
            NodeList rowNodes = table.getElementsByTagName("table:table-row");

            if (rowNodes.getLength() == 0) {
                return new LinkedHashMap<>();
            }

            List<String> headers = extractHeaders(rowNodes);
            Map<String, List<Object>> columnData = initializeColumnData(headers);
            processDataRows(rowNodes, headers, columnData);

            return columnData;
        }
    }

    /**
     * Extracts and parses the content.xml document from the ODS file
     */
    private Document extractContentDocument(ZipFile zip) throws Exception {
        ZipEntry contentEntry = zip.getEntry("content.xml");
        if (contentEntry == null) {
            throw new Exception("content.xml not found in ODS file");
        }

        try (InputStream stream = zip.getInputStream(contentEntry)) {
            return FileHelpers.parseXml(stream);
        }
    }

    /**
     * Finds the first table in the document
     */
    private Element findFirstTable(Document contentDoc) throws Exception {
        NodeList tableNodes = contentDoc.getElementsByTagName("table:table");
        if (tableNodes.getLength() == 0) {
            throw new Exception("No tables found in ODS file");
        }
        return (Element) tableNodes.item(0);
    }

    /**
     * Extracts headers from the first row
     */
    private List<String> extractHeaders(NodeList rowNodes) {
        List<String> headers = new ArrayList<>();

        if (rowNodes.getLength() > 0) {
            Element firstRow = (Element) rowNodes.item(0);
            NodeList headerCells = firstRow.getElementsByTagName("table:table-cell");

            for (int i = 0; i < headerCells.getLength(); i++) {
                Element cell = (Element) headerCells.item(i);
                String header = getCellTextContent(cell);
                String finalHeader = (header != null && !header.trim().isEmpty())
                        ? header.trim()
                        : "Column" + (i + 1);
                headers.add(finalHeader);
            }
        }

        return headers;
    }

    /**
     * Initializes the column data structure
     */
    private Map<String, List<Object>> initializeColumnData(List<String> headers) {
        Map<String, List<Object>> columnData = new LinkedHashMap<>();
        for (String header : headers) {
            columnData.put(header, new ArrayList<>());
        }
        return columnData;
    }

    /**
     * Processes all data rows (excluding header row)
     */
    private void processDataRows(NodeList rowNodes, List<String> headers, Map<String, List<Object>> columnData) {
        for (int rowIdx = 1; rowIdx < rowNodes.getLength(); rowIdx++) {
            Element row = (Element) rowNodes.item(rowIdx);
            processDataRow(row, headers, columnData);
        }
    }

    /**
     * Processes a single data row
     */
    private void processDataRow(Element row, List<String> headers, Map<String, List<Object>> columnData) {
        NodeList cells = row.getElementsByTagName("table:table-cell");
        int currentColumn = 0;

        // Process each cell in the row
        for (int cellIdx = 0; cellIdx < cells.getLength() && currentColumn < headers.size(); cellIdx++) {
            Element cell = (Element) cells.item(cellIdx);
            currentColumn = processCellWithRepetition(cell, headers, columnData, currentColumn);
        }

        // Fill remaining columns with null if row is shorter
        fillRemainingColumnsWithNull(headers, columnData, currentColumn);
    }

    /**
     * Processes a single cell, handling column repetition
     */
    private int processCellWithRepetition(Element cell, List<String> headers,
            Map<String, List<Object>> columnData, int startColumn) {
        int repeatCount = getCellRepeatCount(cell);
        Object value = parseCellValue(cell);
        int currentColumn = startColumn;

        // Add the value to appropriate columns (handling repetition)
        for (int rep = 0; rep < repeatCount && currentColumn < headers.size(); rep++) {
            columnData.get(headers.get(currentColumn)).add(value);
            currentColumn++;
        }

        return currentColumn;
    }

    /**
     * Gets the repeat count for a cell from the columns-repeated attribute
     */
    private int getCellRepeatCount(Element cell) {
        String columnsRepeated = cell.getAttribute("table:number-columns-repeated");
        if (columnsRepeated.isEmpty()) {
            return 1;
        }

        try {
            return Integer.parseInt(columnsRepeated);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /**
     * Fills remaining columns with null values if the row is shorter than expected
     */
    private void fillRemainingColumnsWithNull(List<String> headers, Map<String, List<Object>> columnData,
            int currentColumn) {
        while (currentColumn < headers.size()) {
            columnData.get(headers.get(currentColumn)).add(null);
            currentColumn++;
        }
    }

    /**
     * Helper method to get text content from a table cell
     * <br>
     * 
     * @param cell ODS table cell element
     * @return text content as string
     */
    private String getCellTextContent(Element cell) {
        NodeList paragraphs = cell.getElementsByTagName("text:p");
        if (paragraphs.getLength() > 0) {
            return paragraphs.item(0).getTextContent();
        }
        return null;
    }

    /**
     * Helper method to parse cell value with type detection
     * <br>
     * 
     * @param cell ODS table cell element
     * @return parsed cell value as appropriate type
     */
    private Object parseCellValue(Element cell) {
        String valueType = cell.getAttribute("office:value-type");
        String textContent = getCellTextContent(cell);

        if (textContent == null || textContent.trim().isEmpty()) {
            return null;
        }

        // Use a map of value type parsers to reduce cyclomatic complexity
        Map<String, ValueParser> parsers = createValueParsers();
        ValueParser parser = parsers.getOrDefault(valueType, parsers.get("string"));

        return parser.parse(cell, textContent);
    }

    /**
     * Creates a map of value type parsers
     */
    private Map<String, ValueParser> createValueParsers() {
        Map<String, ValueParser> parsers = new HashMap<>();

        parsers.put("float", new FloatParser());
        parsers.put("currency", new CurrencyParser());
        parsers.put("percentage", new PercentageParser());
        parsers.put("date", new DateParser());
        parsers.put("time", new TimeParser());
        parsers.put("boolean", new BooleanParser());
        parsers.put("string", new StringParser());

        return parsers;
    }

    /**
     * Interface for value parsers
     */
    private interface ValueParser {
        Object parse(Element cell, String textContent);
    }

    /**
     * Parser for float values
     */
    private static class FloatParser implements ValueParser {
        @Override
        public Object parse(Element cell, String textContent) {
            String floatValue = cell.getAttribute("office:value");
            if (!floatValue.isEmpty()) {
                Double result = tryParseDouble(floatValue);
                if (result != null)
                    return result;
            }

            Double result = tryParseDouble(textContent);
            return result != null ? result : textContent;
        }
    }

    /**
     * Parser for currency values
     */
    private static class CurrencyParser implements ValueParser {
        @Override
        public Object parse(Element cell, String textContent) {
            String currencyValue = cell.getAttribute("office:value");
            if (!currencyValue.isEmpty()) {
                Double result = tryParseDouble(currencyValue);
                if (result != null)
                    return result;
            }
            return textContent;
        }
    }

    /**
     * Parser for percentage values
     */
    private static class PercentageParser implements ValueParser {
        @Override
        public Object parse(Element cell, String textContent) {
            String percentValue = cell.getAttribute("office:value");
            if (!percentValue.isEmpty()) {
                Double result = tryParseDouble(percentValue);
                if (result != null)
                    return result * 100; // Convert to percentage
            }
            return textContent;
        }
    }

    /**
     * Parser for date values
     */
    private static class DateParser implements ValueParser {
        @Override
        public Object parse(Element cell, String textContent) {
            String dateValue = cell.getAttribute("office:date-value");
            return !dateValue.isEmpty() ? dateValue : textContent;
        }
    }

    /**
     * Parser for time values
     */
    private static class TimeParser implements ValueParser {
        @Override
        public Object parse(Element cell, String textContent) {
            String timeValue = cell.getAttribute("office:time-value");
            return !timeValue.isEmpty() ? timeValue : textContent;
        }
    }

    /**
     * Parser for boolean values
     */
    private static class BooleanParser implements ValueParser {
        @Override
        public Object parse(Element cell, String textContent) {
            String boolValue = cell.getAttribute("office:boolean-value");
            return !boolValue.isEmpty() ? Boolean.parseBoolean(boolValue) : textContent;
        }
    }

    /**
     * Parser for string values (default)
     */
    private static class StringParser implements ValueParser {
        @Override
        public Object parse(Element cell, String textContent) {
            return textContent;
        }
    }

    /**
     * Helper method to safely parse double values
     */
    private static Double tryParseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}