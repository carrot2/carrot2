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

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;

/**
 * Defines an interface of a phrase extraction algorithm. Does not impose any
 * constraints on what the term 'phrase' actually means: it can be anything from
 * a simple ungrammatical sequence of words up to a grammatically well formed
 * phrase.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface PhraseExtraction
{
    /**
     * For given {@link ModelBuilderContext}returns a {@link List}of
     * {@link ExtendedTokenSequence}s.
     * 
     * @param context data source for the extraction process
     * @return list of extracted phrases
     */
    public List getExtractedPhrases(ModelBuilderContext context);
}