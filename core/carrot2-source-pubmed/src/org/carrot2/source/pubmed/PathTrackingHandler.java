
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.pubmed;

import java.util.HashMap;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.carrotsearch.hppc.IntStack;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * A simple SAX tracking handler that stores the current element's path.
 */
class PathTrackingHandler implements ContentHandler
{
    public abstract class Trigger {
        public void onElement(String localName, String path, Attributes attrs) {}
        public void afterElement(String localName, String path, String text) {}
    }

    /** Collect current element's text. */
    private final StringBuilder text = new StringBuilder();

    /** Current element's hierarchical path (XPath) */
    private final StringBuilder path = new StringBuilder();

    /** Path segments for recursion. */
    private final IntStack pathSegments = new IntStack();

    private final HashMap<String, Trigger> triggers = Maps.newHashMap();

    protected PathTrackingHandler addTrigger(String simplePath, Trigger p) {
        if (triggers.containsKey(simplePath)) {
            throw new IllegalArgumentException("Trigger already bound to path: " + simplePath);
        }
        triggers.put(simplePath, p);
        return this;
    }

    protected PathTrackingHandler addTrigger(List<String> simplePaths, Trigger p) {
        for (String path : simplePaths) {
            addTrigger(path, p);
        }
        return this;
    }

    public void startDocument() throws SAXException
    {
        this.text.setLength(0);
        this.path.setLength(0);
        this.pathSegments.clear();
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException
    {
        pathSegments.push(path.length());
        path.append('/').append(localName);
        text.setLength(0);

        String pathAsString = path.toString();
        if (triggers.containsKey(pathAsString)) {
            triggers.get(pathAsString).onElement(localName, pathAsString, attrs);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException
    {
        String pathAsString = path.toString();
        if (triggers.containsKey(pathAsString)) {
            triggers.get(pathAsString).afterElement(localName, pathAsString, text.toString());
        }

        this.path.setLength(pathSegments.pop());
        text.setLength(0);
    }

    public void characters(char [] ch, int start, int length) throws SAXException
    {
        text.append(ch, start, length);
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
