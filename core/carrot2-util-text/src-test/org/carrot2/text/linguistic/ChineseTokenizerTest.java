
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import java.io.IOException;

import org.carrot2.core.LanguageCode;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.lucene.ChineseTokenizerAdapter;
import org.junit.Test;

/**
 * Test cases for {@link ChineseTokenizerAdapter}.
 */
public class ChineseTokenizerTest extends TokenizerTestBase
{
    @Override
    protected ITokenizer createTokenStream() throws IOException
    {
        return new DefaultTokenizerFactory()
            .getTokenizer(LanguageCode.CHINESE_SIMPLIFIED);
    }

    @Test
    public void testTermTokens()
    {
        String test = "东亚货币贬值";
        TokenImage [] tokens =
        {
            term("东亚"), term("货币"), term("贬值"),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void testChineseEnglishTermTokens()
    {
        String test = "test 东亚货币贬值 English";
        TokenImage [] tokens =
        {
            term("test"), term("东亚"), term("货币"), term("贬值"), term("english")
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void testJunkTokens()
    {
        final String [] junkTokens = new String []
        {
            ",", ".", "<", ">", "?", "/", "\\", "|", "-", "_", "+", "=", "*", "&", "^",
            "%", "#", "@", "!", "~", "`", ";", ":", "'", "\"", "(", ")", "$", "·", "‘",
            "’", "…", "`", "’", "“", "”", "‘", "—"
        };

        TokenImage [] tokens =
        {
            punctuation(","),
        };
        for (String junkToken : junkTokens)
        {
            assertEqualTokens(junkToken, tokens);
        }
    }

    @Test
    public void testPunctuationTokens()
    {
        String test = "东亚货币贬值。周小燕老师，您辛苦了！";
        TokenImage [] tokens =
        {
            term("东亚"), term("货币"), term("贬值"), punctuation(","), term("周"), term("小"),
            term("燕"), term("老师"), punctuation(","), term("您"), term("辛苦"), term("了"),
            punctuation(","),
        };

        assertEqualTokens(test, tokens);
    }

    @Test
    public void testNumericTokens()
    {
        String test = "湖南１１个部门";
        TokenImage [] tokens =
        {
            term("湖南"), numeric("11"), term("个"), term("部门"),
        };

        assertEqualTokens(test, tokens);
    }
}
