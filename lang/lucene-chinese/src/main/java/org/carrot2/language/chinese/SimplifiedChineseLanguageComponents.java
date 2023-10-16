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
package org.carrot2.language.chinese;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.cn.smart.HMMChineseTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.carrot2.language.SingleLanguageComponentsProviderImpl;
import org.carrot2.language.Stemmer;
import org.carrot2.language.Tokenizer;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.LabelFormatterImpl;
import org.carrot2.util.MutableCharArray;

/** */
public class SimplifiedChineseLanguageComponents extends SingleLanguageComponentsProviderImpl {
  public static final String NAME = "Chinese-Simplified";

  public SimplifiedChineseLanguageComponents() {
    super("Carrot2 (Simplified Chinese via Apache Lucene components)", NAME);
    registerResourceless(Stemmer.class, () -> (word) -> null);
    registerResourceless(Tokenizer.class, ChineseTokenizerAdapter::new);
    registerResourceless(LabelFormatter.class, () -> new LabelFormatterImpl(""));
    registerDefaultLexicalData();
  }

  private static final class ChineseTokenizerAdapter implements Tokenizer {
    private static final Pattern numeric = Pattern.compile("[\\-+'$]?\\d+([:\\-/,.]?\\d+)*[%$]?");

    private HMMChineseTokenizer tokenizer;
    private CharTermAttribute term;

    private final MutableCharArray tempCharSequence = new MutableCharArray();

    public short nextToken() throws IOException {
      final boolean hasNextToken = tokenizer.incrementToken();
      if (hasNextToken) {
        short flags = 0;
        final char[] image = term.buffer();
        final int length = term.length();
        tempCharSequence.reset(image, 0, length);
        if (length == 1 && image[0] == ',') {
          // ChineseTokenizer seems to convert all punctuation to ','
          // characters
          flags = Tokenizer.TT_PUNCTUATION;
        } else if (numeric.matcher(tempCharSequence).matches()) {
          flags = Tokenizer.TT_NUMERIC;
        } else {
          flags = Tokenizer.TT_TERM;
        }
        return flags;
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
      } else {
        tokenizer = new HMMChineseTokenizer();
        term = tokenizer.addAttribute(CharTermAttribute.class);
      }

      tokenizer.setReader(input);
      tokenizer.reset();
    }
  }
}
