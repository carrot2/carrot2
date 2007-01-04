
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.yahooapi;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.carrot2.util.StreamUtils;
import org.carrot2.util.StringUtils;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;

/**
 * A regexp-based reader for parsing Yahoo results.
 * 
 * @author Dawid Weiss
 */
public class RegExpYahooXMLReader implements XMLReader {
    private ContentHandler contentHandler;

    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    public void parse(String systemId) throws IOException, SAXException {
        throw new IOException("Parsing through SystemID path is not supported.");
    }

    public void parse(InputSource stream) throws IOException, SAXException {
        contentHandler.startDocument();

        Reader reader = stream.getCharacterStream();
        if (reader == null) {
            reader = new InputStreamReader(stream.getByteStream(), "UTF-8");
        }
        
        final char [] chars = StreamUtils.readFully(reader);
        final StringBuffer input = new StringBuffer(chars.length);
        input.append(chars);

        int pos;
        if ((pos = input.indexOf("<Error")) >= 0) {
            contentHandler.startElement(null, "Error", "Error", new AttributesImpl());
            pos = input.indexOf("<Message", pos);
            final int mStart = input.indexOf(">", pos) + 1;
            pos = input.indexOf("</Message", mStart);
            if (mStart >= 0 && pos >= 0) {
                contentHandler.startElement(null, "Message", "Message", new AttributesImpl());
                contentHandler.characters(chars, mStart, pos - mStart);
                contentHandler.endElement(null, "Message", "Message");
            }
            contentHandler.endElement(null, "Error", "Error");
        } else {
            pos = input.indexOf("<ResultSet");
            if (pos >= 0) {
                Pattern attPattern = Pattern.compile("([a-zA-Z]+)(?:=\")([0-9]+)");
                Matcher matcher = attPattern.matcher(input.subSequence(pos, input.indexOf(">", pos)));
                final AttributesImpl attrs = new AttributesImpl();
                while (matcher.find()) {
                    final String name = matcher.group(1);
                    final String value = matcher.group(2);
                    attrs.addAttribute(null, name, name, "", value);
                }
                
                contentHandler.startElement(null, "ResultSet", "ResultSet", attrs);
                
                // parse results
                final Pattern resPattern = Pattern.compile("(<Result>)(.+?)(</Result>)", Pattern.DOTALL);
                final Pattern inPattern = Pattern.compile("(<)([a-zA-Z]+)(>)(.+?)(</)", Pattern.DOTALL);
                matcher = resPattern.matcher(input);
                while (matcher.find()) {
                    contentHandler.startElement(null, "Result", "Result", new AttributesImpl());
                    final String result = matcher.group(2);
                    final Matcher m2 = inPattern.matcher(result);
                    while (m2.find()) {
                        final String name = m2.group(2);
                        String value = m2.group(4);
                        value = StringUtils.unescapeXml(value);

                        contentHandler.startElement(null, name, name, new AttributesImpl());
                        contentHandler.characters(value.toCharArray(), 0, value.length());
                        contentHandler.endElement(null, name, name);
                    }
                    contentHandler.endElement(null, "Result", "Result");
                }

                contentHandler.endElement(null, "ResultSet", "ResultSet");
            }
        }

        contentHandler.endDocument();
    }

    public EntityResolver getEntityResolver() {
        return null;
    }

    public DTDHandler getDTDHandler() {
        return null;
    }

    public ErrorHandler getErrorHandler() {
        return null;
    }

    public void setContentHandler(ContentHandler handler) {
        this.contentHandler = handler;
    }

    public void setDTDHandler(DTDHandler handler) {
        // ignore.
    }

    public void setEntityResolver(EntityResolver resolver) {
        // ignore.
    }

    public void setErrorHandler(ErrorHandler handler) {
        // ignore.
    }

    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        // ignore.
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        // ignore.
    }
    
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return false;
    }

    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return null;
    }
}
