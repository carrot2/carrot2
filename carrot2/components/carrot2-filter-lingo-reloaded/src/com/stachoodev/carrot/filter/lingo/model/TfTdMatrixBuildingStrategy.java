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
 * @author Stanislaw Osinski
 * @version $Revision$
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
        int tdMatrixColumns = 0;

        // For each document
        for (int d = 0; d < tokenizedDocuments.size(); d++)
        {
            TokenizedDocument document = (TokenizedDocument) tokenizedDocuments
                .get(d);

            // TfIdfMatrixBuildingStrategy may be calling us having marked some
            // documents as omitted. Check that first
            if (document.getProperty(PROPERTY_DOCUMENT_OMITTED) != null)
            {
                continue;
            }

            // Count term frequencies
            HashMap tokenFrequencies = new HashMap();
            ModelUtils.addToFrequencyMap(document.getTitle(),
                ExtendedToken.PROPERTY_TF, tokenFrequencies,
                titleTokenTfMultiplier, (short) 0, false);
            ModelUtils.addToFrequencyMap(document.getSnippet(),
                ExtendedToken.PROPERTY_TF, tokenFrequencies, 1, (short) 0, false);

            // Calculate the number of columns for the matrix. Omit each
            // document that would have only zeros in its corresponding column
            // and mark that fact in the document's property.
            boolean omit = true;
            for (Iterator iter = selectedFeatures.iterator(); iter.hasNext();)
            {
                ExtendedToken extendedToken = (ExtendedToken) iter.next();
                if (tokenFrequencies.containsKey(extendedToken.toString())
                    && extendedToken.getDoubleProperty(
                        ExtendedToken.PROPERTY_IDF, -1) != 0)
                {
                    omit = false;
                    break;
                }
            }
            
            if (omit)
            {
                document.setProperty(
                    TdMatrixBuildingStrategy.PROPERTY_DOCUMENT_OMITTED,
                    Boolean.TRUE);
            }
            else
            {
                tdMatrixColumns++;

                // Store the frequencies in the document for a while
                document.setProperty("timbs-temp-freqs", tokenFrequencies);
            }
        }

        // Create the term-document matrix with the desired number of columns
        DoubleMatrix2D tdMatrix = doubleFactory2D.make(selectedFeatures.size(),
            tdMatrixColumns);

        // For each document
        int d = 0;
        for (Iterator iter = tokenizedDocuments.iterator(); iter.hasNext();)
        {
            TokenizedDocument document = (TokenizedDocument) iter.next();
            Map tokenFrequencies = (Map) document
                .getProperty("timbs-temp-freqs");

            if (tokenFrequencies == null)
            {
                // Omitted document
                continue;
            }

            // Remove document frequencies from the document
            document.setProperty("timbs-temp-freqs", null);

            // Put them into the matrix
            for (int term = 0; term < selectedFeatures.size(); term++)
            {
                ExtendedToken selectedToken = (ExtendedToken) selectedFeatures
                    .get(term);

                if (tokenFrequencies.containsKey(selectedToken.toString()))
                {
                    ExtendedToken extendedToken = (ExtendedToken) tokenFrequencies
                        .get(selectedToken.toString());
                    tdMatrix.set(term, d, extendedToken.getDoubleProperty(
                        ExtendedToken.PROPERTY_TF, 0));
                }
            }

            d++;
        }

        // Add TF information to the selected features if it's not there
        for (int r = 0; r < tdMatrix.rows(); r++)
        {
            ExtendedToken extendedToken = (ExtendedToken) selectedFeatures
                .get(r);
            if (extendedToken.getProperty(ExtendedToken.PROPERTY_TF) == null)
            {
                double tf = 0;
                for (int c = 0; c < tdMatrix.columns(); c++)
                {
                    tf += tdMatrix.getQuick(r, c);
                }
                (extendedToken)
                    .setDoubleProperty(ExtendedToken.PROPERTY_TF, tf);
            }
        }

        return tdMatrix;
    }
}