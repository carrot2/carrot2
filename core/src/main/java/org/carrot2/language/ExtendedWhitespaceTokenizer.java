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
package org.carrot2.language;

import java.io.IOException;
import java.io.Reader;
import org.carrot2.util.MutableCharArray;

/**
 * A tokenizer separating input characters on whitespace, but capable of extracting more complex
 * tokens, such as URLs, e-mail addresses and sentence delimiters.
 */
public final class ExtendedWhitespaceTokenizer implements Tokenizer {
  /** JFlex parser used to split the input into tokens. */
  private final ExtendedWhitespaceTokenizerImpl parser;

  public ExtendedWhitespaceTokenizer() {
    parser = new ExtendedWhitespaceTokenizerImpl((Reader) null);
  }

  /** Reset this tokenizer to start parsing another stream. */
  @Override
  public void reset(Reader input) {
    this.parser.yyreset(input);
  }

  @Override
  public short nextToken() throws IOException {
    final short result = (short) parser.getNextToken();
    return result == ExtendedWhitespaceTokenizerImpl.YYEOF ? Tokenizer.TT_EOF : result;
  }

  @Override
  public void setTermBuffer(MutableCharArray array) {
    array.reset(parser.yybuffer(), parser.yystart(), parser.yylength());
  }
}
