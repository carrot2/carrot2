
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic.lucene;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cn.smart.HMMChineseTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.ExceptionUtils;

/**
 * 
 */
public final class ChineseTokenizerAdapter implements ITokenizer
{
    private final static Pattern numeric = Pattern
        .compile("[\\-+'$]?\\d+([:\\-/,.]?\\d+)*[%$]?");

    private Tokenizer sentenceTokenizer;
    private CharTermAttribute term = null;

    private final MutableCharArray tempCharSequence;

    public ChineseTokenizerAdapter()
    {
        this.tempCharSequence = new MutableCharArray(new char [0]);
        this.sentenceTokenizer = new HMMChineseTokenizer();
    }

    public short nextToken() throws IOException
    {
        final boolean hasNextToken = sentenceTokenizer.incrementToken();
        if (hasNextToken)
        {
            short flags = 0;
            final char [] image = term.buffer();
            final int length = term.length();
            tempCharSequence.reset(image, 0, length);
            if (length == 1 && image[0] == ',')
            {
                // ChineseTokenizer seems to convert all punctuation to ','
                // characters
                flags = ITokenizer.TT_PUNCTUATION;
            }
            else if (numeric.matcher(tempCharSequence).matches())
            {
                flags = ITokenizer.TT_NUMERIC;
            }
            else
            {
                flags = ITokenizer.TT_TERM;
            }
            return flags;
        }

        return ITokenizer.TT_EOF;
    }

    public void setTermBuffer(MutableCharArray array)
    {
        array.reset(term.buffer(), 0, term.length());
    }

    public void reset(Reader input) throws IOException
    {
        try
        {
            if (sentenceTokenizer != null)
            {
                sentenceTokenizer.end();
                sentenceTokenizer.close();
            }

            sentenceTokenizer.setReader(input);
            this.term = sentenceTokenizer.addAttribute(CharTermAttribute.class);
            sentenceTokenizer.reset();
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }
    }
}
