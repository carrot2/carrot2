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

package org.carrot2.filter.striphtml;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.solr.analysis.HTMLStripReader;
import org.carrot2.core.LocalComponent;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.clustering.*;
import org.carrot2.core.profiling.ProfiledLocalFilterComponentBase;
import org.carrot2.util.StreamUtils;

/**
 * <p>
 * Attempts to remove all HTML code from the input document's titles and snippets.
 * 
 * @author Dawid Weiss
 */
public class StripHTMLLocalFilterComponent extends ProfiledLocalFilterComponentBase implements RawDocumentsConsumer,
    RawDocumentsProducer
{
    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = toSet(RawDocumentsProducer.class);

    /** This component's capabilities */
    private final static Set CAPABILITIES_COMPONENT = toSet(RawDocumentsConsumer.class, RawDocumentsProducer.class);

    /** Capabilities required from the next component in the chain */
    private final static Set CAPABILITIES_SUCCESSOR = toSet(RawDocumentsConsumer.class);

    /**
     * Consumer of processed {@link RawDocument}s.
     */
    private RawDocumentsConsumer consumer;

    /*
     * 
     */
    public void addDocument(final RawDocument doc) throws ProcessingException
    {
        startTimer();

        final RawDocumentBase wrapped = new RawDocumentBase(doc)
        {
            public Object getId()
            {
                return doc.getId();
            }
        };

        try
        {
            wrapped.setProperty(RawDocument.PROPERTY_TITLE, toText((String) doc.getProperty(RawDocument.PROPERTY_TITLE)));
            wrapped.setProperty(RawDocument.PROPERTY_SNIPPET, toText((String) doc.getProperty(RawDocument.PROPERTY_SNIPPET)));
        }
        catch (IOException e)
        {
            throw new ProcessingException(e);
        }

        consumer.addDocument(wrapped);

        stopTimer();
    }

    /*
     * 
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);

        this.consumer = (RawDocumentsConsumer) next;
    }

    /*
     * 
     */
    public void flushResources()
    {
        super.flushResources();

        this.consumer = null;
    }

    /*
     * 
     */
    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    /*
     * 
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return CAPABILITIES_SUCCESSOR;
    }

    /*
     * 
     */
    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    /**
     * Converts HTML to plain text.
     */
    static String toText(String html) throws IOException
    {
        if (html == null)
        {
            return null;
        }

        final HTMLStripReader reader = new HTMLStripReader(new StringReader(html));
        return new String(StreamUtils.readFully(reader));
    }
}