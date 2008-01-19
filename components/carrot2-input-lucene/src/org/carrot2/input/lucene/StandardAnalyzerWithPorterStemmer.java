
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * A standard analyzer with Porter stemmer
 * 
 * @author Stanislaw Osinski
 */
public class StandardAnalyzerWithPorterStemmer extends
    StandardAnalyzer
{
    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        return new PorterStemFilter(super.tokenStream(fieldName,
            reader));
    }
}