/*
 * TfTdMatrixBuildingStrategy.java
 * 
 * Created on 2004-05-21
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;

import cern.colt.matrix.*;

/**
 * @author stachoo
 */
public class TfTdMatrixBuildingStrategy implements TdMatrixBuildingStrategy
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
    public TfTdMatrixBuildingStrategy()
    {
        this(DEFAULT_TITLE_TOKEN_TF_MULTIPLIER);
    }

    /**
     * @param titleTokenTfMultiplier
     */
    public TfTdMatrixBuildingStrategy(double titleTokenTfMultiplier)
    {
        this(titleTokenTfMultiplier, DEFAULT_DOUBLE_FACTORY_2D);
    }

    /**
     * @param factory2D
     */
    public TfTdMatrixBuildingStrategy(double titleTokenTfMultiplier,
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
        DoubleMatrix2D tdMatrix = doubleFactory2D.make(selectedFeatures.size(),
                tokenizedDocuments.size());
        HashMap tokenFrequencies = new HashMap();

        // For each document
        for (int d = 0; d < tokenizedDocuments.size(); d++)
        {
            TokenizedDocument document = (TokenizedDocument) tokenizedDocuments
                    .get(d);

            // Count term frequencies
            tokenFrequencies.clear();
            ModelUtils.addToFrequencyMap(document.getTitle(),
                    ExtendedToken.PROPERTY_TF, tokenFrequencies,
                    titleTokenTfMultiplier);
            ModelUtils.addToFrequencyMap((TokenSequence) document
                    .getProperty(TokenizedDocument.PROPERTY_SNIPPET),
                    ExtendedToken.PROPERTY_TF, tokenFrequencies, 1);

            // Put them into the matrix
            for (int term = 0; term < selectedFeatures.size(); term++)
            {
                ExtendedToken selectedToken = (ExtendedToken) selectedFeatures
                        .get(term);

                if (tokenFrequencies.containsKey(selectedToken.toString()))
                {
                    ExtendedToken extendedToken = (ExtendedToken) tokenFrequencies
                            .get(selectedToken.toString());
                    tdMatrix.set(term, d, extendedToken
                            .getDoubleProperty(ExtendedToken.PROPERTY_TF));
                }
            }
        }

        return tdMatrix;
    }
}