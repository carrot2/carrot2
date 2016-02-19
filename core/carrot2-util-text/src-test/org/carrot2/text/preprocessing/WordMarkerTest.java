
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

package org.carrot2.text.preprocessing;

import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.preprocessing.pipeline.BasicPreprocessingPipeline;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link StopListMarker}.
 */
public class WordMarkerTest extends PreprocessingContextTestBase
{
    PreprocessingContextBuilder contextBuilder;

    @Before
    public void prepareContextBuilder()
    {
        contextBuilder = new PreprocessingContextBuilder()
            .withPreprocessingPipeline(new BasicPreprocessingPipeline());
    }

    // @formatter:off
    
    @Test
    public void testNonStopWords()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc("data mining", "data mining")
            .buildContext();

        assertThat(ctx).containsWord("data")
            .withExactTokenType(ITokenizer.TT_TERM);
        assertThat(ctx).containsWord("mining")
            .withExactTokenType(ITokenizer.TT_TERM);
    }

    @Test
    public void testStopWords()
    {
        PreprocessingContext ctx = contextBuilder
            .newDoc("this you", "have are")
            .buildContext();

        assertThat(ctx).containsWord("this")
            .withExactTokenType(ITokenizer.TT_TERM | ITokenizer.TF_COMMON_WORD);
        assertThat(ctx).containsWord("you")
            .withExactTokenType(ITokenizer.TT_TERM | ITokenizer.TF_COMMON_WORD);
        assertThat(ctx).containsWord("have")
            .withExactTokenType(ITokenizer.TT_TERM | ITokenizer.TF_COMMON_WORD);
        assertThat(ctx).containsWord("are")
            .withExactTokenType(ITokenizer.TT_TERM | ITokenizer.TF_COMMON_WORD);
    }
    
    // @formatter:on
}
