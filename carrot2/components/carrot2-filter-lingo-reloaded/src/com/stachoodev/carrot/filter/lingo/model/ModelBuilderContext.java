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

import ViolinStrings.*;
import cern.colt.list.*;

import com.stachoodev.suffixarrays.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ModelBuilderContext
{
    /** Source documents */
    private List tokenizedDocuments;

    /** */
    private TokenizedDocumentsIntWrapper intWrapper;

    /** */
    private TokenizedDocumentsIntWrapper intWrapperReversed;

    /** */
    private DualLcpSuffixArray suffixArray;

    /** */
    private DualLcpSuffixArray suffixArrayReversed;

    /** */
    private DualLcpSuffixSortingStrategy dualLcpSuffixSortingStrategy;

    /**
     * Feature selection algorithms leave here a list of selected token codes.
     * The list assumed to be ordered ascendingly by the token code values.
     */
    private IntArrayList selectedFeatureCodes;

    /**
     * Phrase extraction algorithm leaves here a list of extracted phrases. Each
     * entry is an int [] array containing the phrase's word codes.
     */
    private List extractedPhraseCodes;

    /** Document frequencies corresponding to the selected feature codes */
    private DoubleArrayList documentFrequencies;

    /** Term frequencies corresponding to the selected feature codes */
    private DoubleArrayList termFrequencies;

    /**
     *  
     */
    public ModelBuilderContext()
    {
        dualLcpSuffixSortingStrategy = new QSDualLcpSuffixSortingStrategy();
    }

    /**
     * @param tokenizedDocuments
     */
    public void initialize(List tokenizedDocuments)
    {
        this.initialize(tokenizedDocuments, null);
    }

    /**
     * @param tokenizedDocuments
     */
    public void initialize(List tokenizedDocuments, String rawQuery)
    {
        String [] queryWords = null;
        if (rawQuery != null)
        {
            queryWords = Strings.split(Strings.translate(rawQuery,
                "'\"!@#$%^&*()-+[]{}", "", ' ').toLowerCase());
        }

        this.tokenizedDocuments = tokenizedDocuments;

        intWrapper = new TokenizedDocumentsIntWrapper(tokenizedDocuments,
            queryWords);
        intWrapperReversed = intWrapper.shallowCopy();
        intWrapperReversed.reverse();

        // Note: implement a proper suffix sort for better performance?
        suffixArray = dualLcpSuffixSortingStrategy
            .dualLcpSuffixSort(intWrapper);
        suffixArrayReversed = dualLcpSuffixSortingStrategy
            .dualLcpSuffixSort(intWrapperReversed);
    }

    /**
     *  
     */
    public void clear()
    {
        intWrapper = null;
        intWrapperReversed = null;
        suffixArray = null;
        suffixArrayReversed = null;

        selectedFeatureCodes = null;
    }

    /**
     * Returns this ModelBuilderContext's <code>tokenizedDocuments</code>.
     * 
     * @return
     */
    public List getTokenizedDocuments()
    {
        return tokenizedDocuments;
    }

    /**
     * Returns this ModelBuilderContext's <code>intWrapper</code>.
     * 
     * @return
     */
    public TokenizedDocumentsIntWrapper getIntWrapper()
    {
        return intWrapper;
    }

    /**
     * Returns this ModelBuilderContext's <code>intWrapperReversed</code>.
     * 
     * @return
     */
    public TokenizedDocumentsIntWrapper getIntWrapperReversed()
    {
        return intWrapperReversed;
    }

    /**
     * Returns this ModelBuilderContext's <code>suffixArray</code>.
     * 
     * @return
     */
    public DualLcpSuffixArray getSuffixArray()
    {
        return suffixArray;
    }

    /**
     * Returns this ModelBuilderContext's <code>suffixArrayReversed</code>.
     * 
     * @return
     */
    public DualLcpSuffixArray getSuffixArrayReversed()
    {
        return suffixArrayReversed;
    }

    /**
     * Returns this ModelBuilderContext's <code>selectedFeatureCodes</code>.
     * 
     * @return
     */
    public IntArrayList getSelectedFeatureCodes()
    {
        return selectedFeatureCodes;
    }

    /**
     * @param selectedFeatureCodes
     */
    public void setSelectedFeatureCodes(IntArrayList selectedFeatureCodes)
    {
        selectedFeatureCodes.trimToSize();
        this.selectedFeatureCodes = selectedFeatureCodes;
    }

    /**
     * Returns this ModelBuilderContext's <code>documentFrequencies</code>.
     * 
     * @return
     */
    public DoubleArrayList getDocumentFrequencies()
    {
        return documentFrequencies;
    }

    /**
     * Sets this ModelBuilderContext's <code>documentFrequencies</code>.
     * 
     * @param documentFrequencies
     */
    public void setDocumentFrequencies(DoubleArrayList documentFrequencies)
    {
        this.documentFrequencies = documentFrequencies;
    }

    /**
     * Returns this ModelBuilderContext's <code>termFrequencies</code>.
     * 
     * @return
     */
    public DoubleArrayList getTermFrequencies()
    {
        return termFrequencies;
    }

    /**
     * Sets this ModelBuilderContext's <code>termFrequencies</code>.
     * 
     * @param termFrequencies
     */
    public void setTermFrequencies(DoubleArrayList termFrequencies)
    {
        this.termFrequencies = termFrequencies;
    }
    
    /**
     * Returns this ModelBuilderContext's <code>extractedPhraseCodes</code>.
     * 
     * @return 
     */
    public List getExtractedPhraseCodes()
    {
        return extractedPhraseCodes;
    }
    
    /**
     * Sets this ModelBuilderContext's <code>extractedPhraseCodes</code>.
     *
     * @param extractedPhraseCodes
     */
    public void setExtractedPhraseCodes(List extractedPhrases)
    {
        this.extractedPhraseCodes = extractedPhrases;
    }
}