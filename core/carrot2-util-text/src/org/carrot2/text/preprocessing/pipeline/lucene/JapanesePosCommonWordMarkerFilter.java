package org.carrot2.text.preprocessing.pipeline.lucene;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;

public final class JapanesePosCommonWordMarkerFilter extends TokenFilter
{
    private final CommonWordAttribute commonWordAtt = addAttribute(CommonWordAttribute.class);
    private final PartOfSpeechAttribute posAtt = getAttribute(PartOfSpeechAttribute.class);
    private final Set<String> stopPosSet;

    public JapanesePosCommonWordMarkerFilter(TokenStream input, Set<String> stopPosSet)
    {
        super(input);
        this.stopPosSet = stopPosSet; 
    }

    @Override
    public boolean incrementToken() throws IOException
    {
        final boolean v = input.incrementToken();
        if (v)
        {
            commonWordAtt.setCommon(
                commonWordAtt.isCommon() | stopPosSet.contains(posAtt.getPartOfSpeech()));
        }
        return v;
    }
}