/*
 * DfFeatureSelectionStrategy.java Created on 2004-05-14
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;

/**
 * @author stachoo
 */
public class TfFeatureSelectionStrategy implements FeatureSelectionStrategy
{

    /** */
    private double tfThreshold;
    private static double DEFAULT_TF_THRESHOLD = 2;

    /** */
    private double titleTokenTfMultiplier;
    private static double DEFAULT_TITLE_TOKEN_TF_MULTIPLIER = 2.5;

    /**
     * @param dfThreshold
     */
    public TfFeatureSelectionStrategy()
    {
        this(DEFAULT_TF_THRESHOLD);
    }

    /**
     * @param dfThreshold
     */
    public TfFeatureSelectionStrategy(double dfThreshold)
    {
        this(dfThreshold, DEFAULT_TITLE_TOKEN_TF_MULTIPLIER);
    }

    /**
     * @param dfThreshold
     */
    public TfFeatureSelectionStrategy(double dfThreshold,
        double titleTokenDfMultiplier)
    {
        this.tfThreshold = dfThreshold;
        this.titleTokenTfMultiplier = titleTokenDfMultiplier;
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
                titleTokenTfMultiplier);
            ModelUtils.addToFrequencyMap((TokenSequence) document
                .getProperty(TokenizedDocument.PROPERTY_SNIPPET),
                ExtendedToken.PROPERTY_TF, tokenFrequencies, 1);
        }

        return ModelUtils.frequencyMapToList(tokenFrequencies,
            ExtendedToken.PROPERTY_TF, tfThreshold);
    }
}