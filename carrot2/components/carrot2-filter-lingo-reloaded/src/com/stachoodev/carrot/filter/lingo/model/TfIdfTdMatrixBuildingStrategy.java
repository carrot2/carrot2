/*
 * TfTdMatrixBuildingStrategy.java Created on 2004-05-21
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;

import cern.colt.matrix.*;

/**
 * Note: Overwrites the IDF property of the selected tokens.
 * 
 * @author stachoo
 */
public class TfIdfTdMatrixBuildingStrategy implements TdMatrixBuildingStrategy
{

    /** Matrix factory */
    private DoubleFactory2D doubleFactory2D;
    private static final DoubleFactory2D DEFAULT_DOUBLE_FACTORY_2D = NNIDoubleFactory2D.nni;

    /** */
    private double titleTokenTfMultiplier;
    private static double DEFAULT_TITLE_TOKEN_TF_MULTIPLIER = 2.5;

    /**
     * Creates a TfTdMatrixBuildingStrategy that uses
     * {@link DoubleFactoy2D.sparse}to create the term-document matrix.
     */
    public TfIdfTdMatrixBuildingStrategy()
    {
        this(DEFAULT_TITLE_TOKEN_TF_MULTIPLIER);
    }

    /**
     * @param titleTokenTfMultiplier
     */
    public TfIdfTdMatrixBuildingStrategy(double titleTokenTfMultiplier)
    {
        this(titleTokenTfMultiplier, DEFAULT_DOUBLE_FACTORY_2D);
    }

    /**
     * @param factory2D
     */
    public TfIdfTdMatrixBuildingStrategy(double titleTokenTfMultiplier,
        DoubleFactory2D doubleFactory2D)
    {
        this.titleTokenTfMultiplier = titleTokenTfMultiplier;
        this.doubleFactory2D = doubleFactory2D;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.filter.lingo.model.TdMatrixBuildingStrategy#getTDMatrix(java.util.List,
     *      java.util.List)
     */
    public DoubleMatrix2D getTdMatrix(List tokenizedDocuments,
        List selectedFeatures)
    {
        HashMap tokenFrequencies = new HashMap();
        HashSet tokensUsed = new HashSet();

        // Calculate document frequency
        for (int d = 0; d < tokenizedDocuments.size(); d++)
        {
            TokenizedDocument document = (TokenizedDocument) tokenizedDocuments
                .get(d);

            // Count document frequencies
            tokensUsed.clear();
            ModelUtils.addToFrequencyMap(document.getTitle(),
                ExtendedToken.PROPERTY_DF, tokenFrequencies, 1, tokensUsed,
                (short)0);
            ModelUtils.addToFrequencyMap((TokenSequence) document
                .getProperty(TokenizedDocument.PROPERTY_SNIPPET),
                ExtendedToken.PROPERTY_DF, tokenFrequencies, 1, tokensUsed,
                (short)0);
        }

        // Calculate the idf factor
        for (Iterator terms = selectedFeatures.iterator(); terms.hasNext();)
        {
            ExtendedToken selectedToken = (ExtendedToken) terms.next();
            ExtendedToken token = (ExtendedToken) tokenFrequencies
                .get(selectedToken.toString());

            if (token != null)
            {
                selectedToken.setDoubleProperty(ExtendedToken.PROPERTY_IDF,
                    Math.log(tokenizedDocuments.size()
                        / token.getDoubleProperty(ExtendedToken.PROPERTY_DF, 0)));
            }
            else
            {
                selectedToken.setDoubleProperty(ExtendedToken.PROPERTY_IDF, 0);
            }
        }

        // Get TF term-document matrix from the TdMatrixBuildingStrategy
        TdMatrixBuildingStrategy tdMatrixBuildingStrategy = new TfTdMatrixBuildingStrategy(
            titleTokenTfMultiplier, doubleFactory2D);
        DoubleMatrix2D tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(
            tokenizedDocuments, selectedFeatures);

        // Multiply the elements by the idf factor
        for (int d = 0; d < tokenizedDocuments.size(); d++)
        {
            for (int t = 0; t < selectedFeatures.size(); t++)
            {
                tdMatrix.setQuick(t, d, tdMatrix.getQuick(t, d)
                    * ((ExtendedToken) selectedFeatures.get(t))
                        .getDoubleProperty(ExtendedToken.PROPERTY_IDF, 0));

            }
        }

        return tdMatrix;
    }
}