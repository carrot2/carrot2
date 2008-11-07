
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

package org.carrot2.text.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

/**
 * An {@link Analyzer} instance tokenizing using {@link ExtendedWhitespaceTokenizer}.
 */
public final class ExtendedWhitespaceAnalyzer extends Analyzer
{
    /*
     * 
     */
    public TokenStream reusableTokenStream(String field, final Reader reader)
    {
        /*
         * Avoid using ThreadLocal in Analyzer so that the context class loader's
         * reference is not stored in the thread.
         * 
         * http://issues.carrot2.org/browse/CARROT-414
         */
        return tokenStream(field, reader);
    }

    /*
     * 
     */
    @Override
    public TokenStream tokenStream(String field, Reader reader)
    {
        return new ExtendedWhitespaceTokenizer(reader);
    }
}
