/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2025, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.text.preprocessing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.carrot2.clustering.CachedLangComponents;
import org.carrot2.language.TestsLanguageComponentsFactoryVariant2;
import org.junit.Test;

/** Test cases for {@link PhraseExtractor}. */
public class PhraseExtractorTest extends TestBase {
  PreprocessingContextBuilder contextBuilder =
      new PreprocessingContextBuilder(
          CachedLangComponents.loadCached(TestsLanguageComponentsFactoryVariant2.NAME));

  @Test
  public void testEmpty() {
    assertThat(
            contextBuilder.buildContextAssert(new CompletePreprocessingPipeline()).phraseImages())
        .isEmpty();
  }

  @Test
  public void testNullTitleSnippet() {
    PreprocessingContextAssert a =
        contextBuilder.newDoc(null, null).buildContextAssert(new CompletePreprocessingPipeline());
    assertThat(a.phraseImages()).isEmpty();
  }

  @Test
  public void testSinglePhrase() {
    PreprocessingContextAssert a =
        contextBuilder.newDoc("a a", "a a").buildContextAssert(new CompletePreprocessingPipeline());

    a.containsPhrase("a", "a").withTf(2).withDocumentTf(0, 2);
    assertThat(a.wordImages()).containsOnly("a");
  }

  @Test
  public void testTwoPhrasesOneDocument() {
    PreprocessingContextAssert a =
        contextBuilder.newDoc("a b", "a b").buildContextAssert(new CompletePreprocessingPipeline());

    a.containsPhrase("a", "b").withTf(2).withDocumentTf(0, 2);
  }

  @Test
  public void testSubphrasesAcrossFields() {
    PreprocessingContextAssert a =
        contextBuilder
            .newDoc("a b . a b", "a b c d . a b c d")
            .buildContextAssert(new CompletePreprocessingPipeline());

    a.containsPhrase("a", "b").withTf(4);
    a.containsPhrase("b", "c").withTf(2);
    a.containsPhrase("c", "d").withTf(2);
    a.containsPhrase("a", "b", "c").withTf(2);
    a.containsPhrase("b", "c", "d").withTf(2);
    a.containsPhrase("a", "b", "c", "d").withTf(2);
    assertThat(a.phraseImages().size()).isEqualTo(6);
  }

  @Test
  public void testSubphrasesOneField() {
    PreprocessingContextAssert a =
        contextBuilder
            .newDoc("a b c d . a b c d")
            .buildContextAssert(new CompletePreprocessingPipeline());

    a.containsPhrase("a", "b").withTf(2);
    a.containsPhrase("b", "c").withTf(2);
    a.containsPhrase("c", "d").withTf(2);
    a.containsPhrase("a", "b", "c").withTf(2);
    a.containsPhrase("b", "c", "d").withTf(2);
    a.containsPhrase("a", "b", "c", "d").withTf(2);
    assertThat(a.phraseImages().size()).isEqualTo(6);
  }

  @Test
  public void testNestedPhrases() {
    PreprocessingContextAssert a =
        contextBuilder
            .newDoc("a b c d . a b c d", "a b d . a b d")
            .buildContextAssert(new CompletePreprocessingPipeline());

    a.containsPhrase("a", "b").withTf(4);
    a.containsPhrase("b", "c").withTf(2);
    a.containsPhrase("c", "d").withTf(2);
    a.containsPhrase("b", "d").withTf(2);
    a.containsPhrase("a", "b", "c").withTf(2);
    a.containsPhrase("a", "b", "d").withTf(2);
    a.containsPhrase("b", "c", "d").withTf(2);
    a.containsPhrase("a", "b", "c", "d").withTf(2);
    assertThat(a.phraseImages().size()).isEqualTo(8);
  }

  @Test
  public void testMaxPhraseLength() {
    PreprocessingContextAssert a =
        contextBuilder
            .newDoc("a b c d e f g h i", "a b c d e f g h i")
            .buildContextAssert(new CompletePreprocessingPipeline());

    // All subsequences sized 2..MAX_PHRASE_LENGTH.
    List<String> sequence = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i");
    int all = 0;
    for (int len = 2; len <= PhraseExtractor.MAX_PHRASE_LENGTH; len++) {
      for (int pos = 0; pos + len <= sequence.size(); pos++, all++) {
        a.containsPhrase(sequence.subList(pos, pos + len)).withTf(2);
      }
    }
    assertThat(a.phraseImages().size()).isEqualTo(all);
  }

  @Test
  public void testTwoExtendedPhrases() {
    PreprocessingContextAssert a =
        contextBuilder
            .newDoc("a b c", "a b d")
            .buildContextAssert(new CompletePreprocessingPipeline());

    a.containsPhrase("a", "b").withTf(2);
    assertThat(a.phraseImages().size()).isEqualTo(1);
  }

  @Test
  public void testNoFrequentPhrases() {
    PreprocessingContextAssert a =
        contextBuilder
            .newDoc("a b c", "d e f")
            .buildContextAssert(new CompletePreprocessingPipeline());
    assertThat(a.phraseImages()).isEmpty();
  }

  /**
   * For efficiency reasons we don't care about phrases that ARE frequent in general, but do not
   * have at least two occurrences of one specific variant.
   */
  @Test
  public void testGeneralizedPhraseWithSingleOriginals() {
    PreprocessingContextAssert a =
        contextBuilder
            .newDoc("abc bcd", "abd bce")
            .newDoc("abe bcf", "abf bcg")
            .buildContextAssert(new CompletePreprocessingPipeline());

    assertThat(a.phraseImages()).isEmpty();
  }

  /** Same as {@link #testGeneralizedPhraseWithSingleOriginals()}? */
  @Test
  public void testGeneralizedPhrasesWithSingleOriginals() {
    PreprocessingContextAssert a =
        contextBuilder
            .newDoc("abc bcd", "abd bce")
            .newDoc("abe bcf", "abf bcg")
            .newDoc("efg fgh", "efh fgi")
            .newDoc("efi fgj", "efj fgk")
            .buildContextAssert(new CompletePreprocessingPipeline());

    assertThat(a.phraseImages()).isEmpty();
  }

  @Test
  public void testComposition() {
    PreprocessingContextAssert a =
        contextBuilder
            .newDoc("abc bcd cde", "abc bcd cdf")
            .newDoc("abc bcd cdg", "abc bcd cdh")
            .buildContextAssert(new CompletePreprocessingPipeline());

    a.containsPhraseStemmedAs("a__", "b__")
        .withTf(4)
        .withExactDocumentTfs(new int[][] {{0, 2}, {1, 2}});
    assertThat(a.phraseImages().size()).isEqualTo(1);
  }

  @Test
  public void testGeneralizedPhraseWithMultipleOriginals() {
    PreprocessingContextAssert a =
        contextBuilder
            .newDoc("abd bce", "abe bcf")
            .newDoc("abd bce", "abe bcf . abe bcf")
            .newDoc("abc bcd . abc bcd . abc bcd . abc bcd")
            .buildContextAssert(new CompletePreprocessingPipeline());

    a.containsPhraseStemmedAs("a__", "b__")
        .withTf(9)
        .withExactDocumentTfs(new int[][] {{0, 2}, {1, 3}, {2, 4}});
    assertThat(a.phraseImages().size()).isEqualTo(1);
  }

  @Test
  public void testGeneralizedPhraseFrequencyAggregation() {
    PreprocessingContextAssert a =
        contextBuilder
            .newDoc("abc bcd", "abc bcd")
            .newDoc("abd cde", "abd cde . abe bcd . abe bcd . abe bcd")
            .buildContextAssert(new CompletePreprocessingPipeline());

    a.containsPhraseStemmedAs("a__", "b__")
        .withTf(5)
        .withExactDocumentTfs(new int[][] {{0, 2}, {1, 3}});
    a.containsPhraseStemmedAs("a__", "c__").withTf(2).withExactDocumentTfs(new int[][] {{1, 2}});
    assertThat(a.phraseImages().size()).isEqualTo(2);
  }

  @Test
  public void testTermFrequencyAcrossDocuments() {
    PreprocessingContextAssert a =
        contextBuilder
            .newDoc("abc bcd")
            .newDoc("abc bcd cde")
            .newDoc("abc bcd cde")
            .newDoc("abc bcd cde")
            .buildContextAssert(new CompletePreprocessingPipeline());

    a.containsPhraseStemmedAs("a__", "b__")
        .withTf(4)
        .withExactDocumentTfs(new int[][] {{0, 1}, {1, 1}, {2, 1}, {3, 1}});
    a.containsPhraseStemmedAs("b__", "c__")
        .withTf(3)
        .withExactDocumentTfs(new int[][] {{1, 1}, {2, 1}, {3, 1}});
    a.containsPhraseStemmedAs("a__", "b__", "c__")
        .withTf(3)
        .withExactDocumentTfs(new int[][] {{1, 1}, {2, 1}, {3, 1}});
  }

  @Test
  public void testOverlappingGeneralizedPhrase() {
    PreprocessingContextAssert a =
        contextBuilder
            .newDoc("abc bcd cde def", "abd bce")
            .newDoc("abd bce cde deg", "cdf deg efg . abc fgh cde def")
            .buildContextAssert(new CompletePreprocessingPipeline());

    a.containsPhraseStemmedAs("a__", "b__")
        .withTf(2)
        .withExactDocumentTfs(new int[][] {{0, 1}, {1, 1}});
    a.containsPhraseStemmedAs("c__", "d__")
        .withTf(2)
        .withExactDocumentTfs(new int[][] {{0, 1}, {1, 1}});
  }

  @Test
  public void testDfThreshold() {
    CompletePreprocessingPipeline pipeline = new CompletePreprocessingPipeline();
    pipeline.wordDfThreshold.set(2);

    PreprocessingContextAssert a =
        contextBuilder
            .newDoc("a a", "a a")
            .newDoc("a a . b b . c c", "a a . b b")
            .newDoc("a a", "a a . c c")
            .buildContextAssert(pipeline);

    // a a
    // b b -> removed due to dfThreshold
    // c c
    a.containsPhrase("a", "a").withTf(6).withExactDocumentTfs(new int[][] {{0, 2}, {1, 2}, {2, 2}});
    a.containsPhrase("c", "c").withTf(2).withExactDocumentTfs(new int[][] {{1, 1}, {2, 1}});
    assertThat(a.phraseImages().size()).isEqualTo(2);
  }

  @Test
  public void minMaxPhraseLength() {
    checkPhrase("a b c d e f g h i");
  }

  @Test
  public void minMaxPhraseLengthReverseOrder() {
    checkPhrase("i h g f e d c b a");
  }

  private void checkPhrase(String phrase) {
    CompletePreprocessingPipeline pipeline = new CompletePreprocessingPipeline();
    pipeline.wordDfThreshold.set(2);

    PreprocessingContextAssert a =
        contextBuilder.newDoc(phrase, phrase).newDoc(phrase, phrase).buildContextAssert(pipeline);

    // All subsequences sized 2..MAX_PHRASE_LENGTH.
    List<String> sequence = Arrays.asList(phrase.split("\\s"));
    int all = 0;
    for (int len = 2; len <= PhraseExtractor.MAX_PHRASE_LENGTH; len++) {
      for (int pos = 0; pos + len <= sequence.size(); pos++, all++) {
        a.containsPhrase(sequence.subList(pos, pos + len))
            .withTf(4)
            .withDocumentTf(0, 2)
            .withDocumentTf(1, 2);
      }
    }
    Assertions.assertThat(a.phraseImages().size()).isEqualTo(all);
  }

  @Test
  public void tfByDocumentAndTfSanity() {
    String symbols = "abcd";
    for (int reps = 0; reps < 100; reps++) {
      for (int docs = 1 + iterations(1, 10); docs >= 0; docs--) {
        int phraseSize = randomIntBetween(1, PhraseExtractor.MAX_PHRASE_LENGTH + 2);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < phraseSize; i++)
          sb.append(symbols.charAt(randomIntBetween(0, symbols.length() - 1))).append(" ");

        contextBuilder.newDoc(sb.toString(), null);
      }

      CompletePreprocessingPipeline pipeline = new CompletePreprocessingPipeline();
      pipeline.wordDfThreshold.set(2);
      PreprocessingContextAssert a = contextBuilder.buildContextAssert(pipeline);
      a.phraseTfsCorrect();
    }
  }
}
