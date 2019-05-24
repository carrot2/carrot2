/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language.arabic;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.lucene.analysis.ar.ArabicNormalizer;
import org.apache.lucene.analysis.ar.ArabicStemmer;
import org.carrot2.language.ExtendedWhitespaceTokenizer;
import org.carrot2.language.LanguageComponentsProviderImpl;
import org.carrot2.language.LexicalData;
import org.carrot2.language.Stemmer;
import org.carrot2.language.Tokenizer;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;
import org.carrot2.util.MutableCharArray;
import org.carrot2.util.ResourceLookup;

/** */
public class ArabicLanguageComponents extends LanguageComponentsProviderImpl {
  public static final String NAME = "Arabic";

  public ArabicLanguageComponents() {
    super("Carrot2 (" + NAME + ")", NAME);
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(String language, ResourceLookup resourceLookup)
      throws IOException {
    LexicalData lexicalData = loadLexicalData(NAME, resourceLookup);

    LinkedHashMap<Class<?>, Supplier<?>> components = new LinkedHashMap<>();
    components.put(Stemmer.class, () -> new LuceneArabicStemmer());
    components.put(Tokenizer.class, ExtendedWhitespaceTokenizer::new);
    components.put(LexicalData.class, () -> lexicalData);
    components.put(LabelFormatter.class, () -> new LabelFormatterImpl(" "));

    return components;
  }

  private static class LuceneArabicStemmer implements Stemmer {
    final ArabicStemmer stemmer = new ArabicStemmer();
    final ArabicNormalizer normalizer = new ArabicNormalizer();
    char[] buffer = new char[128];

    @Override
    public CharSequence stem(CharSequence word) {
      if (word.length() > buffer.length) {
        buffer = new char[word.length()];
      }

      for (int i = 0; i < word.length(); i++) {
        buffer[i] = word.charAt(i);
      }

      int newLen = normalizer.normalize(buffer, word.length());
      newLen = stemmer.stem(buffer, newLen);

      if (newLen != word.length() || !equals(buffer, newLen, word)) {
        return new MutableCharArray(Arrays.copyOf(buffer, newLen));
      } else {
        return null;
      }
    }

    private boolean equals(char[] buffer, int len, CharSequence word) {
      assert len == word.length();

      for (int i = 0; i < len; i++) {
        if (buffer[i] != word.charAt(i)) {
          return false;
        }
      }

      return true;
    }
  }
}
