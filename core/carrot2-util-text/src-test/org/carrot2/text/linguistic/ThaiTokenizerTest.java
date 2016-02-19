
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
import org.carrot2.core.Platform;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.lucene.ThaiTokenizerAdapter;
import org.junit.Assume;
import org.junit.Test;

/**
 * Test cases for {@link ThaiTokenizerAdapter}. Test strings taken from Lucene's
 * TestThaiAnalyzer.
 */
public class ThaiTokenizerTest extends TokenizerTestBase
{
    @Override
    protected ITokenizer createTokenStream() throws IOException
    {
        return new DefaultTokenizerFactory().getTokenizer(LanguageCode.THAI);
    }

    @Test
    public void testThaiTermTokens()
    {
        Assume.assumeTrue(Platform.getPlatform() != Platform.DOTNET);
        Assume.assumeTrue(ThaiTokenizerAdapter.platformSupportsThai());
        assertEqualTokens(
            "การที่ได้ต้องแสดงว่างานดี",
            tokens(ITokenizer.TT_TERM, "การ", "ที่", "ได้", "ต้อง", "แสดง", "ว่า", "งาน", "ดี"));
    }

    @Test
    public void testThaiEnglishTermTokens()
    {
        Assume.assumeTrue(Platform.getPlatform() != Platform.DOTNET);
        Assume.assumeTrue(ThaiTokenizerAdapter.platformSupportsThai());
        assertEqualTokens("ประโยคว่า The quick brown",
            tokens(ITokenizer.TT_TERM, "ประโยค", "ว่า", "The", "quick", "brown"));
    }

    @Test
    public void testNumericTokens()
    {
        Assume.assumeTrue(Platform.getPlatform() != Platform.DOTNET);
        Assume.assumeTrue(ThaiTokenizerAdapter.platformSupportsThai());
        assertEqualTokens("๑๒๓", tokens(ITokenizer.TT_TERM, "๑๒๓"));
    }
}
