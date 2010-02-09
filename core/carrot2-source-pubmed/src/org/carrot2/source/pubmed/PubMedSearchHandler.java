
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

package org.carrot2.source.pubmed;

import java.util.List;

import org.xml.sax.*;

import com.google.common.collect.Lists;

/**
 * A SAX content handler that collects PubMed IDs.
 */
class PubMedSearchHandler implements ContentHandler
{
    /** Collects IDs of PubMed entries to retrieve */
    private List<String> pubMedPrimaryIds;

    /** For locating/storing IDs */
    private StringBuffer id;
    private boolean handlingId;

    public List<String> getPubMedPrimaryIds()
    {
        return pubMedPrimaryIds;
    }

    public void startDocument() throws SAXException
    {
        pubMedPrimaryIds = Lists.newArrayListWithExpectedSize(100);
        handlingId = false;
        id = new StringBuffer();
    }

    public void startElement(String namespaceURI, String localName, String qName,
        Attributes atts) throws SAXException
    {
        handlingId = "Id".equals(localName);
        id.setLength(0);
    }

    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException
    {
        if (handlingId)
        {
            pubMedPrimaryIds.add(id.toString());
            handlingId = false;
        }
    }

    public void characters(char [] ch, int start, int length) throws SAXException
    {
        if (handlingId)
        {
            id.append(ch, start, length);
        }
    }

    public void endDocument() throws SAXException
    {
    }

    public void endPrefixMapping(String prefix) throws SAXException
    {
    }

    public void ignorableWhitespace(char [] ch, int start, int length)
        throws SAXException
    {
    }

    public void processingInstruction(String target, String data) throws SAXException
    {
    }

    public void setDocumentLocator(Locator locator)
    {
    }

    public void skippedEntity(String name) throws SAXException
    {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException
    {
    }
}
