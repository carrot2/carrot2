/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;

/**
 * Document-frequency term selection strategy, chooses terms that appear in more
 * than a certain number of documents.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class DfFeatureSelectionStrategy implements FeatureSelectionStrategy
{
    /** */
    private double dfThreshold;
    private static double DEFAULT_DF_THRESHOLD = 2;

    /** */
    private double titleTokenDfMultiplier;
    private static double DEFAULT_TITLE_TOKEN_DF_MULTIPLIER = 2.5;

    /** */
    private short filterMask;

    /**
     * @param dfThreshold
     */
    public DfFeatureSelectionStrategy()
    {
        this(DEFAULT_DF_THRESHOLD);
    }

    /**
     * @param dfThreshold
     */
    public DfFeatureSelectionStrategy(double dfThreshold)
    {
        this(dfThreshold, DEFAULT_TITLE_TOKEN_DF_MULTIPLIER);
    }

    /**
     * @param dfThreshold
     */
    public DfFeatureSelectionStrategy(double dfThreshold,
        double titleTokenDfMultiplier)
    {
        this.dfThreshold = dfThreshold;
        this.titleTokenDfMultiplier = titleTokenDfMultiplier;
        this.filterMask = FeatureSelectionStrategy.DEFAULT_FILTER_MASK;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.filter.lingo.model.FeatureSelectionStrategy#getSelectedFeatures()
     */
    public List getSelectedFeatures(List tokenizedDocuments)
    {
        HashMap tokenFrequencies = new HashMap();
        HashSet tokensUsed = new HashSet();

        // For each document
        for (Iterator documents = tokenizedDocuments.iterator(); documents
            .hasNext();)
        {
            TokenizedDocument document = (TokenizedDocument) documents.next();
            tokensUsed.clear();

            ModelUtils.addToFrequencyMap(document.getTitle(),
                ExtendedToken.PROPERTY_DF, tokenFrequencies,
                titleTokenDfMultiplier, tokensUsed, filterMask, true);
            ModelUtils.addToFrequencyMap((TokenSequence) document
                .getProperty(TokenizedDocument.PROPERTY_SNIPPET),
                ExtendedToken.PROPERTY_DF, tokenFrequencies, 1, tokensUsed,
                filterMask, true);
        }

        return ModelUtils.frequencyMapToList(tokenFrequencies,
            ExtendedToken.PROPERTY_DF, dfThreshold);
    }
}