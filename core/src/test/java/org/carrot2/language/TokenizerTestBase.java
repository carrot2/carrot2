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
import java.io.StringReader;
import java.util.ArrayList;
import org.assertj.core.api.Assertions;
import org.carrot2.TestBase;
import org.carrot2.util.MutableCharArray;

/** A base class for testing Carrot2 tokenizers. */
abstract class TokenizerTestBase extends TestBase {
  /** Creates the Analyzer under tests. */
  protected abstract Tokenizer createTokenStream() throws IOException;

  /** Internal class for comparing sequences of tokens. */
  protected static class TokenImage {
    final int type;
    final String image;

    public TokenImage(String image, int type) {
      this.type = type;
      this.image = image;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof TokenImage) {
        return (((TokenImage) o).image.equals(this.image) && (((TokenImage) o).type == this.type));
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return image != null ? image.hashCode() ^ type : type;
    }

    public String toString() {
      final String rawType = "0x" + Integer.toHexString(type);
      return "[" + rawType + "] " + this.image;
    }
  }

  /** Compare expected and produced token sequences. */
  protected void assertEqualTokens(String testString, TokenImage[] expectedTokens) {
    try {
      final Tokenizer tokenStream = createTokenStream();
      tokenStream.reset(new StringReader(testString));

      final ArrayList<TokenImage> tokens = new ArrayList<>();
      short token;
      MutableCharArray buffer = new MutableCharArray();
      while ((token = tokenStream.nextToken()) >= 0) {
        tokenStream.setTermBuffer(buffer);
        tokens.add(new TokenImage(buffer.toString(), token));
      }

      Assertions.assertThat(tokens).containsExactly(expectedTokens);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected TokenImage term(String image) {
    return new TokenImage(image, Tokenizer.TT_TERM);
  }

  protected TokenImage punctuation(String image) {
    return new TokenImage(image, Tokenizer.TT_PUNCTUATION);
  }

  protected TokenImage sentenceDelimiter(String image) {
    return new TokenImage(image, Tokenizer.TT_PUNCTUATION | Tokenizer.TF_SEPARATOR_SENTENCE);
  }

  protected TokenImage numeric(String image) {
    return new TokenImage(image, Tokenizer.TT_NUMERIC);
  }

  protected TokenImage[] tokens(int type, String... images) {
    final TokenImage[] result = new TokenImage[images.length];

    for (int i = 0; i < images.length; i++) {
      result[i] = new TokenImage(images[i], type);
    }

    return result;
  }
}
