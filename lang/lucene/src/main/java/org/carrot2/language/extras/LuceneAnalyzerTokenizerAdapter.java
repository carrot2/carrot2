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
import java.util.ArrayDeque;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.carrot2.language.Tokenizer;
import org.carrot2.util.MutableCharArray;

public class LuceneAnalyzerTokenizerAdapter implements Tokenizer {
  private static class TokenInfo {
    CharSequence term;
    short type;

    int positionLength;

    public TokenInfo(short type, String term, int positionLength) {
      this.type = type;
      this.term = term;
      this.positionLength = positionLength;
    }
  }

  private static final TokenInfo SENTENCE_SEPARATOR =
      new TokenInfo((short) (Tokenizer.TF_SEPARATOR_SENTENCE | Tokenizer.TT_PUNCTUATION), ".", 1);

  private final ArrayDeque<TokenInfo> queue = new ArrayDeque<>();
  private final Analyzer analyzer;

  private TokenStream ts;
  private PositionIncrementAttribute posIncrAttr;
  private PositionLengthAttribute posLengthAttr;
  private CharTermAttribute charTermAttr;
  private TokenInfo last;

  public LuceneAnalyzerTokenizerAdapter(Analyzer analyzer) {
    this.analyzer = analyzer;
  }

  @Override
  public void reset(Reader reader) throws IOException {
    if (ts != null) {
      ts.end();
      ts.close();
    }

    ts = analyzer.tokenStream("", reader);
    ts.reset();

    posIncrAttr = ts.getAttribute(PositionIncrementAttribute.class);
    charTermAttr = ts.getAttribute(CharTermAttribute.class);

    int position = 0;
    while (ts.incrementToken()) {
      int increment = posIncrAttr != null ? posIncrAttr.getPositionIncrement() : 1;

      if (increment < 0) {
        throw new AssertionError("Unexpected negative position increment: " + increment);
      }

      // There was a gap. Insert a synthetic end-of-sentence token here.
      if (increment > 1 && !queue.isEmpty() && !isSentenceSeparator(queue.peekLast())) {
        queue.addLast(SENTENCE_SEPARATOR);
      }

      position += increment;

      int positionLength = posLengthAttr != null ? posLengthAttr.getPositionLength() : 1;
      TokenInfo current =
          new TokenInfo((short) Tokenizer.TT_TERM, charTermAttr.toString(), positionLength);

      // Tokens at the same position.
      if (increment == 0 && !queue.isEmpty()) {
        if (positionLength < queue.peekLast().positionLength) {
          // Keep the short one in the queue. This should result in the simplest token stream,
          // from which phrases can be recreated by the algorithm.
          queue.removeLast();
          queue.addLast(current);
        }
      } else {
        queue.addLast(current);
      }
    }
    ts.end();
    ts.close();
    ts = null;
  }

  private boolean isSentenceSeparator(TokenInfo ti) {
    return (ti.type & Tokenizer.TF_SEPARATOR_SENTENCE) != 0;
  }

  @Override
  public short nextToken() {
    if (queue.isEmpty()) {
      last = null;
      return Tokenizer.TT_EOF;
    } else {
      return (last = queue.removeFirst()).type;
    }
  }

  @Override
  public void setTermBuffer(MutableCharArray array) {
    array.reset(last.term);
  }
}
