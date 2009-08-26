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
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.text.linguistic.LanguageCode;

/**
 * An analyzer that delegates processing to an appropriate analyzer based on the value of
 * the {@link AttributeNames#ACTIVE_LANGUAGE} attributes. If
 * {@link AttributeNames#ACTIVE_LANGUAGE} is {@link LanguageCode#CHINESE_SIMPLIFIED},
 * {@link ChineseAnalyzer} is used. Otherwise, {@link ExtendedWhitespaceAnalyzer} is used.
 */
public final class ActiveLanguageAnalyzer extends Analyzer
{
    /**
     * A cached instance of {@link ChineseAnalyzer}.
     */
    private ChineseAnalyzer chineseAnalyzer;

    /**
     * A cached instance of {@link ExtendedWhitespaceAnalyzer}
     */
    private ExtendedWhitespaceAnalyzer extendedWhitespaceAnalyzer;

    /**
     * The analyzer to used for the current request, depending on
     * {@link AttributeNames#ACTIVE_LANGUAGE}.
     */
    private Analyzer activeAnalyzer;

    @Override
    public TokenStream tokenStream(String field, Reader reader)
    {
        if (activeAnalyzer == null)
        {
            throw new IllegalStateException("Please set the active language first.");
        }

        return activeAnalyzer.tokenStream(field, reader);
    }

    /**
     * Sets the language to be used by this analyzer. This method must be called before
     * calls to {@link #tokenStream(String, Reader)} are made.
     */
    public void setActiveLanguage(LanguageCode activeLanguage)
    {
        if (activeLanguage == LanguageCode.CHINESE_SIMPLIFIED)
        {
            if (chineseAnalyzer == null)
            {
                chineseAnalyzer = new ChineseAnalyzer();
            }
            activeAnalyzer = chineseAnalyzer;
        }
        else
        {
            if (extendedWhitespaceAnalyzer == null)
            {
                extendedWhitespaceAnalyzer = new ExtendedWhitespaceAnalyzer();
            }
            activeAnalyzer = extendedWhitespaceAnalyzer;
        }
    }
}
