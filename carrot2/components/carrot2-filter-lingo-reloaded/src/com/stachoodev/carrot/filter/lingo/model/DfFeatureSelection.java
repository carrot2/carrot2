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
import cern.colt.map.*;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.stachoodev.suffixarrays.wrapper.*;

/**
 * Note: selected features are ordered by their integer codes. 
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class DfFeatureSelection extends FeatureSelectionBase implements
    FeatureSelection
{
    /** */
    public static final String PROPERTY_DF_THRESHOLD = "dfth";
    private static final double DEFAULT_DF_THRESHOLD = 2.0;

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.filter.lingo.model.combo.FeatureSelection#getSelectedFeatures(com.stachoodev.carrot.filter.lingo.model.combo.ModelBuilderContext)
     */
    public List getSelectedFeatures(ModelBuilderContext context)
    {
        double dfTheshold = getDoubleProperty(PROPERTY_DF_THRESHOLD,
            DEFAULT_DF_THRESHOLD);

        TokenizedDocumentsIntWrapper wrapper = context.getIntWrapper();
        int [] intData = wrapper.asIntArray();
        int [] suffixArray = context.getSuffixArray().getSuffixArray();

        IntArrayList selectedCodes = new IntArrayList();
        List selectedFeatures = new ArrayList();

        // Note: although the selection is based on the document frequency
        // the choice of the most frequent original token will still be based
        // on the term frequency
        double tf = 0;
        int prevCode = -1;
        int mostFrequentCode = -1;
        double mostFrequentCodeTf = 0;

        int superPrevCode = -1;

        OpenIntIntHashMap documentIndices = new OpenIntIntHashMap(context
            .getTokenizedDocuments().size());

        for (int i = 0; i < suffixArray.length; i++)
        {
            int documentIndex = wrapper
                .getDocumentIndexForPosition(suffixArray[i]);
            int code = intData[suffixArray[i]];
            int superCode = code & MaskableIntWrapper.SECONDARY_MASK;
            if (!wrapper.isIndexableToken(code))
            {
                continue;
            }

            // Filtering
            TypedToken token = wrapper.getTokenForCode(code);
            if ((token.getType() & DEFAULT_FILTER_MASK) != 0
                || token.toString().length() == 1 || wrapper.isQueryWord(code))
            {
                continue;
            }

            // Handle original tokens
            if (prevCode != code)
            {
                if (tf > mostFrequentCodeTf)
                {
                    mostFrequentCode = prevCode;
                    mostFrequentCodeTf = tf;
                }
                tf = 0;
            }
            // NB: the TfFeatureSelection uses title-weighted selection here,
            // which will skew Df vs. Tf comparisons
            tf++;

            prevCode = code;

            // Handle super (stemmed) tokens
            if (superPrevCode != superCode)
            {
                if (documentIndices.size() >= dfTheshold)
                {
                    addNewSelectedToken(wrapper, selectedCodes,
                        selectedFeatures, mostFrequentCode, superPrevCode,
                        documentIndices);
                }

                mostFrequentCodeTf = 0;
                documentIndices.clear();
            }

            documentIndices.put(documentIndex, documentIndex);
            superPrevCode = superCode;
        }

        // For the last token
        if (tf > mostFrequentCodeTf)
        {
            mostFrequentCode = prevCode;
            mostFrequentCodeTf = tf;
        }

        if (documentIndices.size() >= dfTheshold)
        {
            addNewSelectedToken(wrapper, selectedCodes, selectedFeatures,
                mostFrequentCode, superPrevCode, documentIndices);
        }

        // Sort by df
//        Comparator comparator = PropertyHelper.getComparatorForDoubleProperty(
//            ExtendedToken.PROPERTY_DF, true);
//        Collections.sort(selectedFeatures, comparator);

        // Store selected features in the model builder context
        context.setSelectedFeatureCodes(selectedCodes);

        return selectedFeatures;
    }

    /**
     * @param wrapper
     * @param selectedCodes
     * @param selectedFeatures
     * @param mostFrequentCode
     * @param superPrevCode
     * @param documentIndices
     */
    private void addNewSelectedToken(TokenizedDocumentsIntWrapper wrapper,
        IntArrayList selectedCodes, List selectedFeatures, int mostFrequentCode,
        int superPrevCode, OpenIntIntHashMap documentIndices)
    {
        ExtendedToken extendedToken = new ExtendedToken(wrapper
            .getTokenStemForCode(superPrevCode));
        extendedToken.setDoubleProperty(ExtendedToken.PROPERTY_DF,
            documentIndices.size());
        extendedToken.setProperty(
            ExtendedToken.PROPERTY_MOST_FREQUENT_ORIGINAL_TOKEN, wrapper
                .getTokenForCode(mostFrequentCode));
        selectedFeatures.add(extendedToken);

        selectedCodes.add(superPrevCode);
    }
}