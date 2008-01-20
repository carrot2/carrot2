
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.yahooapi;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import org.carrot2.core.ProcessingException;
import org.carrot2.util.StringUtils;

/**
 * Content handler for Yahoo search results.
 * 
 * @author Dawid Weiss
 */
public final class YahooResponseHandler implements ContentHandler {
    public long totalResults;
    public int firstResultPosition;
    public int resultsReturned;

    public ArrayList stack = new ArrayList();

    private String title;
    private String summary;
    private String url;
    private String clickurl;
    private String newsSource;
    private String newsSourceUrl;
    
    private StringBuffer buffer = new StringBuffer();

    /** An error occurred. */
    private boolean error;
    private StringBuffer errorText;

    /** A consumer for results */
    private final YahooSearchResultConsumer consumer;

    public YahooResponseHandler(YahooSearchResultConsumer consumer) {
        this.error = false;
        this.consumer = consumer;
    }

    public void startDocument() throws SAXException {
        // Perform cleanup.
        this.totalResults = 0;
        this.firstResultPosition = 0;
        this.resultsReturned = 0;
        cleanup();
    }

    private void cleanup() {
        this.buffer.setLength(0);
        this.title = null;
        this.summary = null;
        this.clickurl = null;
        this.url = null;
        this.newsSource = null;
        this.newsSourceUrl = null;
    }

    public void endDocument() throws SAXException {
    }

    public void startElement(String uri, String localName, String qname, Attributes attributes) throws SAXException {
        buffer.setLength(0);
        if (stack.size() == 0 && "ResultSet".equals(localName)) {
            String tmp = attributes.getValue("totalResultsAvailable");
            if (tmp != null) {
                this.totalResults = Long.parseLong(tmp);
            }
            tmp = attributes.getValue("totalResultsReturned");
            if (tmp != null) {
                this.resultsReturned = Integer.parseInt(tmp);
            }
            tmp = attributes.getValue("firstResultPosition");
            if (tmp != null) {
                this.firstResultPosition = Integer.parseInt(tmp);
            }
        } else if (stack.size() == 0 && "Error".equals(localName)) {
            this.error = true;
            errorText = new StringBuffer();
        } else if (stack.size() > 1 && error) {
            errorText.append(localName + ": ");
        }
        stack.add(localName);
    }

    public void endElement(String uri, String localName, String qname) throws SAXException {
        if (error) {
            errorText.append(buffer);
            errorText.append("\n");
        }
        if (stack.size() == 2 && "Result".equals(localName)) {
            // New result parsed. Push it.
            try {
                if (newsSource == null) {
                    consumer.add(new YahooSearchResult(url, title, summary, clickurl));
                } else {
                    consumer.add(new YahooSearchResult(url, title, summary, clickurl, newsSource, newsSourceUrl));
                }
            } catch (ProcessingException e) {
                throw new SAXException("Could not add result to consumer.", e);
            }
            cleanup();
        }
        if (stack.size() == 3 && "Result".equals(stack.get(1))) {
            String text = buffer.toString();
            buffer.setLength(0);

            // WORKAROUND: Yahoo returns double-encoded entities.
            text = StringUtils.unescapeHtml(text);

            if ("Title".equals(localName)) {
                this.title = text;
            } else if ("Summary".equals(localName)) {
                this.summary = text;
            } else if ("Url".equals(localName)) {
                this.url = text;
            } else if ("ClickUrl".equals(localName)) {
                this.clickurl = text;
            } else if ("NewsSource".equals(localName)) {
                this.newsSource = text;
            } else if ("NewsSourceUrl".equals(localName)) {
                this.newsSourceUrl = text;
            }
        }
        stack.remove(stack.size()-1);
    }

    public void characters(char[] chars, int start, int length) throws SAXException {
        buffer.append(chars, start, length);
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void startPrefixMapping(String arg0, String arg1) throws SAXException {
    }

    public void endPrefixMapping(String arg0) throws SAXException {
    }

    public void ignorableWhitespace(char[] whsp, int start, int length) throws SAXException {
    }

    public void processingInstruction(String name, String value) throws SAXException {
    }

    public void skippedEntity(String entity) throws SAXException {
    }

    public boolean isErraneous() {
        return error;
    }

    public String getErrorText() {
        return errorText.toString();
    }
}
