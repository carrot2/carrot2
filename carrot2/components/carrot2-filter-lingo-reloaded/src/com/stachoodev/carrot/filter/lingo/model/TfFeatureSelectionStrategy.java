/*
 * DfFeatureSelectionStrategy.java
 * 
 * Created on 2004-05-14
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.common.*;

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

        // Create the final list
        ArrayList list = new ArrayList(tokenFrequencies.values());
        Comparator comparator = PropertyHelper.getComparatorForDoubleProperty(
                ExtendedToken.PROPERTY_TF, true);
        Collections.sort(list, comparator);

        // A fake ExtendedToken we will use to binary-search the token list
        ExtendedToken thresholdToken = new ExtendedToken(null);
        thresholdToken
                .setDoubleProperty(ExtendedToken.PROPERTY_TF, tfThreshold);

        // NB: binarySearch requires that the list be sorted ascendingly, but
        // as long the supplied comparator is consistent with the list's
        // ordering the method will work as expected
        int index = Collections.binarySearch(list, thresholdToken, comparator);

        if (index < 0)
        {
            index = -index;
        } else
        {
            while (((ExtendedToken) list.get(index))
                    .getDoubleProperty(ExtendedToken.PROPERTY_TF) >= tfThreshold)
            {
                index++;
            }
        }

        return list.subList(0, index - 1);
    }
}