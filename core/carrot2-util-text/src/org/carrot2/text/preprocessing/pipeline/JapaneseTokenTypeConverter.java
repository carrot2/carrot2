package org.carrot2.text.preprocessing.pipeline;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.carrot2.text.analysis.ITokenizer;

public final class JapaneseTokenTypeConverter extends TokenFilter
{
    private final TokenTypeAttribute tokenTypeAtt = addAttribute(TokenTypeAttribute.class);

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
            // TODO: add detection and marking of punctionation and end of sentence markers.
            tokenTypeAtt.setType(ITokenizer.TT_TERM);
        }
        return v;
    }
}
