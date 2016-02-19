
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
import org.carrot2.text.linguistic.lucene.HindiStemmerAdapter;
import org.junit.Test;

/**
 * Test cases for {@link HindiStemmerAdapter}. Test strings taken from Lucene's
 * TestThaiAnalyzer.
 */
public class HindiStemmerFactoryTest extends TokenizerTestBase
{
    @Override
    protected ITokenizer createTokenStream() throws IOException
    {
        return new DefaultTokenizerFactory().getTokenizer(LanguageCode.HINDI);
    }

    @Test
    public void testTokens()
    {
        assertEqualTokens(
            "डाटा को कई जगह पर foobar",
            new TokenImage [] {
                term("डाटा"),
                term("को"),
                term("कई"),
                term("जगह"),
                term("पर"),
                term("foobar"),
            });

        assertEqualTokens(
            "रिडनडेंसी कहलाता है । डाटा माइनिंग",
            new TokenImage [] {
                term("रिडनडेंसी"),
                term("कहलाता"),
                term("है"),
                sentenceDelimiter("।"),
                term("डाटा"),
                term("माइनिंग"),
            });        
    }
}
