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
public class TfTdMatrixBuilding extends TdMatrixBuildingBase implements
    TdMatrixBuilding
{
    /** */
    public static final String PROPERTY_TITLE_TF_MULTIPLIER = "ttfm";
    private static final double DEFAULT_TITLE_TF_MULTIPLIER = 2.5;

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.filter.lingo.model.combo.TdMatrixBuilding#getTdMatrix(com.stachoodev.carrot.filter.lingo.model.combo.ModelBuilderContext)
     */
    public DoubleMatrix2D getTdMatrix(ModelBuilderContext context)
    {
        IntArrayList featureCodes = context.getSelectedFeatureCodes();
        if (featureCodes == null)
        {
            throw new IllegalStateException(
                "Feature selection must be first performed on this context");
        }

        double titleMultiplier = getDoubleProperty(
            PROPERTY_TITLE_TF_MULTIPLIER, DEFAULT_TITLE_TF_MULTIPLIER);

        TokenizedDocumentsIntWrapper wrapper = context.getIntWrapper();
        int [] intData = wrapper.asIntArray();
        int [] suffixArray = context.getSuffixArray().getSuffixArray();

        List tokenizedDocuments = context.getTokenizedDocuments();

        DoubleMatrix2D A = NNIDoubleFactory2D.nni.make(featureCodes.size(),
            tokenizedDocuments.size());

        int j = 0;
        for (int i = 0; i < featureCodes.size(); i++)
        {
            int code = featureCodes.get(i);

            // Locate the first occurence of the code in the suffix array
            while (j < suffixArray.length && intData[suffixArray[j]] != code)
            {
                j++;
            }

            // Set appropriate cells in A
            while (j < suffixArray.length && intData[suffixArray[j]] == code)
            {
                int documentIndex = wrapper
                    .getDocumentIndexForPosition(suffixArray[j]);

                if (wrapper.getSegmentForPosition(suffixArray[j]) == TokenizedDocumentsIntWrapper.SEGMENT_TITLE)
                {
                    A.setQuick(i, documentIndex, A.getQuick(i, documentIndex)
                        + titleMultiplier);
                }
                else
                {
                    A.setQuick(i, documentIndex,
                        A.getQuick(i, documentIndex) + 1);
                }
                j++;
            }
        }

        return A;
    }
}