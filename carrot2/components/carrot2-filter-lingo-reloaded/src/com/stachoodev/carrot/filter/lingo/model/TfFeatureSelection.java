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
public class TfFeatureSelection extends FeatureSelectionBase implements
    FeatureSelection
{
    /** */
    public static final String PROPERTY_TF_THRESHOLD = "tfth";
    private static final double DEFAULT_TF_THRESHOLD = 2.0;

    /** */
    public static final String PROPERTY_TITLE_TF_MULTIPLIER = "ttfm";
    private static final double DEFAULT_TITLE_TF_MULTIPLIER = 2.5;

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.filter.lingo.model.combo.FeatureSelection#getSelectedFeatures(com.stachoodev.carrot.filter.lingo.model.combo.ModelBuilderContext)
     */
    public List getSelectedFeatures(ModelBuilderContext context)
    {
        double titleMultiplier = getDoubleProperty(
            PROPERTY_TITLE_TF_MULTIPLIER, DEFAULT_TITLE_TF_MULTIPLIER);
        double tfTheshold = getDoubleProperty(PROPERTY_TF_THRESHOLD,
            DEFAULT_TF_THRESHOLD);

        TokenizedDocumentsIntWrapper wrapper = context.getIntWrapper();
        int [] intData = wrapper.asIntArray();
        int [] suffixArray = context.getSuffixArray().getSuffixArray();

        IntArrayList selectedCodes = new IntArrayList();
        List selectedFeatures = new ArrayList();

        double tf = 0;
        int prevCode = -1;
        int mostFrequentCode = -1;
        double mostFrequentCodeTf = 0;
        double superTf = 0;
        int superPrevCode = -1;

        OpenIntIntHashMap documentIndices = new OpenIntIntHashMap(context
            .getTokenizedDocuments().size());
        DoubleArrayList documentFrequencies = new DoubleArrayList();
        DoubleArrayList termFrequencies = new DoubleArrayList();

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

            if (wrapper.getSegmentForPosition(suffixArray[i]) == TokenizedDocumentsIntWrapper.SEGMENT_TITLE)
            {
                tf += titleMultiplier;
            }
            else
            {
                tf++;
            }

            prevCode = code;

            // Handle super (stemmed) tokens
            if (superPrevCode != superCode)
            {
                if (superTf >= tfTheshold)
                {
                    addNewSelectedToken(wrapper, selectedCodes,
                        documentFrequencies, termFrequencies, selectedFeatures,
                        mostFrequentCode, superTf, documentIndices.size(),
                        superPrevCode);
                }

                superTf = 0;
                mostFrequentCodeTf = 0;
                documentIndices.clear();
            }

            if (wrapper.getSegmentForPosition(suffixArray[i]) == TokenizedDocumentsIntWrapper.SEGMENT_TITLE)
            {
                superTf += titleMultiplier;
            }
            else
            {
                superTf++;
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

        if (superTf >= tfTheshold)
        {
            addNewSelectedToken(wrapper, selectedCodes, documentFrequencies,
                termFrequencies, selectedFeatures, mostFrequentCode, superTf,
                documentIndices.size(), superPrevCode);
        }

        //        Comparator comparator =
        // PropertyHelper.getComparatorForDoubleProperty(
        //            ExtendedToken.PROPERTY_TF, true);
        //        Collections.sort(selectedFeatures, comparator);

        // Store selected features in the model builder context
        context.setSelectedFeatureCodes(selectedCodes);
        context.setDocumentFrequencies(documentFrequencies);
        context.setTermFrequencies(termFrequencies);

        return selectedFeatures;
    }

    /**
     * @param wrapper
     * @param selectedCodes
     * @param selectedFeatures
     * @param mostFrequentCode
     * @param superTf
     * @param superPrevCode
     */
    private void addNewSelectedToken(TokenizedDocumentsIntWrapper wrapper,
        IntArrayList selectedCodes, DoubleArrayList documentFrequencies,
        DoubleArrayList termFrequencies, List selectedFeatures,
        int mostFrequentCode, double superTf, double superDf, int superPrevCode)
    {
        ExtendedToken extendedToken = new ExtendedToken(wrapper
            .getTokenStemForCode(superPrevCode));
        extendedToken.setDoubleProperty(ExtendedToken.PROPERTY_TF, superTf);
        extendedToken.setDoubleProperty(ExtendedToken.PROPERTY_DF, superDf);
        extendedToken.setProperty(
            ExtendedToken.PROPERTY_MOST_FREQUENT_ORIGINAL_TOKEN, wrapper
                .getTokenForCode(mostFrequentCode));
        selectedFeatures.add(extendedToken);

        selectedCodes.add(superPrevCode);
        documentFrequencies.add(superDf);
        termFrequencies.add(superTf);
    }
}