
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.tokenizer.parser.jflex;

import java.io.*;

import org.carrot2.core.linguistic.tokens.Token;

/**
 * A wrapper around the {@link JFlexWordBasedParser} that allows custom
 * preprocessing of the input stream. The results of preprocessing is then
 * passed to the {@link JFlexWordBasedParser} for the actual tokenization.
 * 
 * @author Stanislaw Osinski
 * @version $Revision: 1595 $
 */
public abstract class PreprocessedJFlexWordBasedParserBase extends
    JFlexWordBasedParser
{
    /** The JFlex-based tokenizer to handle the complicated stuff */
    private JFlexWordBasedParser tokenizer = new JFlexWordBasedParser();

    public int getNextTokens(Token [] array, int startAt)
    {
        return tokenizer.getNextTokens(array, startAt);
    }

    public int getNextTokens(Token [] array, int [] startPositions, int startAt)
    {
        return tokenizer.getNextTokens(array, startPositions, startAt);
    }

    public void restartTokenizationOn(Reader stream)
    {
        String forPreprocessing = readStringFromReader(stream);
        String preprocessed = preprocess(forPreprocessing);

        tokenizer.restartTokenizationOn(new StringReader(preprocessed));
    }

    public void reuse()
    {
        tokenizer.reuse();
    }

    /**
     * Implement this method to add your custom preprocessing. The underlying
     * tokenizer will interpet the space (' ') character as a word separator and
     * the period character ('.') as a sentence separator.
     * 
     * @param input
     * @return preprocessed input string to be further tokenized by the standard
     *         tokenizer
     */
    public abstract String preprocess(String input);

    private String readStringFromReader(Reader reader)
    {
        StringWriter writer = new StringWriter();
        char [] buffer = new char [256];
        int len = 0;
        try
        {
            while ((len = reader.read(buffer)) > 0)
            {
                writer.write(buffer, 0, len);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Parser exception: "
                + e.getLocalizedMessage(), e);
        }

        return writer.toString();
    }
}