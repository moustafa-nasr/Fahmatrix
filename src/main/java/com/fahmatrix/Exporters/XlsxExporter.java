package com.fahmatrix.Exporters;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XlsxExporter {
    private String filePath;

    private Map<String, List<Object>> columns;
    private List<String> sharedStrings;
    private Map<String, Integer> stringIndexMap;

    public XlsxExporter(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void saveXLSX(Map<String, List<Object>> columns) throws Exception {
        this.columns = columns;
        this.sharedStrings = new ArrayList<>();
        this.stringIndexMap = new HashMap<>();

        try (FileOutputStream fos = new FileOutputStream(filePath);
                ZipOutputStream zos = new ZipOutputStream(fos)) {

            // Build shared strings first
            buildSharedStrings();

            // Write all XML files to ZIP
            writeContentTypes(zos);
            writeRels(zos);
            writeWorkbook(zos);
            writeWorkbookRels(zos);
            writeSharedStrings(zos);
            writeStyles(zos);
            writeSheet(zos);
        }
    }

    private void buildSharedStrings() {
        Set<String> uniqueStrings = new HashSet<>();

        // Add column headers
        for (String columnName : columns.keySet()) {
            uniqueStrings.add(columnName);
        }

        // Add all string values from data
        for (List<Object> columnData : columns.values()) {
            for (Object value : columnData) {
                if (value != null && !(value instanceof Number)) {
                    uniqueStrings.add(value.toString());
                }
            }
        }

        // Build index map
        int index = 0;
        for (String str : uniqueStrings) {
            sharedStrings.add(str);
            stringIndexMap.put(str, index++);
        }
    }

    private void writeContentTypes(ZipOutputStream zos) throws Exception {
        zos.putNextEntry(new ZipEntry("[Content_Types].xml"));

        String contentTypes = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n" +
                "  <Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n"
                +
                "  <Default Extension=\"xml\" ContentType=\"application/xml\"/>\n" +
                "  <Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>\n"
                +
                "  <Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n"
                +
                "  <Override PartName=\"/xl/sharedStrings.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml\"/>\n"
                +
                "  <Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>\n"
                +
                "</Types>";

        zos.write(contentTypes.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    private void writeRels(ZipOutputStream zos) throws Exception {
        zos.putNextEntry(new ZipEntry("_rels/.rels"));

        String rels = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
                "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>\n"
                +
                "</Relationships>";

        zos.write(rels.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    private void writeWorkbook(ZipOutputStream zos) throws Exception {
        zos.putNextEntry(new ZipEntry("xl/workbook.xml"));

        String workbook = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">\n"
                +
                "  <sheets>\n" +
                "    <sheet name=\"Sheet1\" sheetId=\"1\" r:id=\"rId1\"/>\n" +
                "  </sheets>\n" +
                "</workbook>";

        zos.write(workbook.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    private void writeWorkbookRels(ZipOutputStream zos) throws Exception {
        zos.putNextEntry(new ZipEntry("xl/_rels/workbook.xml.rels"));

        String workbookRels = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
                "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>\n"
                +
                "  <Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/sharedStrings\" Target=\"sharedStrings.xml\"/>\n"
                +
                "  <Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>\n"
                +
                "</Relationships>";

        zos.write(workbookRels.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    private void writeSharedStrings(ZipOutputStream zos) throws Exception {
        zos.putNextEntry(new ZipEntry("xl/sharedStrings.xml"));

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();

        Element sst = doc.createElement("sst");
        sst.setAttribute("xmlns", "http://schemas.openxmlformats.org/spreadsheetml/2006/main");
        sst.setAttribute("count", String.valueOf(sharedStrings.size()));
        sst.setAttribute("uniqueCount", String.valueOf(sharedStrings.size()));
        doc.appendChild(sst);

        for (String str : sharedStrings) {
            Element si = doc.createElement("si");
            Element t = doc.createElement("t");
            t.setTextContent(str);
            si.appendChild(t);
            sst.appendChild(si);
        }

        writeDocumentToZip(doc, zos);
        zos.closeEntry();
    }

    private void writeStyles(ZipOutputStream zos) throws Exception {
        zos.putNextEntry(new ZipEntry("xl/styles.xml"));

        String styles = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n" +
                "  <numFmts count=\"0\"/>\n" +
                "  <fonts count=\"1\">\n" +
                "    <font>\n" +
                "      <sz val=\"11\"/>\n" +
                "      <name val=\"Calibri\"/>\n" +
                "    </font>\n" +
                "  </fonts>\n" +
                "  <fills count=\"1\">\n" +
                "    <fill>\n" +
                "      <patternFill patternType=\"none\"/>\n" +
                "    </fill>\n" +
                "  </fills>\n" +
                "  <borders count=\"1\">\n" +
                "    <border>\n" +
                "      <left/>\n" +
                "      <right/>\n" +
                "      <top/>\n" +
                "      <bottom/>\n" +
                "      <diagonal/>\n" +
                "    </border>\n" +
                "  </borders>\n" +
                "  <cellStyleXfs count=\"1\">\n" +
                "    <xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/>\n" +
                "  </cellStyleXfs>\n" +
                "  <cellXfs count=\"1\">\n" +
                "    <xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>\n" +
                "  </cellXfs>\n" +
                "</styleSheet>";

        zos.write(styles.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    private void writeSheet(ZipOutputStream zos) throws Exception {
        zos.putNextEntry(new ZipEntry("xl/worksheets/sheet1.xml"));

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();

        Element worksheet = doc.createElement("worksheet");
        worksheet.setAttribute("xmlns", "http://schemas.openxmlformats.org/spreadsheetml/2006/main");
        worksheet.setAttribute("xmlns:r", "http://schemas.openxmlformats.org/officeDocument/2006/relationships");
        doc.appendChild(worksheet);

        Element sheetData = doc.createElement("sheetData");
        worksheet.appendChild(sheetData);

        List<String> columnNames = new ArrayList<>(columns.keySet());
        int maxRows = columns.values().stream().mapToInt(List::size).max().orElse(0);

        // Write header row
        Element headerRow = doc.createElement("row");
        headerRow.setAttribute("r", "1");
        sheetData.appendChild(headerRow);

        for (int col = 0; col < columnNames.size(); col++) {
            String cellRef = getCellReference(col, 0);
            Element cell = doc.createElement("c");
            cell.setAttribute("r", cellRef);
            cell.setAttribute("t", "s"); // string type

            Element value = doc.createElement("v");
            value.setTextContent(String.valueOf(stringIndexMap.get(columnNames.get(col))));
            cell.appendChild(value);
            headerRow.appendChild(cell);
        }

        // Write data rows
        for (int row = 0; row < maxRows; row++) {
            Element dataRow = doc.createElement("row");
            dataRow.setAttribute("r", String.valueOf(row + 2));
            sheetData.appendChild(dataRow);

            for (int col = 0; col < columnNames.size(); col++) {
                String columnName = columnNames.get(col);
                List<Object> columnData = columns.get(columnName);

                if (row < columnData.size()) {
                    Object cellValue = columnData.get(row);
                    if (cellValue != null) {
                        String cellRef = getCellReference(col, row + 1);
                        Element cell = doc.createElement("c");
                        cell.setAttribute("r", cellRef);

                        Element value = doc.createElement("v");

                        if (cellValue instanceof Number) {
                            // Numeric value
                            value.setTextContent(cellValue.toString());
                        } else {
                            // String value
                            cell.setAttribute("t", "s");
                            Integer stringIndex = stringIndexMap.get(cellValue.toString());
                            value.setTextContent(String.valueOf(stringIndex != null ? stringIndex : 0));
                        }

                        cell.appendChild(value);
                        dataRow.appendChild(cell);
                    }
                }
            }
        }

        writeDocumentToZip(doc, zos);
        zos.closeEntry();
    }

    private String getCellReference(int col, int row) {
        StringBuilder colRef = new StringBuilder();
        int colNum = col;
        while (colNum >= 0) {
            colRef.insert(0, (char) ('A' + (colNum % 26)));
            colNum = colNum / 26 - 1;
            if (colNum < 0)
                break;
        }
        return colRef.toString() + (row + 1);
    }

    private void writeDocumentToZip(Document doc, ZipOutputStream zos) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(zos);
        transformer.transform(source, result);
    }
}