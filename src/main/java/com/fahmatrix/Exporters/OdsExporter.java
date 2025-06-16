package com.fahmatrix.Exporters;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fahmatrix.Helpers.FileHelpers;

public class OdsExporter {
    private String filePath;
    private Map<String, List<Object>> columns;

    /**
     * Constructs a new OdsExporter instance with the specified file name.
     * <br>
     * 
     * @param filePath the file name to export
     */
    public OdsExporter(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns the file name associated with this OdsExporter instance.
     *
     * @return the file name
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the file name for this CsvExporter instance.
     *
     * @param fileName the new file name
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Save data to ODS file
     * <br>
     *
     * @param columns Map of column names to values
     * @throws IOException if failed to write to file
     */
    public void saveODS(Map<String, List<Object>> columns) throws Exception {
        this.columns = columns.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new ArrayList<>(entry.getValue())));

        try (FileOutputStream fos = new FileOutputStream(filePath);
                ZipOutputStream zos = new ZipOutputStream(fos)) {

            // Write all required ODS files to ZIP
            writeMimeType(zos);
            writeManifest(zos);
            writeMeta(zos);
            writeSettings(zos);
            writeStyles(zos);
            writeContent(zos);
        }
    }

    /**
     * Writes the MIME type of the document to the specified ZipOutputStream.
     * This is a required file for ODS documents, which indicates that it's an
     * OpenDocument Spreadsheet (ods) file.
     * <br>
     * 
     * @param zos the ZipOutputStream to write to
     * @throws IOException if an I/O error occurs while writing
     */
    private void writeMimeType(ZipOutputStream zos) throws IOException {
        // First entry must be mimetype (uncompressed)
        ZipEntry entry = new ZipEntry("mimetype");
        entry.setMethod(ZipEntry.STORED);
        String mimeType = "application/vnd.oasis.opendocument.spreadsheet";
        entry.setSize(mimeType.length());
        entry.setCrc(calculateCRC32(mimeType.getBytes(StandardCharsets.UTF_8)));

        zos.putNextEntry(entry);
        zos.write(mimeType.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    /**
     * Calculates the CRC-32 checksum of a given byte array.
     * <br>
     * This method uses the {@link java.util.zip.CRC32} class to calculate
     * the checksum. The resulting value is then returned as an integer.
     * <br>
     * 
     * @param data the input data for which the checksum should be calculated
     * @return the CRC-32 checksum of the input data
     */
    private long calculateCRC32(byte[] data) {
        java.util.zip.CRC32 crc = new java.util.zip.CRC32();
        crc.update(data);
        return crc.getValue();
    }

    /**
     * This method is responsible for writing the manifest file, which is an XML
     * file that describes the structure and contents of the ODS document.
     * <br>
     * The manifest file contains metadata about the ODS document, including
     * a list of all files that make up the document.
     * <br>
     *
     * @param zos the ZipOutputStream to write to
     * @throws IOException if an I/O error occurs while writing
     */
    private void writeManifest(ZipOutputStream zos) throws IOException {
        zos.putNextEntry(new ZipEntry("META-INF/manifest.xml"));

        String manifest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<manifest:manifest xmlns:manifest=\"urn:oasis:names:tc:opendocument:xmlns:manifest:1.0\" manifest:version=\"1.2\">\n"
                +
                "  <manifest:file-entry manifest:full-path=\"/\" manifest:version=\"1.2\" manifest:media-type=\"application/vnd.oasis.opendocument.spreadsheet\"/>\n"
                +
                "  <manifest:file-entry manifest:full-path=\"content.xml\" manifest:media-type=\"text/xml\"/>\n" +
                "  <manifest:file-entry manifest:full-path=\"styles.xml\" manifest:media-type=\"text/xml\"/>\n" +
                "  <manifest:file-entry manifest:full-path=\"meta.xml\" manifest:media-type=\"text/xml\"/>\n" +
                "  <manifest:file-entry manifest:full-path=\"settings.xml\" manifest:media-type=\"text/xml\"/>\n" +
                "</manifest:manifest>";

        zos.write(manifest.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    /**
     * Writes the meta file, which contains metadata about the ODS document.
     * <br>
     *
     * @param zos the ZipOutputStream to write to
     * @throws IOException if an I/O error occurs while writing to the ODS archive
     */
    private void writeMeta(ZipOutputStream zos) throws IOException {
        zos.putNextEntry(new ZipEntry("meta.xml"));

        String meta = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<office:document-meta xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\" " +
                "xmlns:meta=\"urn:oasis:names:tc:opendocument:xmlns:meta:1.0\" " +
                "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" office:version=\"1.2\">\n" +
                "  <office:meta>\n" +
                "    <meta:generator>Fahmatrix Library</meta:generator>\n" +
                "    <dc:title>Fahmatrix Export</dc:title>\n" +
                "  </office:meta>\n" +
                "</office:document-meta>";

        zos.write(meta.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    /**
     * Writes the settings file, which contains configuration data for the ODS
     * document.
     * <br>
     * This method is responsible for writing the content of the "settings.xml"
     * file,
     * which contains metadata about the ODS document, including configuration
     * settings
     * and other relevant information.
     * <br>
     *
     * @param zos the ZipOutputStream to write to
     * @throws IOException if an I/O error occurs while writing
     */
    private void writeSettings(ZipOutputStream zos) throws IOException {
        zos.putNextEntry(new ZipEntry("settings.xml"));

        String settings = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<office:document-settings xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\" " +
                "xmlns:config=\"urn:oasis:names:tc:opendocument:xmlns:config:1.0\" office:version=\"1.2\">\n" +
                "  <office:settings>\n" +
                "    <config:config-item-set config:name=\"ooo:view-settings\">\n" +
                "      <config:config-item config:name=\"VisibleAreaTop\" config:type=\"int\">0</config:config-item>\n"
                +
                "      <config:config-item config:name=\"VisibleAreaLeft\" config:type=\"int\">0</config:config-item>\n"
                +
                "    </config:config-item-set>\n" +
                "  </office:settings>\n" +
                "</office:document-settings>";

        zos.write(settings.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    /**
     * Writes the styles file, which contains formatting information for the ODS
     * document.
     * This method is responsible for writing the content of the "styles.xml" file,
     * which contains metadata about the ODS document, including formatting settings
     * and other relevant information.
     * <br>
     *
     * @param zos the ZipOutputStream to write to
     * @throws IOException if an I/O error occurs while writing
     */
    private void writeStyles(ZipOutputStream zos) throws IOException {
        zos.putNextEntry(new ZipEntry("styles.xml"));

        String styles = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<office:document-styles xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\" " +
                "xmlns:style=\"urn:oasis:names:tc:opendocument:xmlns:style:1.0\" " +
                "xmlns:fo=\"urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0\" office:version=\"1.2\">\n" +
                "  <office:font-face-decls>\n" +
                "    <style:font-face style:name=\"Arial\" svg:font-family=\"Arial\" style:font-family-generic=\"swiss\"/>\n"
                +
                "  </office:font-face-decls>\n" +
                "  <office:styles>\n" +
                "    <style:default-style style:family=\"table-cell\">\n" +
                "      <style:paragraph-properties style:tab-stop-distance=\"1.25cm\"/>\n" +
                "      <style:text-properties style:font-name=\"Arial\" fo:font-size=\"10pt\"/>\n" +
                "    </style:default-style>\n" +
                "  </office:styles>\n" +
                "  <office:automatic-styles>\n" +
                "    <style:page-layout style:name=\"pm1\">\n" +
                "      <style:page-layout-properties style:writing-mode=\"lr-tb\"/>\n" +
                "    </style:page-layout>\n" +
                "  </office:automatic-styles>\n" +
                "  <office:master-styles>\n" +
                "    <style:master-page style:name=\"Default\" style:page-layout-name=\"pm1\"/>\n" +
                "  </office:master-styles>\n" +
                "</office:document-styles>";

        zos.write(styles.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    /**
     * Writes the content of the ODS document.
     * This method is responsible for writing the content of the "content.xml" file,
     * which contains the actual data of the ODS document.
     * <br>
     *
     * @param zos the ZipOutputStream to write to
     * @throws IOException                  if an I/O error occurs while writing
     * @throws ParserConfigurationException if a parser configuration exception
     *                                      occurs
     * @throws TransformerException         if a transformation exception occurs
     */
    private void writeContent(ZipOutputStream zos)
            throws IOException, ParserConfigurationException, TransformerException {
        zos.putNextEntry(new ZipEntry("content.xml"));

        Document doc = FileHelpers.createSecureDocumentBuilderFactory().newDocumentBuilder().newDocument();

        // Set the XML declaration in the document
        Element documentContent = doc.createElement("office:document-content");
        documentContent.setAttribute("xmlns:office", "urn:oasis:names:tc:opendocument:xmlns:office:1.0");
        documentContent.setAttribute("xmlns:table", "urn:oasis:names:tc:opendocument:xmlns:table:1.0");
        documentContent.setAttribute("xmlns:text", "urn:oasis:names:tc:opendocument:xmlns:text:1.0");
        documentContent.setAttribute("office:version", "1.2");
        doc.appendChild(documentContent);

        // Automatic styles
        Element automaticStyles = doc.createElement("office:automatic-styles");
        documentContent.appendChild(automaticStyles);

        // Body
        Element body = doc.createElement("office:body");
        documentContent.appendChild(body);

        Element spreadsheet = doc.createElement("office:spreadsheet");
        body.appendChild(spreadsheet);

        // Table (Sheet)
        Element table = doc.createElement("table:table");
        table.setAttribute("table:name", "Sheet1");
        spreadsheet.appendChild(table);

        List<String> columnNames = new ArrayList<>(columns.keySet());
        int maxRows = columns.values().stream().mapToInt(List::size).max().orElse(0);

        // Header row
        Element headerRow = doc.createElement("table:table-row");
        table.appendChild(headerRow);

        for (String columnName : columnNames) {
            Element cell = doc.createElement("table:table-cell");
            cell.setAttribute("office:value-type", "string");
            headerRow.appendChild(cell);

            Element paragraph = doc.createElement("text:p");
            paragraph.setTextContent(columnName);
            cell.appendChild(paragraph);
        }

        // Data rows
        for (int row = 0; row < maxRows; row++) {
            Element dataRow = doc.createElement("table:table-row");
            table.appendChild(dataRow);

            for (String columnName : columnNames) {
                List<Object> columnData = columns.get(columnName);
                Element cell = doc.createElement("table:table-cell");

                if (row < columnData.size() && columnData.get(row) != null) {
                    Object cellValue = columnData.get(row);

                    if (cellValue instanceof Number) {
                        cell.setAttribute("office:value-type", "float");
                        cell.setAttribute("office:value", cellValue.toString());
                    } else {
                        cell.setAttribute("office:value-type", "string");
                    }

                    Element paragraph = doc.createElement("text:p");
                    paragraph.setTextContent(cellValue.toString());
                    cell.appendChild(paragraph);
                } else {
                    // Empty cell
                    cell.setAttribute("office:value-type", "string");
                    Element paragraph = doc.createElement("text:p");
                    cell.appendChild(paragraph);
                }

                dataRow.appendChild(cell);
            }
        }

        writeDocumentToZip(doc, zos);
        zos.closeEntry();
    }

    /**
     * Writes the content of a Document object to a ZipOutputStream.
     * <br>
     * 
     * @param doc the Document object to write
     * @param zos the ZipOutputStream to write to
     * @throws TransformerException if a transformation exception occurs
     */
    private void writeDocumentToZip(Document doc, ZipOutputStream zos) throws TransformerException {
        TransformerFactory transformerFactory = FileHelpers.createSecureTransformerFactory();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(zos);
        transformer.transform(source, result);
    }
}