package com.fahmatrix.Importers;

import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SimpleXlsxImporter {

    private Map<String, List<Object>> columns = new LinkedHashMap<>();
    private List<String> index = new ArrayList<>();

    /**
     * Main Read Excel method
     * <br>
     * 
     * @param filePath file path as a string
     * @throws Exception error parsing file or data
     */
    public void readExcel(String filePath) throws Exception {
        columns = readExcelAsColumns(filePath);

        if (index.isEmpty()) {
            for (int i = 0; i < columns.size(); i++) {
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
     * Main reading excel method
     * <br>
     * 
     * @param filePath file path as a string
     * @return Map of columns data
     * @throws Exception error parsing file or data
     */
    private Map<String, List<Object>> readExcelAsColumns(String filePath) throws Exception {
        Map<String, List<Object>> columnData = new LinkedHashMap<>();
        List<String> headers = new ArrayList<>();
        List<String> sharedStrings = new ArrayList<>();

        try (ZipFile zip = new ZipFile(filePath)) {
            // Parse shared strings (text values)
            ZipEntry sharedStringsEntry = zip.getEntry("xl/sharedStrings.xml");
            if (sharedStringsEntry != null) {
                try (InputStream stream = zip.getInputStream(sharedStringsEntry)) {
                    Document sharedStringsDoc = parseXml(stream);
                    NodeList siNodes = sharedStringsDoc.getElementsByTagName("si");
                    for (int i = 0; i < siNodes.getLength(); i++) {
                        sharedStrings.add(siNodes.item(i).getTextContent().trim());
                    }
                }
            }

            // Parse first sheet
            ZipEntry sheetEntry = zip.getEntry("xl/worksheets/sheet1.xml");
            try (InputStream stream = zip.getInputStream(sheetEntry)) {
                Document sheetDoc = parseXml(stream);
                NodeList rowNodes = sheetDoc.getElementsByTagName("row");

                // Initialize column structure
                if (rowNodes.getLength() > 0) {
                    // First row = headers
                    Element firstRow = (Element) rowNodes.item(0);
                    NodeList headerCells = firstRow.getElementsByTagName("c");
                    for (int i = 0; i < headerCells.getLength(); i++) {
                        Element cell = (Element) headerCells.item(i);
                        String header = getCellValue(cell, sharedStrings);
                        headers.add(header != null ? header : "Column" + (i + 1));
                        columnData.put(headers.get(i), new ArrayList<>());
                    }

                    // Process data rows (skip first row)
                    for (int rowIdx = 1; rowIdx < rowNodes.getLength(); rowIdx++) {
                        Element row = (Element) rowNodes.item(rowIdx);
                        NodeList cells = row.getElementsByTagName("c");

                        for (int cellIdx = 0; cellIdx < cells.getLength(); cellIdx++) {
                            if (cellIdx >= headers.size())
                                break; // Ignore extra cells
                            Element cell = (Element) cells.item(cellIdx);
                            Object value = parseCellValue(cell, sharedStrings);
                            columnData.get(headers.get(cellIdx)).add(value);
                        }
                    }
                }
            }
        }
        return columnData;
    }

    /**
     * Helper method used to parse xml files, used in all parsing proccess as xlsx
     * files are achives containing data in xml formats
     * <br>
     * 
     * @param stream Input stream for xml file inside archive. Used to get excel
     *               file configurations and style
     * @return parsed xml document
     * @throws Exception
     */
    private Document parseXml(InputStream stream) throws Exception {
        return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(stream);
    }

    /**
     * Helper method to read the cell data as a string
     * <br>
     * 
     * @param cell          excel cell as xml element
     * @param sharedStrings excel strings file configuration
     * @return cell value as string
     */
    private String getCellValue(Element cell, List<String> sharedStrings) {
        NodeList vNodes = cell.getElementsByTagName("v");
        if (vNodes.getLength() == 0)
            return null;
        String value = vNodes.item(0).getTextContent();

        // Check if cell uses shared strings
        String cellType = cell.getAttribute("t");
        if ("s".equals(cellType)) {
            int index = Integer.parseInt(value);
            return sharedStrings.get(index);
        }
        return value;
    }

    /**
     * Helper method to read/parse the cell data into object
     * <br>
     * 
     * @param cell
     * @param sharedStrings
     * @return
     */
    private Object parseCellValue(Element cell, List<String> sharedStrings) {
        String value = getCellValue(cell, sharedStrings);
        if (value == null)
            return null;

        // Detect numbers (crude check)
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return value;
        }
    }
}
