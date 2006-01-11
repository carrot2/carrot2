
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

package com.stachoodev.carrot.filter.normalizer.local;

import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.profiling.*;
import com.stachoodev.carrot.filter.normalizer.*;

/**
 * Brings the case of all tokens in all input tokenized documents's titles and
 * snippets to one common form. This process can be thought of as 'stemming for
 * case'. Two different alorithms can be used here, see implementations of
 * {@link com.stachoodev.carrot.filter.normalizer.CaseNormalizer}. 
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class CaseNormalizerLocalFilterComponent extends
    ProfiledLocalFilterComponentBase implements TokenizedDocumentsConsumer,
    TokenizedDocumentsProducer, LocalFilterComponent
{
    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object []
        { TokenizedDocumentsProducer.class }));

    /** This component's capabilities */
    private final static Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object []
        { TokenizedDocumentsConsumer.class, TokenizedDocumentsProducer.class }));

    /** Capabilities required from the next component in the chain */
    private final static Set CAPABILITIES_SUCCESSOR = new HashSet(Arrays
        .asList(new Object []
        { TokenizedDocumentsConsumer.class }));

    /** Tokenized documents consumer */
    private TokenizedDocumentsConsumer tokenizedDocumentsConsumer;

    /** Normalization engine */
    private CaseNormalizer caseNormalizer;

    /**
     * Creates the case normalizer filter with the default instance of 
     * {@link SimpleCaseNormalizer} algorithm.
     */
    public CaseNormalizerLocalFilterComponent()
    {
        caseNormalizer = new SimpleCaseNormalizer();
    }
    
    /**
     * Creates the case normalizer filter with given implementation of
     */
    public CaseNormalizerLocalFilterComponent(CaseNormalizer caseNormalizer)
    {
        this.caseNormalizer = caseNormalizer;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#init(com.dawidweiss.carrot.core.local.LocalControllerContext)
     */
    public void init(LocalControllerContext context)
        throws InstantiationException
    {
        super.init(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.TokenizedDocumentsConsumer#addDocument(com.dawidweiss.carrot.core.local.clustering.TokenizedDocument)
     */
    public void addDocument(TokenizedDocument doc) throws ProcessingException
    {
        startTimer();
        caseNormalizer.addDocument(doc);
        stopTimer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#endProcessing()
     */
    public void endProcessing() throws ProcessingException
    {
        startTimer();
        List tokenizedDocuments = caseNormalizer.getNormalizedDocuments();
        stopTimer();

        for (Iterator iter = tokenizedDocuments.iterator(); iter.hasNext();)
        {
            TokenizedDocument document = (TokenizedDocument) iter.next();
            tokenizedDocumentsConsumer.addDocument(document);
        }
        super.endProcessing();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return CAPABILITIES_SUCCESSOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredPredecessorCapabilities()
     */
    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalFilterComponent#setNext(com.dawidweiss.carrot.core.local.LocalComponent)
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        tokenizedDocumentsConsumer = (TokenizedDocumentsConsumer) next;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        caseNormalizer.clear();
        tokenizedDocumentsConsumer = null;
        profile = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getName()
     */
    public String getName()
    {
        return "Case Normalizer";
    }
}