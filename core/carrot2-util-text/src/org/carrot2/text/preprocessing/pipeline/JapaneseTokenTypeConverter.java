package org.carrot2.text.preprocessing.pipeline;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;
import org.carrot2.text.analysis.ITokenizer;

public final class JapaneseTokenTypeConverter extends TokenFilter
{
    private final TokenTypeAttribute tokenTypeAtt = addAttribute(TokenTypeAttribute.class);
    private final PartOfSpeechAttribute posAtt = getAttribute(PartOfSpeechAttribute.class);

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

            // TODO: Do we need to determine numeric tokens here?
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

            tokenTypeAtt.setType(type);
        }
        return v;
    }
}
