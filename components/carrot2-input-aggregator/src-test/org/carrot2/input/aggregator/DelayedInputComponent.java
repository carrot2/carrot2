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

package org.carrot2.input.aggregator;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.*;

/**
 * An input component that allows to simulate a delay before the documents are
 * pushed down the processing chain.
 * 
 * @author Stanislaw Osinski
 */
public class DelayedInputComponent extends LocalInputComponentBase implements
    RawDocumentsProducer
{
    /** The number of miliseconds to wait before pushing documents */
    private int delay;

    /** The content base of the generated document */
    private String contentBase;

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = new HashSet(Arrays
        .asList(new Object []
        {
            RawDocumentsConsumer.class
        }));

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = new HashSet(Arrays
        .asList(new Object []
        {
            RawDocumentsProducer.class
        }));

    /** Current RawDocumentsConsumer to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /**
     * Creates an instance of the component.
     * 
     * @param delay the number of miliseconds to wait before pushing documents
     */
    public DelayedInputComponent(int delay, String contentBase)
    {
        this.delay = delay;
        this.contentBase = contentBase;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalInputComponent#setQuery(java.lang.String)
     */
    public void setQuery(String query)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return COMPONENT_CAPABILITIES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return SUCCESSOR_CAPABILITIES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        rawDocumentConsumer = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalInputComponent#setNext(org.carrot2.core.LocalComponent)
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        if (next instanceof RawDocumentsConsumer)
        {
            rawDocumentConsumer = (RawDocumentsConsumer) next;
        }
        else
        {
            rawDocumentConsumer = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#startProcessing(org.carrot2.core.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        // Initialize the subsequent components
        super.startProcessing(requestContext);

        try
        {
            Thread.sleep(delay);
        }
        catch (InterruptedException e)
        {
            throw new ProcessingException(e);
        }
        
        // Push one document
        for (int i = 0; i < 1; i++)
        {
            rawDocumentConsumer.addDocument(new RawDocumentSnippet(contentBase
                + "-" + i, contentBase + "-" + i + "-title", contentBase + "-"
                + i + "-snippet", contentBase + "-" + i + "-url", 0.0f));
        }
    }
}
