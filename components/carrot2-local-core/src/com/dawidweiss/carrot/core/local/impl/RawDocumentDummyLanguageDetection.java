package com.dawidweiss.carrot.core.local.impl;

import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.profiling.*;

/**
 * A local component that adds a predefined language code to the properties of a
 * document. No real recognition is performed.
 * 
 * <p>
 * The component implements {@link RawDocumentConsumer} and
 * {@link RawDocumentProducer}, so basically, it acts as a filter between two
 * other components.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class RawDocumentDummyLanguageDetection extends
    ProfiledLocalFilterComponentBase implements RawDocumentsProducer,
    RawDocumentsConsumer
{
    /** */
    public static final String PARAM_LANGUAGE_CODE_TO_SET = "lang-code";

    /** */
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
    private String languageCode;

    /**
     * Creates
     * 
     * @param languageCode
     */
    public RawDocumentDummyLanguageDetection()
    {
    }

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
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocumentsConsumer#addDocument(com.dawidweiss.carrot.core.local.clustering.RawDocument)
     */
    public void addDocument(RawDocument doc) throws ProcessingException
    {
        startTimer();
        doc.setProperty(RawDocument.PROPERTY_LANGUAGE, languageCode);
        stopTimer();

        // pass the document reference...
        this.rawDocumentConsumer.addDocument(doc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.profiling.ProfiledLocalFilterComponentBase#startProcessing(com.dawidweiss.carrot.core.local.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        languageCode = (String) requestContext.getRequestParameters().get(
            PARAM_LANGUAGE_CODE_TO_SET);
        if (languageCode == null)
        {
            languageCode = DEFAULT_LANGUAGE_CODE_TO_SET;
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
     * @see com.dawidweiss.carrot.core.local.LocalComponent.flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        this.rawDocumentConsumer = null;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredPredecessorCapabilities()
     */
    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    /*
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return CAPABILITIES_SUCCESSOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getName()
     */
    public String getName()
    {
        return "Dummy Language Guesser";
    }
}
