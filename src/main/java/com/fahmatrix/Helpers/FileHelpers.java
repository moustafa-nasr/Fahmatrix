package com.fahmatrix.Helpers;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class FileHelpers {

    /**
     * Utility method to create secure DocumentBuilderFactory to prevent XXE attacks
     * <br>
     * 
     * @return
     * @throws ParserConfigurationException for XML document builder configuration
     *                                      issues
     */
    public static DocumentBuilderFactory createSecureDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Prevent XXE attacks
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setExpandEntityReferences(false);
        factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);

        return factory;
    }

    /**
     * 
     * @return
     * @throws TransformerException
     */
    public static TransformerFactory createSecureTransformerFactory() throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();

        // Enable secure processing
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        // Disable access to external DTDs and stylesheets
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        return factory;
    }

    /**
     * Utility method used to parse xml files, used in all parsing proccess as xlsx
     * and ODS files are achives containing data in xml formats
     * <br>
     * 
     * @param stream Input stream for xml file inside archive. Used to get
     *               spreadsheets (excel/ODS)
     *               file configurations and style
     * @return parsed xml document
     * @throws ParserConfigurationException for XML document builder configuration
     *                                      issues
     * @throws SAXException                 for XML parsing errors
     * @throws IOException                  for file and stream operations
     */
    public static Document parseXml(InputStream stream) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = FileHelpers.createSecureDocumentBuilderFactory().newDocumentBuilder();
        return builder.parse(stream);
    }
}
