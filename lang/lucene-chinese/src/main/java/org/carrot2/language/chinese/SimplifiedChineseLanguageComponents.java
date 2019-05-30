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
package org.carrot2.language.chinese;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.cn.smart.HMMChineseTokenizer;
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
public class SimplifiedChineseLanguageComponents extends LanguageComponentsProviderImpl {
  public static final String NAME = "Chinese-Simplified";

  public SimplifiedChineseLanguageComponents() {
    super("Carrot2 (Chinese)", NAME);
  }

  @Override
  public Map<Class<?>, Supplier<?>> load(String language, ResourceLookup resourceLookup)
      throws IOException {
    LexicalData lexicalData = loadLexicalData(NAME, resourceLookup);

    LinkedHashMap<Class<?>, Supplier<?>> components = new LinkedHashMap<>();
    components.put(Stemmer.class, (Supplier<Stemmer>) (() -> (word) -> null));
    components.put(Tokenizer.class, ChineseTokenizerAdapter::new);
    components.put(LexicalData.class, () -> lexicalData);
    components.put(LabelFormatter.class, () -> new LabelFormatterImpl(" "));

    return components;
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
