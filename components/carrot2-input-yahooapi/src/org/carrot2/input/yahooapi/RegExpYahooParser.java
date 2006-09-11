package org.carrot2.input.yahooapi;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.xml.sax.*;

/**
 * A pseudo-parser for parsing pseudo-XMLs from Yahoo. 
 * 
 * @author Dawid Weiss
 */
final class RegExpYahooParser extends SAXParser {

    public RegExpYahooParser() throws ParserConfigurationException {
        // do nothing.
    }
    
    public Parser getParser() throws SAXException {
        return null;
    }

    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotSupportedException("Unsupported property: " + name);
    }

    public XMLReader getXMLReader() throws SAXException {
        return new RegExpYahooXMLReader();
    }

    public boolean isNamespaceAware() {
        return false;
    }

    public boolean isValidating() {
        return false;
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        // ignore properties.
    }
}