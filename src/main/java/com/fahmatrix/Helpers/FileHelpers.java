package com.fahmatrix.Helpers;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class FileHelpers {

    /**
     * Utility method to create secure DocumentBuilderFactory to prevent XXE attacks
     * <br>
     * 
     * @return
     * @throws Exception
     */
    public static DocumentBuilderFactory createSecureDocumentBuilderFactory() throws Exception {
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
     * Utility method used to parse xml files, used in all parsing proccess as xlsx
     * and ODS files are achives containing data in xml formats
     * <br>
     * 
     * @param stream Input stream for xml file inside archive. Used to get
     *               spreadsheets (excel/ODS)
     *               file configurations and style
     * @return parsed xml document
     * @throws Exception
     */
    public static Document parseXml(InputStream stream) throws Exception {
        DocumentBuilder builder = FileHelpers.createSecureDocumentBuilderFactory().newDocumentBuilder();
        return builder.parse(stream);
    }
}
