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
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class TfFeatureSelectionStrategy implements FeatureSelectionStrategy
{

    /** */
    private double tfThreshold;
    private static double DEFAULT_TF_THRESHOLD = 2;

    /** */
    private double titleTokenTfMultiplier;
    private static double DEFAULT_TITLE_TOKEN_TF_MULTIPLIER = 2.5;

    /** */
    private short filterMask;

    /**
     * @param dfThreshold
     */
    public TfFeatureSelectionStrategy()
    {
        this(DEFAULT_TF_THRESHOLD);
    }

    /**
     * @param tfThreshold
     */
    public TfFeatureSelectionStrategy(double tfThreshold)
    {
        this(tfThreshold, DEFAULT_TITLE_TOKEN_TF_MULTIPLIER);
    }

    /**
     * @param tfThreshold
     */
    public TfFeatureSelectionStrategy(double tfThreshold,
        double titleTokenDfMultiplier)
    {
        this.tfThreshold = tfThreshold;
        this.titleTokenTfMultiplier = titleTokenDfMultiplier;
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

        // For each document
        for (Iterator documents = tokenizedDocuments.iterator(); documents
            .hasNext();)
        {
            TokenizedDocument document = (TokenizedDocument) documents.next();

            ModelUtils.addToFrequencyMap(document.getTitle(),
                ExtendedToken.PROPERTY_TF, tokenFrequencies,
                titleTokenTfMultiplier, filterMask, true);
            ModelUtils.addToFrequencyMap((TokenSequence) document
                .getProperty(TokenizedDocument.PROPERTY_SNIPPET),
                ExtendedToken.PROPERTY_TF, tokenFrequencies, 1, filterMask, true);
        }

        return ModelUtils.frequencyMapToList(tokenFrequencies,
            ExtendedToken.PROPERTY_TF, tfThreshold);
    }
}