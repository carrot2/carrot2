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

import cern.colt.list.*;
import cern.colt.matrix.*;

import com.stachoodev.util.common.*;

/**
 * Note: the matrix is <b>not </b> L2 normalised.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class PhraseMatrixBuilding implements PropertyProvider
{
    /** Handles properties */
    private PropertyHelper propertyHelper = new PropertyHelper();

    /** */
    private IdfFormula idfFormula = IdfFormula.linear;

    /**
     * @return
     */
    public DoubleMatrix2D getPhraseMatrix(ModelBuilderContext context)
    {
        int documentCount = context.getTokenizedDocuments().size();
        int [] selectedFeatureCodes = context.getSelectedFeatureCodes()
            .elements();
        DoubleArrayList documentFrequencies = context.getDocumentFrequencies();
        DoubleArrayList termFrequencies = context.getTermFrequencies();
        List extractedPhraseCodes = context.getExtractedPhraseCodes();

        // Create the phrase entries
        DoubleMatrix2D termPhraseMatrix = NNIDoubleFactory2D.nni.make(
            selectedFeatureCodes.length, selectedFeatureCodes.length
                + extractedPhraseCodes.size());

        // For single terms - just write the identity matrix
        for (int i = 0; i < selectedFeatureCodes.length; i++)
        {
            termPhraseMatrix.setQuick(i, i, 1);
        }

        for (int j = 0; j < extractedPhraseCodes.size(); j++)
        {
            int [] tokenCodes = (int []) extractedPhraseCodes.get(j);

            // Set values and calculate length
            for (int i = 0; i < tokenCodes.length; i++)
            {
                int index = Arrays.binarySearch(selectedFeatureCodes,
                    tokenCodes[i]);

                if (index < 0)
                {
                    // This word of the phase has not been indexed (e.g. because
                    // of high tf threshold)
                    continue;
                }

                double weight = termFrequencies.get(index)
                    * idfFormula.compute(documentFrequencies.get(index),
                        documentCount);
                termPhraseMatrix.setQuick(index, selectedFeatureCodes.length
                    + j, weight);
            }
        }

        return termPhraseMatrix;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#getDoubleProperty(java.lang.String,
     *      double)
     */
    public double getDoubleProperty(String propertyName, double defaultValue)
    {
        return propertyHelper.getDoubleProperty(propertyName, defaultValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#getIntProperty(java.lang.String,
     *      int)
     */
    public int getIntProperty(String propertyName, int defaultValue)
    {
        return propertyHelper.getIntProperty(propertyName, defaultValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#getProperty(java.lang.String)
     */
    public Object getProperty(String propertyName)
    {
        return propertyHelper.getProperty(propertyName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#setDoubleProperty(java.lang.String,
     *      double)
     */
    public Object setDoubleProperty(String propertyName, double value)
    {
        return propertyHelper.setDoubleProperty(propertyName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#setIntProperty(java.lang.String,
     *      int)
     */
    public Object setIntProperty(String propertyName, int value)
    {
        return propertyHelper.setIntProperty(propertyName, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.util.common.PropertyProvider#setProperty(java.lang.String,
     *      java.lang.Object)
     */
    public Object setProperty(String propertyName, Object value)
    {
        return propertyHelper.setProperty(propertyName, value);
    }
}