
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

import org.apache.lucene.analysis.TokenStream;
import org.junit.Test;

/**
 * Test cases for {@link ChineseAnalyzer}.
 */
public class ChineseTokenizerTest extends TokenizerTestBase
{
    private ChineseAnalyzer chineseAnalyzer = new ChineseAnalyzer();
    
    @Override
    protected TokenStream createTokenStream(Reader reader)
    {
        return chineseAnalyzer.tokenStream(null, reader);
    }

    @Test
    public void TERM()
    {
        String test = "东亚货币贬值";
        TokenImage [] tokens =
        {
            term("东亚"),
            term("货币"),
            term("贬值"),
        };

        assertEqualTokens(test, tokens);
    }
    
    @Test
    public void PUNCTUATION()
    {
        String test = "东亚货币贬值。周小燕老师，您辛苦了！";
        TokenImage [] tokens =
        {
            term("东亚"),
            term("货币"),
            term("贬值"),
            punctuation(","),
            term("周"),
            term("小"),
            term("燕"),
            term("老师"),
            punctuation(","),
            term("您"),
            term("辛苦"),
            term("了"),
            punctuation(","),
        };
        
        assertEqualTokens(test, tokens);
    }
    
    @Test
    public void NUMERIC()
    {
        String test = "湖南１１个部门";
        TokenImage [] tokens =
        {
            term("湖南"),
            numeric("11"),
            term("个"),
            term("部门"),
        };
        
        assertEqualTokens(test, tokens);
    }
}
