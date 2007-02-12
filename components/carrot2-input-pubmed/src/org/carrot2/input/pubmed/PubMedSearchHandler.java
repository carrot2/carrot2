/*
 * Copyright (c) 2004 Poznan Supercomputing and Networking Center
 * 10 Noskowskiego Street, Poznan, Wielkopolska 61-704, Poland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Poznan Supercomputing and Networking Center ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into
 * with PSNC.
 */

package org.carrot2.input.pubmed;

import java.util.*;

import org.xml.sax.*;


/**
 * A SAX content handler that collects PubMed IDs.
 * 
 * @author Stanislaw Osinski
 */
public class PubMedSearchHandler
    implements ContentHandler
{
    /** Collects IDs of PubMed entries to retrieve */
    private List pubMedPrimaryIds;

    /** For locating/storing IDs */
    private StringBuffer id;
    private boolean handlingId;

    public List getPubMedPrimaryIds()
    {
        return pubMedPrimaryIds;
    }

    public void startDocument()
        throws SAXException
    {
        pubMedPrimaryIds = new ArrayList(100);
        handlingId = false;
        id = new StringBuffer();
    }


    public void startElement(String namespaceURI, String localName,
            String qName, Attributes atts)
        throws SAXException
    {
        handlingId = "Id".equals(localName);
        id.setLength(0);
    }


    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException
    {
        if (handlingId) {
            pubMedPrimaryIds.add(id.toString());
            handlingId = false;
        }
    }


    public void characters(char[] ch, int start, int length)
        throws SAXException
    {
        if (handlingId) {
            id.append(ch, start, length);
        }
    }


    public void endDocument()
        throws SAXException
    {}


    public void endPrefixMapping(String prefix)
        throws SAXException
    {}


    public void ignorableWhitespace(char[] ch, int start, int length)
        throws SAXException
    {}


    public void processingInstruction(String target, String data)
        throws SAXException
    {}


    public void setDocumentLocator(Locator locator)
    {}


    public void skippedEntity(String name)
        throws SAXException
    {}


    public void startPrefixMapping(String prefix, String uri)
        throws SAXException
    {}

}
