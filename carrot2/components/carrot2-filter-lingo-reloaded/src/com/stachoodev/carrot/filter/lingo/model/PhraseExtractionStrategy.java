/*
 * PhraseExtractionStrategy.java Created on 2004-06-15
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

/**
 * Defines an interface of a phrase extraction algorithm. Does not impose any
 * constraints on what the term 'phrase' actually means: it can be anything from
 * a simple ungrammatical sequence of words up to a grammatically well formed
 * phrase.
 * 
 * @author stachoo
 */
public interface PhraseExtractionStrategy
{

    /**
     * For a given list of
     * {@link com.dawidweiss.carrot.core.local.clustering.TokenizedDocument}s
     * returns a list of selected {@link ExtendedTokenSequence}s.
     * 
     * @param tokenizedDocuments a list of
     *            {@link com.dawidweiss.carrot.core.local.clustering.TokenizedDocument}
     *            objects
     * @return list of selected token sequences
     */
    List getExtractedPhrases(List tokenizedDocuments);
}