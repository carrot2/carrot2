
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

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