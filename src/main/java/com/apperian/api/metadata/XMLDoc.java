package com.apperian.api.metadata;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

class XMLDoc {
    private final Document doc;

    public static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();

    public static final XPathFactory X_PATH_FACTORY = XPathFactory.newInstance();

    public XMLDoc(InputStream in) throws Exception {
        doc = FACTORY.newDocumentBuilder().parse(in);
    }

    public String extractValue(String xPath) throws XPathExpressionException {
        return X_PATH_FACTORY.newXPath().compile(xPath).evaluate(doc).trim();
    }
}
