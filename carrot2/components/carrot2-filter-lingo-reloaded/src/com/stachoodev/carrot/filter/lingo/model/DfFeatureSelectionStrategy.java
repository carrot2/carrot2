/*
 * DfFeatureSelectionStrategy.java Created on 2004-05-14
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.common.*;

/**
 * Document-frequency term selection strategy, chooses terms that appear in more
 * than a certain number of documents.
 * 
 * TODO: return not only stems but also the original tokens (like in phrase extraction)
 * 
 * @author stachoo
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
    private static short DEFAULT_FILTER_MASK = TypedToken.TOKEN_FLAG_STOPWORD
            | TypedToken.TOKEN_TYPE_SYMBOL | TypedToken.TOKEN_TYPE_UNKNOWN
            | TypedToken.TOKEN_TYPE_PUNCTUATION;

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
        this.filterMask = DEFAULT_FILTER_MASK;
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
                    titleTokenDfMultiplier, tokensUsed, filterMask);
            ModelUtils.addToFrequencyMap((TokenSequence) document
                    .getProperty(TokenizedDocument.PROPERTY_SNIPPET),
                    ExtendedToken.PROPERTY_DF, tokenFrequencies, 1, tokensUsed,
                    filterMask);
        }

        // Create the final list
        ArrayList list = new ArrayList(tokenFrequencies.values());
        Comparator comparator = PropertyHelper.getComparatorForDoubleProperty(
                ExtendedToken.PROPERTY_DF, true);
        Collections.sort(list, comparator);

        // A fake ExtendedToken we will use to binary-search the token list
        ExtendedToken thresholdToken = new ExtendedToken(null);
        thresholdToken
                .setDoubleProperty(ExtendedToken.PROPERTY_DF, dfThreshold);

        // NB: binarySearch requires that the list be sorted ascendingly, but
        // as long the supplied comparator is consistent with the list's
        // ordering the method will work as expected
        int index = Collections.binarySearch(list, thresholdToken, comparator);

        if (index < 0)
        {
            index = -index;
        }
        else
        {
            while (((ExtendedToken) list.get(index))
                    .getDoubleProperty(ExtendedToken.PROPERTY_DF) >= dfThreshold)
            {
                index++;
            }
        }

        return list.subList(0, index - 1);
    }

}