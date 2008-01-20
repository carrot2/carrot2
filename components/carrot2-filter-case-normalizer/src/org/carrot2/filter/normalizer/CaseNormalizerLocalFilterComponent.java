
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

package org.carrot2.filter.normalizer;

import java.util.*;


import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.profiling.*;

/**
 * Brings the case of all tokens in all input tokenized documents's titles and
 * snippets to one common form. This process can be thought of as 'stemming for
 * case'. Two different alorithms can be used here, see implementations of
 * {@link org.carrot2.filter.normalizer.CaseNormalizer}. 
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
     * Creates the case normalizer filter with given implementation of
     */
    public CaseNormalizerLocalFilterComponent(CaseNormalizer caseNormalizer)
    {
        this.caseNormalizer = caseNormalizer;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#init(org.carrot2.core.LocalControllerContext)
     */
    public void init(LocalControllerContext context)
        throws InstantiationException
    {
        super.init(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.clustering.TokenizedDocumentsConsumer#addDocument(org.carrot2.core.clustering.TokenizedDocument)
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
     * @see org.carrot2.core.LocalComponent#endProcessing()
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
     * @see org.carrot2.core.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return CAPABILITIES_SUCCESSOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#getRequiredPredecessorCapabilities()
     */
    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalFilterComponent#setNext(org.carrot2.core.LocalComponent)
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        tokenizedDocumentsConsumer = (TokenizedDocumentsConsumer) next;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#flushResources()
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
     * @see org.carrot2.core.LocalComponent#getName()
     */
    public String getName()
    {
        return "Case Normalizer";
    }
}