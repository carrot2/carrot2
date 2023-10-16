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

import static org.assertj.core.api.Assertions.assertThat;
import static org.carrot2.text.preprocessing.PreprocessingContextAssert.assertThat;
import static org.carrot2.text.preprocessing.PreprocessingContextAssert.tokens;
import static org.carrot2.text.preprocessing.PreprocessingContextBuilder.FieldValue.fv;

import org.carrot2.TestBase;
import org.carrot2.language.Tokenizer;
import org.junit.Test;

/** Test cases for {@link Tokenizer}. */
public class TokenizerTest extends TestBase {
  @Test
  public void testNoDocuments() {
    PreprocessingContext ctx =
        new PreprocessingContextBuilder().buildContext(new BasicPreprocessingPipeline());

    assertThat(ctx)
        .tokenAt(0)
        .hasImage(null)
        .hasDocIndex(-1)
        .hasFieldIndex(-1)
        .hasExactTokenType(Tokenizer.TF_TERMINATOR);
  }

  @Test
  public void testEmptyDocuments() {
    PreprocessingContext ctx =
        new PreprocessingContextBuilder()
            .newDoc(null, null)
            .newDoc("", "")
            .buildContext(new BasicPreprocessingPipeline());

    assertThat(ctx)
        .tokenAt(0)
        .hasImage(null)
        .hasDocIndex(-1)
        .hasFieldIndex(-1)
        .hasExactTokenType(Tokenizer.TF_SEPARATOR_DOCUMENT);
    assertThat(ctx)
        .tokenAt(1)
        .hasImage(null)
        .hasDocIndex(-1)
        .hasFieldIndex(-1)
        .hasExactTokenType(Tokenizer.TF_TERMINATOR);
  }

  @Test
  public void testEmptyFirstField() {
    PreprocessingContext ctx =
        new PreprocessingContextBuilder()
            .newDoc(null, "a")
            .buildContext(new BasicPreprocessingPipeline());

    assertThat(ctx)
        .tokenAt(0)
        .hasImage("a")
        .hasDocIndex(0)
        .hasFieldIndex(0)
        .hasExactTokenType(Tokenizer.TT_TERM);
    assertThat(ctx)
        .tokenAt(1)
        .hasImage(null)
        .hasDocIndex(-1)
        .hasFieldIndex(-1)
        .hasExactTokenType(Tokenizer.TF_TERMINATOR);
  }

  @Test
  public void testEmptyField() {
    PreprocessingContext ctx =
        new PreprocessingContextBuilder()
            .newDoc(fv("field1", "data mining"), fv("field2", ""), fv("field3", "web site"))
            .buildContext(new BasicPreprocessingPipeline());

    assertThat(ctx.allFields.name).containsOnly("field1", "field3");

    assertThat(ctx)
        .tokenAt(0)
        .hasImage("data")
        .hasDocIndex(0)
        .hasFieldIndex(0)
        .hasExactTokenType(Tokenizer.TT_TERM);
    assertThat(ctx)
        .tokenAt(1)
        .hasImage("mining")
        .hasDocIndex(0)
        .hasFieldIndex(0)
        .hasExactTokenType(Tokenizer.TT_TERM);
    assertThat(ctx)
        .tokenAt(2)
        .hasImage(null)
        .hasDocIndex(0)
        .hasFieldIndex(-1)
        .hasExactTokenType(Tokenizer.TF_SEPARATOR_FIELD);
    assertThat(ctx)
        .tokenAt(3)
        .hasImage("web")
        .hasDocIndex(0)
        .hasFieldIndex(1)
        .hasExactTokenType(Tokenizer.TT_TERM);
    assertThat(ctx)
        .tokenAt(4)
        .hasImage("site")
        .hasDocIndex(0)
        .hasFieldIndex(1)
        .hasExactTokenType(Tokenizer.TT_TERM);
    assertThat(ctx)
        .tokenAt(5)
        .hasImage(null)
        .hasDocIndex(-1)
        .hasFieldIndex(-1)
        .hasExactTokenType(Tokenizer.TF_TERMINATOR);
  }

  @Test
  public void testOneDocument() {
    PreprocessingContext ctx =
        new PreprocessingContextBuilder()
            .newDoc("data mining", "web site")
            .buildContext(new BasicPreprocessingPipeline());

    assertThat(ctx)
        .tokenAt(0)
        .hasImage("data")
        .hasDocIndex(0)
        .hasFieldIndex(0)
        .hasExactTokenType(Tokenizer.TT_TERM);
    assertThat(ctx)
        .tokenAt(1)
        .hasImage("mining")
        .hasDocIndex(0)
        .hasFieldIndex(0)
        .hasExactTokenType(Tokenizer.TT_TERM);
    assertThat(ctx)
        .tokenAt(2)
        .hasImage(null)
        .hasDocIndex(0)
        .hasFieldIndex(-1)
        .hasExactTokenType(Tokenizer.TF_SEPARATOR_FIELD);
    assertThat(ctx)
        .tokenAt(3)
        .hasImage("web")
        .hasDocIndex(0)
        .hasFieldIndex(1)
        .hasExactTokenType(Tokenizer.TT_TERM);
    assertThat(ctx)
        .tokenAt(4)
        .hasImage("site")
        .hasDocIndex(0)
        .hasFieldIndex(1)
        .hasExactTokenType(Tokenizer.TT_TERM);
    assertThat(ctx)
        .tokenAt(5)
        .hasImage(null)
        .hasDocIndex(-1)
        .hasFieldIndex(-1)
        .hasExactTokenType(Tokenizer.TF_TERMINATOR);
  }

  @Test
  public void testSentenceSeparator() {
    PreprocessingContext ctx =
        new PreprocessingContextBuilder()
            .newDoc("data . mining", "")
            .buildContext(new BasicPreprocessingPipeline());

    assertThat(ctx)
        .tokenAt(0)
        .hasImage("data")
        .hasDocIndex(0)
        .hasFieldIndex(0)
        .hasExactTokenType(Tokenizer.TT_TERM);

    assertThat(ctx)
        .tokenAt(1)
        .hasImage(".")
        .hasDocIndex(0)
        .hasFieldIndex(0)
        .hasExactTokenType(Tokenizer.TF_SEPARATOR_SENTENCE | Tokenizer.TT_PUNCTUATION);

    assertThat(ctx)
        .tokenAt(2)
        .hasImage("mining")
        .hasDocIndex(0)
        .hasFieldIndex(0)
        .hasExactTokenType(Tokenizer.TT_TERM);

    assertThat(ctx)
        .tokenAt(3)
        .hasImage(null)
        .hasDocIndex(-1)
        .hasFieldIndex(-1)
        .hasExactTokenType(Tokenizer.TF_TERMINATOR);
  }

  @Test
  public void testMoreDocuments() {
    PreprocessingContext ctx =
        new PreprocessingContextBuilder()
            .newDoc("data mining", "web site")
            .newDoc("artificial intelligence", "ai")
            .newDoc("test", "test")
            .buildContext(new BasicPreprocessingPipeline());

    assertThat(tokens(ctx).stream().map(t -> t.getTokenImage()))
        .containsExactly(
            "data",
            "mining",
            null,
            "web",
            "site",
            null,
            "artificial",
            "intelligence",
            null,
            "ai",
            null,
            "test",
            null,
            "test",
            null);

    assertThat(ctx.allTokens.documentIndex)
        .containsExactly(0, 0, 0, 0, 0, -1, 1, 1, 1, 1, -1, 2, 2, 2, -1);

    assertThat(ctx.allTokens.type)
        .containsExactly(
            new short[] {
              Tokenizer.TT_TERM, Tokenizer.TT_TERM, Tokenizer.TF_SEPARATOR_FIELD,
              Tokenizer.TT_TERM, Tokenizer.TT_TERM, Tokenizer.TF_SEPARATOR_DOCUMENT,
              Tokenizer.TT_TERM, Tokenizer.TT_TERM, Tokenizer.TF_SEPARATOR_FIELD,
              Tokenizer.TT_TERM, Tokenizer.TF_SEPARATOR_DOCUMENT, Tokenizer.TT_TERM,
              Tokenizer.TF_SEPARATOR_FIELD, Tokenizer.TT_TERM, Tokenizer.TF_TERMINATOR
            });

    assertThat(ctx.allTokens.fieldIndex)
        .containsExactly(0, 0, -1, 1, 1, -1, 0, 0, -1, 1, -1, 0, -1, 1, -1);
  }

  @Test
  public void testUnicodeNextLine() {
    PreprocessingContext ctx =
        new PreprocessingContextBuilder()
            .newDoc("Foo\u0085 Bar")
            .buildContext(new BasicPreprocessingPipeline());

    assertThat(tokens(ctx).stream().map(t -> t.getTokenImage()))
        .containsExactly("Foo", "Bar", null);

    assertThat(ctx.allTokens.type)
        .containsExactly(
            new short[] {Tokenizer.TT_TERM, Tokenizer.TT_TERM, Tokenizer.TF_TERMINATOR});
  }
}
