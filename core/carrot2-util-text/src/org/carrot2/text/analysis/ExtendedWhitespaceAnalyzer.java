
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
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
    @Override
    public TokenStream tokenStream(String field, Reader reader)
    {
        return new ExtendedWhitespaceTokenizer(reader);
    }
}
