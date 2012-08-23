package org.carrot2.text.preprocessing.pipeline;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.carrot2.text.linguistic.ILexicalData;
import org.carrot2.text.util.MutableCharArray;

/**
 * A filter that sets {@link CommonWordAttribute} attribute if the current
 * token is within a set of stop words.
 */
public final class CommonWordMarkerFilter extends TokenFilter
{
    private final CommonWordAttribute commonWordAtt = addAttribute(CommonWordAttribute.class);
    private final CharTermAttribute termAtt = getAttribute(CharTermAttribute.class);

    private final ILexicalData lexicalData;
    private final MutableCharArray scratch = new MutableCharArray();

    public CommonWordMarkerFilter(TokenStream input, ILexicalData lexicalData)
    {
        super(input);
        this.lexicalData = lexicalData; 
    }

    @Override
    public boolean incrementToken() throws IOException
    {
        final boolean v = input.incrementToken();
        if (v)
        {
            scratch.reset(termAtt);
            commonWordAtt.setCommon(lexicalData.isCommonWord(scratch));
        }
        return v;
    }
}
