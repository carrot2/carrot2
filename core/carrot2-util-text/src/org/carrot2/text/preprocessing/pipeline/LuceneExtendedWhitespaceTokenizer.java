package org.carrot2.text.preprocessing.pipeline;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.carrot2.text.analysis.ExtendedWhitespaceTokenizer;
import org.carrot2.text.analysis.ExtendedWhitespaceTokenizerImpl;

/**
 * A Lucene {@link Tokenizer} that delegates to Carrot2
 * {@link ExtendedWhitespaceTokenizer} to emit tokens for punctuation. Standard Lucene
 * tokenizers omit punctuation and it's a useful hints for phrase splitting.
 */
public class LuceneExtendedWhitespaceTokenizer extends Tokenizer
{
    private ExtendedWhitespaceTokenizerImpl scanner;

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    private final TokenTypeAttribute tokenTypeAtt = addAttribute(TokenTypeAttribute.class);

    public LuceneExtendedWhitespaceTokenizer(Reader input)
    {
        super(input);
        scanner = new ExtendedWhitespaceTokenizerImpl(input);
    }

    @Override
    public final boolean incrementToken() throws IOException {
      clearAttributes();

        int tokenType = scanner.getNextToken();

        if (tokenType == ExtendedWhitespaceTokenizerImpl.YYEOF) {
          return false;
        }

        posIncrAtt.setPositionIncrement(1);
        termAtt.copyBuffer(scanner.yybuffer(), scanner.yystart(), scanner.yylength());

        final int start = scanner.yychar();
        offsetAtt.setOffset(correctOffset(start), correctOffset(start+termAtt.length()));

        tokenTypeAtt.setType(tokenType);
        return true;
    }
    
    @Override
    public final void end() {
      // set final offset
      int finalOffset = correctOffset(scanner.yychar() + scanner.yylength());
      offsetAtt.setOffset(finalOffset, finalOffset);
    }

    @Override
    public void reset(Reader reader) throws IOException {
      super.reset(reader);
      scanner.yyreset(reader);
    }
}
