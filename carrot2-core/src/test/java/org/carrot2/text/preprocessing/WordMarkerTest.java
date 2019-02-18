
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import org.carrot2.AbstractTest;
import org.carrot2.language.Tokenizer;
import org.junit.Test;

import static org.carrot2.text.preprocessing.PreprocessingContextAssert.assertThat;

/**
 * Test cases for {@link StopListMarker}.
 */
public class WordMarkerTest extends AbstractTest {
  @Test
  public void testNonStopWords() {
    PreprocessingContext ctx = new PreprocessingContextBuilder()
        .newDoc("data mining", "data mining")
        .buildContext(new BasicPreprocessingPipeline());

    assertThat(ctx).containsWord("data")
        .withExactTokenType(Tokenizer.TT_TERM);
    assertThat(ctx).containsWord("mining")
        .withExactTokenType(Tokenizer.TT_TERM);
  }

  @Test
  public void testStopWords() {
    PreprocessingContext ctx = new PreprocessingContextBuilder()
        .newDoc("this you", "have are")
        .buildContext(new BasicPreprocessingPipeline());

    assertThat(ctx).containsWord("this")
        .withExactTokenType(Tokenizer.TT_TERM | Tokenizer.TF_COMMON_WORD);
    assertThat(ctx).containsWord("you")
        .withExactTokenType(Tokenizer.TT_TERM | Tokenizer.TF_COMMON_WORD);
    assertThat(ctx).containsWord("have")
        .withExactTokenType(Tokenizer.TT_TERM | Tokenizer.TF_COMMON_WORD);
    assertThat(ctx).containsWord("are")
        .withExactTokenType(Tokenizer.TT_TERM | Tokenizer.TF_COMMON_WORD);
  }
}
