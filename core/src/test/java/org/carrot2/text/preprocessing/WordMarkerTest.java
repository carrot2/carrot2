/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.text.preprocessing;

import static org.carrot2.text.preprocessing.PreprocessingContextAssert.assertThat;

import org.carrot2.TestBase;
import org.carrot2.language.Tokenizer;
import org.junit.Test;

/** Test cases for {@link StopListMarker}. */
public class WordMarkerTest extends TestBase {
  @Test
  public void testNonStopWords() {
    PreprocessingContext ctx =
        new PreprocessingContextBuilder()
            .newDoc("data mining", "data mining")
            .buildContext(new BasicPreprocessingPipeline());

    assertThat(ctx).containsWord("data").withExactTokenType(Tokenizer.TT_TERM);
    assertThat(ctx).containsWord("mining").withExactTokenType(Tokenizer.TT_TERM);
  }

  @Test
  public void testStopWords() {
    PreprocessingContext ctx =
        new PreprocessingContextBuilder()
            .newDoc("this you", "have are")
            .buildContext(new BasicPreprocessingPipeline());

    assertThat(ctx)
        .containsWord("this")
        .withExactTokenType(Tokenizer.TT_TERM | Tokenizer.TF_COMMON_WORD);
    assertThat(ctx)
        .containsWord("you")
        .withExactTokenType(Tokenizer.TT_TERM | Tokenizer.TF_COMMON_WORD);
    assertThat(ctx)
        .containsWord("have")
        .withExactTokenType(Tokenizer.TT_TERM | Tokenizer.TF_COMMON_WORD);
    assertThat(ctx)
        .containsWord("are")
        .withExactTokenType(Tokenizer.TT_TERM | Tokenizer.TF_COMMON_WORD);
  }
}
