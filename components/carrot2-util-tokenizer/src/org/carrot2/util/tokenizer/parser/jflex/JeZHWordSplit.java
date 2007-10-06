
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

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
