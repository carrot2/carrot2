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

import cern.colt.matrix.*;

/**
 * Note: Overwrites the IDF property of the selected tokens.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class TfIdfTdMatrixBuildingStrategy implements TdMatrixBuildingStrategy
{

    /** Matrix factory */
    private DoubleFactory2D doubleFactory2D;
    public static final DoubleFactory2D DEFAULT_DOUBLE_FACTORY_2D = NNIDoubleFactory2D.nni;

    /** */
    private double titleTokenTfMultiplier;
    public static double DEFAULT_TITLE_TOKEN_TF_MULTIPLIER = 2.5;

    /** */
    private IdfFormula idfFormula;
    public static IdfFormula DEFAULT_IDF_FORMULA = IdfFormula.linear;

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
        this(titleTokenTfMultiplier, doubleFactory2D, DEFAULT_IDF_FORMULA);
    }

    /**
     * @param factory2D
     */
    public TfIdfTdMatrixBuildingStrategy(double titleTokenTfMultiplier,
        DoubleFactory2D doubleFactory2D, IdfFormula idfFormula)
    {
        this.titleTokenTfMultiplier = titleTokenTfMultiplier;
        this.doubleFactory2D = doubleFactory2D;
        this.idfFormula = idfFormula;
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
                (short) 0, false);
            ModelUtils.addToFrequencyMap((TokenSequence) document
                .getProperty(TokenizedDocument.PROPERTY_SNIPPET),
                ExtendedToken.PROPERTY_DF, tokenFrequencies, 1, tokensUsed,
                (short) 0, false);
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
                    idfFormula.compute(token.getDoubleProperty(
                        ExtendedToken.PROPERTY_DF, 0), tokenizedDocuments
                        .size()));

                selectedToken.setDoubleProperty(ExtendedToken.PROPERTY_DF,
                    token.getDoubleProperty(ExtendedToken.PROPERTY_DF, 0));
            }
            else
            {
                selectedToken.setDoubleProperty(ExtendedToken.PROPERTY_IDF, 0);
            }
        }

        // Check if we need to omit any documents because of idf == 0. It may
        // happen that a document contains only terms that have idf == 0, which
        // would result in a column of zeros in the term-document matrix. We
        // want to avoid such anomalies.
        for (Iterator iter = tokenizedDocuments.iterator(); iter.hasNext();)
        {
            TokenizedDocument document = (TokenizedDocument) iter.next();
            boolean omit = checkIfOmit(tokenizedDocuments, tokenFrequencies,
                document.getTitle());
            
            if (omit)
            {
                omit = checkIfOmit(tokenizedDocuments, tokenFrequencies,
                    document.getSnippet());
            }

            if (omit)
            {
                document.setProperty(
                    TdMatrixBuildingStrategy.PROPERTY_DOCUMENT_OMITTED,
                    Boolean.TRUE);
            }
        }

        // Get TF term-document matrix from the TdMatrixBuildingStrategy.
        // Documents that we've just marked as omitted will be omitted
        // additionally to documents omitted because of TF
        TdMatrixBuildingStrategy tdMatrixBuildingStrategy = new TfTdMatrixBuildingStrategy(
            titleTokenTfMultiplier, doubleFactory2D);
        DoubleMatrix2D tdMatrix = tdMatrixBuildingStrategy.getTdMatrix(
            tokenizedDocuments, selectedFeatures);

        // Multiply the elements by the idf factor
        for (int d = 0; d < tdMatrix.columns(); d++)
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

    /**
     * @param tokenizedDocuments
     * @param tokenFrequencies
     * @param tokenSequence
     * @return
     */
    private boolean checkIfOmit(List tokenizedDocuments,
        HashMap tokenFrequencies, TokenSequence tokenSequence)
    {
        boolean omit = true;
        for (int t = 0; t < tokenSequence.getLength(); t++)
        {
            ExtendedToken token = (ExtendedToken) tokenFrequencies
                .get(ModelUtils.getStem((TypedToken) tokenSequence.getTokenAt(t)));

            if (token != null && idfFormula.compute(token.getDoubleProperty(
                ExtendedToken.PROPERTY_DF, 0), tokenizedDocuments.size()) != 0)
            {
                omit = false;
                break;
            }
        }
        return omit;
    }
}