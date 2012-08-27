package org.carrot2.text.preprocessing.pipeline;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.carrot2.text.analysis.ITokenizer;

public final class JapaneseTokenTypeConverter extends TokenFilter
{
    private final TokenTypeAttribute tokenTypeAtt = addAttribute(TokenTypeAttribute.class);
    private final PartOfSpeechAttribute posAtt = getAttribute(PartOfSpeechAttribute.class);
    private final CharTermAttribute charTerm = getAttribute(CharTermAttribute.class);

    protected JapaneseTokenTypeConverter(TokenStream input)
    {
        super(input);
    }

    @Override
    public boolean incrementToken() throws IOException
    {
        final boolean v = input.incrementToken();
        if (v)
        {
            final String pos = posAtt.getPartOfSpeech();

            int type = ITokenizer.TT_TERM;
            if (pos.equals("名詞-数"))
            {
                type = ITokenizer.TT_NUMERIC;
            }

            // Check for "symbol" POS prefix.
            if (pos.startsWith("記号"))
            {
                type = ITokenizer.TT_PUNCTUATION;
                if (pos.equals("記号-句点"))
                {
                    type |= ITokenizer.TF_SEPARATOR_SENTENCE;
                }
            }
            else
            {
                // Manually check for punctuation sequences.
                if (charTerm.toString().matches("\\p{Punct}+"))
                {
                    type = ITokenizer.TT_PUNCTUATION;
                }
            }

            tokenTypeAtt.setType(type);
        }
        return v;
    }
}
