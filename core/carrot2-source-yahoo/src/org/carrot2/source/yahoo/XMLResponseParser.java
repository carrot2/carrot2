
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.yahoo;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang.StringEscapeUtils;
import org.carrot2.core.Document;
import org.carrot2.source.SearchEngineResponse;
import org.xml.sax.*;

import com.google.common.collect.Lists;

/**
 * XML content handler for parsing Yahoo! search response.
 */
final class XMLResponseParser implements ContentHandler
{
    /** Search response object. */
    public SearchEngineResponse response;

    /** Currently parsed document. */
    private Document document;

    /** Parsed element stack. */
    private final ArrayList<String> stack = new ArrayList<String>();

    /** String builder for assembling content. */
    private final StringBuilder buffer = new StringBuilder();

    /** An error occurred. */
    private boolean error;

    /** */
    private StringBuilder errorText;

    /*
     *
     */
    public void startDocument() throws SAXException
    {
        this.error = false;
        this.response = null;
        this.stack.clear();

        cleanup();
    }

    /*
     *
     */
    private void cleanup()
    {
        this.document = null;
        this.buffer.setLength(0);
    }

    /*
     *
     */
    public void endDocument() throws SAXException
    {
        if (error)
        {
            throw new SAXException(new IOException("Yahoo! service error: "
                + errorText.toString()));
        }
    }

    /*
     *
     */
    public void startElement(String uri, String localName, String qname,
        Attributes attributes) throws SAXException
    {
        buffer.setLength(0);
        if (stack.size() == 0 && "ResultSet".equals(localName))
        {
            response = new SearchEngineResponse();

            addResponseMetadataLong(attributes, "firstResultPosition", response,
                YahooSearchService.FIRST_INDEX_KEY);

            addResponseMetadataLong(attributes, "totalResultsAvailable", response,
                SearchEngineResponse.RESULTS_TOTAL_KEY);

            addResponseMetadataLong(attributes, "totalResultsReturned", response,
                YahooSearchService.RESULTS_RETURNED_KEY);
        }
        else if (stack.size() == 0 && "Error".equals(localName))
        {
            this.error = true;
            errorText = new StringBuilder();
        }
        else if (stack.size() > 1 && error)
        {
            errorText.append(localName + ": ");
        }
        else if ("Result".equals(localName))
        {
            this.document = new Document();
        }

        stack.add(localName);
    }

    /**
     * Adds a meta data entry to the response if it exists in the set of attributes.
     */
    private static void addResponseMetadataLong(Attributes attributes,
        String attributeName, SearchEngineResponse response, String metadataKey)
    {
        final String value = attributes.getValue(attributeName);
        if (value != null && !"".equals(value.trim()))
        {
            response.metadata.put(metadataKey, Long.parseLong(value));
        }
    }

    /*
     *
     */
    public void endElement(String uri, String localName, String qname)
        throws SAXException
    {
        if (error)
        {
            errorText.append(buffer);
            errorText.append("\n");
        }
        else if (stack.size() == 2 && "Result".equals(localName))
        {
            // New result parsed. Push it.
            this.response.results.add(document);

            // Cleanup for the next element.
            cleanup();
        }
        else if (stack.size() == 3 && "Result".equals(stack.get(1)))
        {
            // Yahoo! returns double-encoded entities in its response (it
            // return escaped HTML), so reparse it.
            final String text = StringEscapeUtils.unescapeHtml(buffer.toString());
            buffer.setLength(0);

            // Recognize special fields and translate them.
            if ("Title".equals(localName))
            {
                document.setField(Document.TITLE, text);
            }
            else if ("Summary".equals(localName))
            {
                document.setField(Document.SUMMARY, text);
            }
            else if ("Url".equals(localName))
            {
                document.setField(Document.CONTENT_URL, text);
            }
            else if ("NewsSource".equals(localName))
            {
                document.setField(Document.SOURCES, Lists.newArrayList(text));
            }
            else if (!"Thumbnail".equals(localName))
            {
                // All other fields go directly in the document.
                document.setField(localName, text);
            }
        }
        else if (stack.size() == 4 && "Thumbnail".equals(stack.get(2)))
        {
            final String text = buffer.toString();
            buffer.setLength(0);

            if ("Url".equals(localName)) 
            {
                document.setField(Document.THUMBNAIL_URL, text);
            }
        }

        stack.remove(stack.size() - 1);
    }

    /*
     *
     */
    public void characters(char [] chars, int start, int length) throws SAXException
    {
        buffer.append(chars, start, length);
    }

    /*
     *
     */
    public void setDocumentLocator(Locator locator)
    {
        // Empty.
    }

    /*
     *
     */
    public void startPrefixMapping(String arg0, String arg1) throws SAXException
    {
        // Empty.
    }

    /*
     *
     */
    public void endPrefixMapping(String arg0) throws SAXException
    {
        // Empty.
    }

    /*
     *
     */
    public void ignorableWhitespace(char [] whsp, int start, int length)
        throws SAXException
    {
    }

    /*
     *
     */
    public void processingInstruction(String name, String value) throws SAXException
    {
    }

    /*
     *
     */
    public void skippedEntity(String entity) throws SAXException
    {
    }
}
