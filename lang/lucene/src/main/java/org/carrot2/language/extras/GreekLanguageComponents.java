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
package org.carrot2.language.extras;

import org.apache.lucene.analysis.el.GreekStemmer;
import org.carrot2.language.ExtendedWhitespaceTokenizer;
import org.carrot2.language.SingleLanguageComponentsProviderImpl;
import org.carrot2.language.Stemmer;
import org.carrot2.language.Tokenizer;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;

/** */
public class GreekLanguageComponents extends SingleLanguageComponentsProviderImpl {
  public static final String NAME = "Greek";

  public GreekLanguageComponents() {
    super("Carrot2 (" + NAME + " support via Apache Lucene components)", NAME);

    registerResourceless(Tokenizer.class, ExtendedWhitespaceTokenizer::new);
    registerResourceless(LabelFormatter.class, () -> new LabelFormatterImpl(" "));
    registerDefaultLexicalData();
    registerResourceless(
        Stemmer.class,
        () -> {
          GreekStemmer stemmer = new GreekStemmer();
          return new LuceneStemmerAdapter(
              (word, len) -> {
                lowerCase(word, len);
                return stemmer.stem(word, len);
              });
        });
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
}
