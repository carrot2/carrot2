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

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class TfIdfTdMatrixBuilding extends TdMatrixBuildingBase implements
    TdMatrixBuilding
{
    /** */
    private TfTdMatrixBuilding tfTdMatrixBuilding = new TfTdMatrixBuilding();

    /** */
    public static final String PROPERTY_TITLE_TF_MULTIPLIER = "ttfm";
    private static final double DEFAULT_TITLE_TF_MULTIPLIER = 2.5;

    /** */
    public static final String PROPERTY_IDF_FORMULA = "idff";
    public static IdfFormula DEFAULT_IDF_FORMULA = IdfFormula.linear;

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.filter.lingo.model.combo.TdMatrixBuilding#getTdMatrix(com.stachoodev.carrot.filter.lingo.model.combo.ModelBuilderContext)
     */
    public DoubleMatrix2D getTdMatrix(ModelBuilderContext context)
    {
        double titleMultiplier = getDoubleProperty(
            PROPERTY_TITLE_TF_MULTIPLIER, DEFAULT_TITLE_TF_MULTIPLIER);
        IdfFormula idfFormula = (IdfFormula) getProperty(PROPERTY_IDF_FORMULA);
        if (idfFormula == null)
        {
            idfFormula = DEFAULT_IDF_FORMULA;
        }

        List tokenizedDocuments = context.getTokenizedDocuments();

        // Build using tf algorithm first
        tfTdMatrixBuilding.setDoubleProperty(
            TfTdMatrixBuilding.PROPERTY_TITLE_TF_MULTIPLIER, titleMultiplier);
        DoubleMatrix2D A = tfTdMatrixBuilding.getTdMatrix(context);

        // Scale by the idf factor
        DoubleArrayList documentFrequencies = context.getDocumentFrequencies();
        for (int r = 0; r < A.rows(); r++)
        {
            double idf = idfFormula.compute(documentFrequencies.get(r),
                tokenizedDocuments.size());

            for (int c = 0; c < A.columns(); c++)
            {
                A.setQuick(r, c, A.getQuick(r, c) * idf);
            }
        }
        
        return A;
    }
}