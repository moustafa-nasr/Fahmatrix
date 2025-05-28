package com.fahmatrix.Importers;

import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
        Map<String, List<Object>> columnData = new LinkedHashMap<>();
        List<String> headers = new ArrayList<>();

        try (ZipFile zip = new ZipFile(filePath)) {
            // Parse content.xml (main spreadsheet data)
            ZipEntry contentEntry = zip.getEntry("content.xml");
            if (contentEntry == null) {
                throw new Exception("content.xml not found in ODS file");
            }

            try (InputStream stream = zip.getInputStream(contentEntry)) {
                Document contentDoc = parseXml(stream);
                
                // Find the first table (sheet)
                NodeList tableNodes = contentDoc.getElementsByTagName("table:table");
                if (tableNodes.getLength() == 0) {
                    throw new Exception("No tables found in ODS file");
                }
                
                Element table = (Element) tableNodes.item(0);
                NodeList rowNodes = table.getElementsByTagName("table:table-row");

                if (rowNodes.getLength() > 0) {
                    // First row = headers
                    Element firstRow = (Element) rowNodes.item(0);
                    NodeList headerCells = firstRow.getElementsByTagName("table:table-cell");
                    
                    for (int i = 0; i < headerCells.getLength(); i++) {
                        Element cell = (Element) headerCells.item(i);
                        String header = getCellTextContent(cell);
                        headers.add(header != null && !header.trim().isEmpty() ? header.trim() : "Column" + (i + 1));
                        columnData.put(headers.get(i), new ArrayList<>());
                    }

                    // Process data rows (skip first row which contains headers)
                    for (int rowIdx = 1; rowIdx < rowNodes.getLength(); rowIdx++) {
                        Element row = (Element) rowNodes.item(rowIdx);
                        NodeList cells = row.getElementsByTagName("table:table-cell");

                        // Handle repeated cells and columns-repeated attribute
                        int currentColumn = 0;
                        for (int cellIdx = 0; cellIdx < cells.getLength() && currentColumn < headers.size(); cellIdx++) {
                            Element cell = (Element) cells.item(cellIdx);
                            
                            // Check for columns-repeated attribute
                            String columnsRepeated = cell.getAttribute("table:number-columns-repeated");
                            int repeatCount = 1;
                            if (!columnsRepeated.isEmpty()) {
                                try {
                                    repeatCount = Integer.parseInt(columnsRepeated);
                                } catch (NumberFormatException e) {
                                    repeatCount = 1;
                                }
                            }
                            
                            Object value = parseCellValue(cell);
                            
                            // Add the value to appropriate columns (handling repetition)
                            for (int rep = 0; rep < repeatCount && currentColumn < headers.size(); rep++) {
                                columnData.get(headers.get(currentColumn)).add(value);
                                currentColumn++;
                            }
                        }
                        
                        // Fill remaining columns with null if row is shorter
                        while (currentColumn < headers.size()) {
                            columnData.get(headers.get(currentColumn)).add(null);
                            currentColumn++;
                        }
                    }
                }
            }
        }
        return columnData;
    }

    /**
     * Helper method used to parse xml files
     * <br>
     * 
     * @param stream Input stream for xml file inside archive
     * @return parsed xml document
     * @throws Exception
     */
    private Document parseXml(InputStream stream) throws Exception {
        return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(stream);
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
        
        // Handle different value types
        switch (valueType) {
            case "float":
                String floatValue = cell.getAttribute("office:value");
                if (!floatValue.isEmpty()) {
                    try {
                        return Double.parseDouble(floatValue);
                    } catch (NumberFormatException e) {
                        // Fall back to text content
                    }
                }
                try {
                    return Double.parseDouble(textContent);
                } catch (NumberFormatException e) {
                    return textContent;
                }
                
            case "currency":
                String currencyValue = cell.getAttribute("office:value");
                if (!currencyValue.isEmpty()) {
                    try {
                        return Double.parseDouble(currencyValue);
                    } catch (NumberFormatException e) {
                        // Fall back to text content
                    }
                }
                return textContent;
                
            case "percentage":
                String percentValue = cell.getAttribute("office:value");
                if (!percentValue.isEmpty()) {
                    try {
                        return Double.parseDouble(percentValue) * 100; // Convert to percentage
                    } catch (NumberFormatException e) {
                        // Fall back to text content
                    }
                }
                return textContent;
                
            case "date":
                String dateValue = cell.getAttribute("office:date-value");
                if (!dateValue.isEmpty()) {
                    return dateValue; // Return as string for now, can be enhanced to parse as Date
                }
                return textContent;
                
            case "time":
                String timeValue = cell.getAttribute("office:time-value");
                if (!timeValue.isEmpty()) {
                    return timeValue; // Return as string for now
                }
                return textContent;
                
            case "boolean":
                String boolValue = cell.getAttribute("office:boolean-value");
                if (!boolValue.isEmpty()) {
                    return Boolean.parseBoolean(boolValue);
                }
                return textContent;
                
            case "string":
            default:
                return textContent;
        }
    }
}