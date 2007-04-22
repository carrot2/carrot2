package org.carrot2.util.tokenizer.parser.jflex;

import java.io.IOException;

import jeasy.analysis.MMAnalyzer;

public class JeZHWordSplit extends PreprocessedJFlexWordBasedParserBase
{
    private MMAnalyzer analyzer = new MMAnalyzer();

    public String preprocess(String input)
    {
        try
        {
            return analyzer.segment(input, " ");
        }
        catch (IOException e)
        {
            throw new RuntimeException(
                "Tokenizer exception: " + e.getMessage(), e);
        }
    }
}
