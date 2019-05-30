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
package org.carrot2.language.extras;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.lucene.analysis.th.ThaiTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.carrot2.language.LanguageComponentsProviderImpl;
import org.carrot2.language.LexicalData;
import org.carrot2.language.Stemmer;
import org.carrot2.language.Tokenizer;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;
import org.carrot2.util.MutableCharArray;
import org.carrot2.util.ResourceLookup;

/** */
public class ThaiLanguageComponents extends LanguageComponentsProviderImpl {
  public static final String NAME = "Thai";

  public ThaiLanguageComponents() {
    super("Carrot2 (extras)", NAME);
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(String language, ResourceLookup resourceLookup)
      throws IOException {
    LexicalData lexicalData = loadLexicalData(NAME, resourceLookup);

    LinkedHashMap<Class<?>, Supplier<?>> components = new LinkedHashMap<>();
    components.put(Stemmer.class, IdentityStemmer::new);
    components.put(Tokenizer.class, () -> new ThaiTokenizerAdapter());
    components.put(LexicalData.class, () -> lexicalData);
    components.put(LabelFormatter.class, () -> new LabelFormatterImpl(" "));

    return components;
  }

  private static class ThaiTokenizerAdapter implements Tokenizer {
    private CharTermAttribute term = null;

    private final MutableCharArray tempCharSequence = new MutableCharArray();
    private final ThaiTokenizer tokenizer = new ThaiTokenizer();

    public short nextToken() throws IOException {
      final boolean hasNextToken = tokenizer.incrementToken();
      if (hasNextToken) {
        final char[] image = term.buffer();
        final int length = term.length();
        tempCharSequence.reset(image, 0, length);

        return Tokenizer.TT_TERM;
      }

      return Tokenizer.TT_EOF;
    }

    public void setTermBuffer(MutableCharArray array) {
      array.reset(term.buffer(), 0, term.length());
    }

    public void reset(Reader input) throws IOException {
      if (tokenizer != null) {
        tokenizer.end();
        tokenizer.close();
      }

      tokenizer.setReader(input);
      this.term = tokenizer.addAttribute(CharTermAttribute.class);
      this.tokenizer.reset();
    }
  }
}
