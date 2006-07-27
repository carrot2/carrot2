
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

package org.carrot2.core.impl;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.profiling.ProfiledLocalFilterComponentBase;

/**
 * A local component that adds a predefined language code to the properties of a
 * document. No real recognition is performed.
 * 
 * <p>
 * The component implements {@link RawDocumentsConsumer} and
 * {@link RawDocumentsProducer}, so basically, it acts as a filter between two
 * other components.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class RawDocumentDummyLanguageDetection extends
    ProfiledLocalFilterComponentBase implements RawDocumentsProducer,
    RawDocumentsConsumer
{
    /**
     * Name of the context parameter which holds the default language
     * set for all input documents. 
     */
    public static final String PARAM_LANGUAGE_CODE_TO_SET = "lang-code";

    /**
     * Default language code to set if no {@link #PARAM_LANGUAGE_CODE_TO_SET}
     * was provided in the context params. 
     */
    public static final String DEFAULT_LANGUAGE_CODE_TO_SET = "en";

    /**
     * Capabilities exposed by this component.
     */
    private static final Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object []
        {
            RawDocumentsProducer.class, RawDocumentsConsumer.class
        }));

    /**
     * Capabilities required of the successor of this component.
     */
    private static final Set CAPABILITIES_SUCCESSOR = new HashSet(Arrays
        .asList(new Object []
        {
            RawDocumentsConsumer.class,
        }));

    /**
     * Capabilities required of the predecessor of this component.
     */
    private static final Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object []
        {
            RawDocumentsProducer.class,
        }));

    /**
     * The successor component, consumer of documents accepted by this
     * component.
     */
    private RawDocumentsConsumer rawDocumentConsumer;

    /** Language code to be added to the documents */
    private final String languageCode;

    /** 
     * Language code set for the duration of one query (either an override
     * using {@link #PARAM_LANGUAGE_CODE_TO_SET}, taken from the context,
     * or the {@link #languageCode} set in the constructor). 
     */
    private String currentLanguageCode;

    /**
     * @param languageCode
     */
    public RawDocumentDummyLanguageDetection(String languageCode)
    {
        this.languageCode = languageCode;
    }

    /**
     * New document to process: identify language and add a new property to the
     * document.
     * 
     * @see org.carrot2.core.clustering.RawDocumentsConsumer#addDocument(org.carrot2.core.clustering.RawDocument)
     */
    public void addDocument(RawDocument doc) throws ProcessingException
    {
        startTimer();
        doc.setProperty(RawDocument.PROPERTY_LANGUAGE, currentLanguageCode);
        stopTimer();

        // pass the document reference...
        this.rawDocumentConsumer.addDocument(doc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.profiling.ProfiledLocalFilterComponentBase#startProcessing(org.carrot2.core.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        // If there's an override of the default language in the context,
        // use it. Otherwise, take the default set in the constructor.
        final String languageCodeForQuery = (String) requestContext
            .getRequestParameters().get(PARAM_LANGUAGE_CODE_TO_SET);
        if (languageCodeForQuery != null) {
            this.currentLanguageCode = languageCodeForQuery;
        } else {
            this.currentLanguageCode = this.languageCode;
        }
        super.startProcessing(requestContext);
    }

    /**
     * Sets the successor component for the duration of the current request. The
     * component should implement <code>RawDocumentsConsumer</code> interface.
     * 
     * @param next The successor component.
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);

        if (next instanceof RawDocumentsConsumer)
        {
            this.rawDocumentConsumer = (RawDocumentsConsumer) next;
        }
        else
        {
            throw new IllegalArgumentException("Successor should implement: "
                + RawDocumentsConsumer.class.getName());
        }
    }

    /**
     * Performs a cleanup before the object is reused.
     * 
     * @see LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        this.rawDocumentConsumer = null;
        this.currentLanguageCode = null;
    }

    /*
     * @see org.carrot2.core.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    /*
     * @see org.carrot2.core.LocalComponent#getRequiredPredecessorCapabilities()
     */
    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    /*
     * @see org.carrot2.core.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return CAPABILITIES_SUCCESSOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#getName()
     */
    public String getName()
    {
        return "Dummy Language Guesser";
    }
}
