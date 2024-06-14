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

import java.util.Set;
import org.apache.lucene.analysis.FilteringTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;

public class JapanesePartOfSpeechKeepFilter extends FilteringTokenFilter {

  private final Set<String> keepTags;
  private final PartOfSpeechAttribute posAtt = addAttribute(PartOfSpeechAttribute.class);

  public JapanesePartOfSpeechKeepFilter(TokenStream input, Set<String> keepTags) {
    super(input);
    this.keepTags = keepTags;
  }

  @Override
  protected boolean accept() {
    final String pos = posAtt.getPartOfSpeech();
    return pos != null && keepTags.stream().anyMatch(tag -> pos.startsWith(tag));
  }
}
