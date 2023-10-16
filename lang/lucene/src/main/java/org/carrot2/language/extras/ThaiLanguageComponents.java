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

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.th.ThaiTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.carrot2.language.SingleLanguageComponentsProviderImpl;
import org.carrot2.language.Stemmer;
import org.carrot2.language.Tokenizer;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;
import org.carrot2.util.MutableCharArray;

/** */
public class ThaiLanguageComponents extends SingleLanguageComponentsProviderImpl {
  public static final String NAME = "Thai";

  public ThaiLanguageComponents() {
    super("Carrot2 (" + NAME + " support via Apache Lucene components)", NAME);

    registerResourceless(Tokenizer.class, ThaiTokenizerAdapter::new);
    registerResourceless(LabelFormatter.class, () -> new LabelFormatterImpl(" "));
    registerDefaultLexicalData();
    registerResourceless(Stemmer.class, IdentityStemmer::new);
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
      tokenizer.end();
      tokenizer.close();

      tokenizer.setReader(input);
      this.term = tokenizer.addAttribute(CharTermAttribute.class);
      this.tokenizer.reset();
    }
  }
}
