/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2021, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language.japanese;

import java.io.IOException;
import org.apache.lucene.analysis.FilteringTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class LengthFilter extends FilteringTokenFilter {

  private final int minCodePointLength;
  private final int maxCodePointLength;
  private final CharTermAttribute charAtt = addAttribute(CharTermAttribute.class);

  public LengthFilter(TokenStream input, int minCodePointLength, int maxCodePointLength) {
    super(input);
    this.minCodePointLength = minCodePointLength;
    this.maxCodePointLength = maxCodePointLength;
  }

  @Override
  protected boolean accept() throws IOException {
    final String term = charAtt.toString();
    final int length = term.codePointCount(0, term.length());
    return length >= minCodePointLength && length <= maxCodePointLength;
  }
}
