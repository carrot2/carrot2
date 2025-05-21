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
package org.carrot2.lucene.analysis;

import java.util.Objects;
import org.carrot2.language.Stemmer;

public class LuceneAccessBypass {
  public static Stemmer getCzechStemmer() {
    return new LuceneStemmerAdapter(new CzechStemmer()::stem);
  }

  public static Stemmer getArabicStemmer() {
    final ArabicStemmer stemmer = new ArabicStemmer();
    final ArabicNormalizer normalizer = new ArabicNormalizer();
    return new LuceneStemmerAdapter(
        (word, len) -> {
          int newLen = normalizer.normalize(word, len);
          newLen = stemmer.stem(word, newLen);
          return newLen;
        });
  }

  public static Stemmer getBrazilianStemmer() {
    return new LuceneStemmerAdapter(new BrazilianStemmerAdapter()::stems, 5);
  }

  public static Stemmer getHindiStemmer() {
    return new LuceneStemmerAdapter(new HindiStemming());
  }

  public static Stemmer getIndonesianStemmer() {
    return new LuceneStemmerAdapter(new IndonesianStemming());
  }

  public static Stemmer getLatvianStemmer() {
    return new LuceneStemmerAdapter(new LatvianStemmer()::stem);
  }

  private static class IndonesianStemming implements LuceneStemmerAdapter.StemmingFunction {
    private final IndonesianStemmer stemmer = new IndonesianStemmer();

    @Override
    public int apply(char[] buffer, int length) {
      return stemmer.stem(buffer, length, true);
    }
  }

  private static class HindiStemming implements LuceneStemmerAdapter.StemmingFunction {
    final IndicNormalizer indicNormalizer = new IndicNormalizer();
    final HindiNormalizer hindiNormalizer = new HindiNormalizer();
    final HindiStemmer hindiStemmer = new HindiStemmer();

    @Override
    public int apply(char[] word, int len) {
      len = indicNormalizer.normalize(word, len);
      len = hindiNormalizer.normalize(word, len);
      len = hindiStemmer.stem(word, len);
      return len;
    }
  }

  private static class BrazilianStemmerAdapter extends BrazilianStemmer {
    public int stems(char[] chars, int len) {
      String word = new String(chars, 0, len);
      String stem = super.stem(word);

      if (stem == null || Objects.equals(word, stem)) {
        return len;
      } else {
        stem.getChars(0, stem.length(), chars, 0);
        return stem.length();
      }
    }
  }

  public static Stemmer getBulgarianStemmer() {
    return new LuceneStemmerAdapter(new BulgarianStemmer()::stem);
  }

  public static Stemmer getGalicianStemmer() {
    return new LuceneStemmerAdapter(new GalicianStemmer()::stem);
  }

  private class GalicianStemming implements LuceneStemmerAdapter.StemmingFunction {
    final GalicianStemmer stemmer = new GalicianStemmer();

    @Override
    public int apply(char[] word, int len) {
      return stemmer.stem(word, len);
    }
  }

  public static Stemmer getGreekStemmer() {
    GreekStemmer stemmer = new GreekStemmer();
    return new LuceneStemmerAdapter(
        new LuceneStemmerAdapter.StemmingFunction() {
          @Override
          public int apply(char[] word, int len) {
            lowerCase(word, len);
            return stemmer.stem(word, len);
          }

          private void lowerCase(char[] word, int len) {
            for (int i = 0; i < len; ) {
              i += Character.toChars(lowerCase(Character.codePointAt(word, i, len)), word, i);
            }
          }

          private int lowerCase(int codepoint) {
            switch (codepoint) {
              /* There are two lowercase forms of sigma:
               *   U+03C2: small final sigma (end of word)
               *   U+03C3: small sigma (otherwise)
               *
               * Standardize both to U+03C3
               */
              case '\u03C2': /* small final sigma */
                return '\u03C3'; /* small sigma */

              /* Some greek characters contain diacritics.
               * This filter removes these, converting to the lowercase base form.
               */

              case '\u0386': /* capital alpha with tonos */
              case '\u03AC': /* small alpha with tonos */
                return '\u03B1'; /* small alpha */

              case '\u0388': /* capital epsilon with tonos */
              case '\u03AD': /* small epsilon with tonos */
                return '\u03B5'; /* small epsilon */

              case '\u0389': /* capital eta with tonos */
              case '\u03AE': /* small eta with tonos */
                return '\u03B7'; /* small eta */

              case '\u038A': /* capital iota with tonos */
              case '\u03AA': /* capital iota with dialytika */
              case '\u03AF': /* small iota with tonos */
              case '\u03CA': /* small iota with dialytika */
              case '\u0390': /* small iota with dialytika and tonos */
                return '\u03B9'; /* small iota */

              case '\u038E': /* capital upsilon with tonos */
              case '\u03AB': /* capital upsilon with dialytika */
              case '\u03CD': /* small upsilon with tonos */
              case '\u03CB': /* small upsilon with dialytika */
              case '\u03B0': /* small upsilon with dialytika and tonos */
                return '\u03C5'; /* small upsilon */

              case '\u038C': /* capital omicron with tonos */
              case '\u03CC': /* small omicron with tonos */
                return '\u03BF'; /* small omicron */

              case '\u038F': /* capital omega with tonos */
              case '\u03CE': /* small omega with tonos */
                return '\u03C9'; /* small omega */

              /* The previous implementation did the conversion below.
               * Only implemented for backwards compatibility with old indexes.
               */

              case '\u03A2': /* reserved */
                return '\u03C2'; /* small final sigma */

              default:
                return Character.toLowerCase(codepoint);
            }
          }
        });
  }
}
